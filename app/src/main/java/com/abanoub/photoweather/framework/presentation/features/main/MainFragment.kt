package com.abanoub.photoweather.framework.presentation.features.main

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.FragmentMainBinding
import com.abanoub.photoweather.framework.presentation.enums.ToolbarType
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import com.abanoub.photoweather.framework.presentation.services.MyLocationService
import com.abanoub.photoweather.framework.utils.BitmapUtils.rotate
import com.abanoub.photoweather.framework.utils.BitmapUtils.writeTextOnDrawable
import com.abanoub.photoweather.framework.utils.Constants.Constants.MEDIA_TYPE_IMAGE
import com.abanoub.photoweather.framework.utils.Constants.Location.RECEIVER.INTENT_LOCATION
import com.abanoub.photoweather.framework.utils.DataState
import com.abanoub.photoweather.framework.utils.FileUtils.getOutputMediaFile
import com.abanoub.photoweather.framework.utils.isExternalStorageWritable
import com.abanoub.photoweather.framework.utils.isNetworkAvailable
import com.abanoub.photoweather.framework.utils.permissions.Permission
import com.abanoub.photoweather.framework.utils.permissions.PermissionManager
import com.abanoub.photoweather.framework.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class MainFragment @Inject constructor() : BaseFragment<FragmentMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private val permissionManager = PermissionManager.from(this)
    private lateinit var locationBroadcastReceiver: BroadcastReceiver

    private var mCamera: Camera? = null
    var mPreview: CameraPreview? = null
    private var correctCameraOrientation = 0
    var mPicture: PictureCallback? = null


    private var pictureFile: File? = null
    private var realImage: Bitmap? = null
    private var savedImageUri: Uri? = null
    private var weatherData: String? = null
    private var realImageWithWeatherData: Bitmap? = null

    override fun bindViews() {
        initUI()
        initClickListeners()
        subscribeOnObservers()
        registerLocationBroadCastReceiver()
    }

    private fun initClickListeners() {
        binding.buttonCapture.setOnClickListener {
            onCaptureBtnClicked()
        }
        binding.cancel.setOnClickListener {
            onCancelBtnClicked()
        }
        binding.addWeatherData.setOnClickListener {
            onAddWeatherDataBtnClicked()
        }
        binding.done.setOnClickListener {
            onDoneBtnClicked()
        }
        binding.share.setOnClickListener {
            onShareBtnClicked()
        }
    }

    private fun initUI() {
        mainViewModel.updateToolbarType(ToolbarType.MAIN)
        onPictureTaken()
        listenOnBackPress()
    }

    private fun subscribeOnObservers() {
        viewModel.weatherDataState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    weatherData = it.data
                }
                is DataState.Failure -> {
                    activity?.showSnackBar(it.throwable.message.toString())
                }
                is DataState.Loading -> {}
            }
        }
    }

    private fun onCaptureBtnClicked() {
        if (mCamera != null && mPicture != null) {
            try {
                mCamera!!.takePicture(null, null, mPicture) // get an image from the camera
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun onCancelBtnClicked() {
        hidePhotoViewerAndShowCamera()
    }

    private fun onAddWeatherDataBtnClicked() {
        if (isNetworkAvailable(requireContext())) {
            if (realImage != null && weatherData != null) {
                realImage?.let { realCapturedImage ->
                    weatherData?.let { weatherLine ->
                        realImageWithWeatherData =
                            writeTextOnDrawable(requireContext(), realCapturedImage, weatherLine)
                        onAddWeatherDataSuccessed(realImageWithWeatherData)
                    }
                }
            }
        } else {
            errorCheckNetworkConnection()
        }
    }

    private fun onDoneBtnClicked() {
        if (pictureFile != null && realImage != null) {
            if (isExternalStorageWritable()) {
                saveImageToFile()
            }
        }
    }

    private fun onShareBtnClicked() {
        if (savedImageUri != null)
            shareImageUri(savedImageUri)
        else
            errorPhotoNotSaved()
    }

    private fun startCameraCheck() {
        activity?.let {
            //Setup camera in onStart() to get camera back if released in any other app
            if (checkCameraHardware(it)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestCameraPermissionsIfNotGranted()
                else
                    startCameraAndLocation()
            } else
                errorDeviceNotSupportCamera()
        }
    }

    private fun errorDeviceNotSupportCamera() {
        activity?.showSnackBar("Device doesn't support camera")
    }

    private fun requestCameraPermissionsIfNotGranted() {
        permissionManager
            .request(Permission.AppPermissions)
            .rationale(getString(R.string.you_should_grant_permission_to_continue))
            .checkPermission {
                startCameraAndLocation()
            }
    }

    private fun startCameraAndLocation() {
        setupCamera()
        startLocationService()
    }

    private fun onPictureTaken() {
        mPicture = PictureCallback { data, camera ->
            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE)
            if (pictureFile == null) {
                Log.d("Error", "check storage permissions")
                return@PictureCallback
            }
            realImage = BitmapFactory.decodeByteArray(data, 0, data.size)
            //rotate image with same camera angle
            realImage?.let {
                realImage = rotate(it, correctCameraOrientation)
                setBitmapToImageView(realImage)
                hideCameraAndShowPhotoViewer()
            }
        }
    }

    private fun setBitmapToImageView(realImage: Bitmap?) {
        binding.imageView.setImageBitmap(realImage)
    }

    private fun hideCameraAndShowPhotoViewer() {
        binding.cameraContainer.setVisibility(View.GONE)
        binding.buttonCapture.setVisibility(View.GONE)
        binding.imageConstraintLayout.setVisibility(View.VISIBLE)
        if (binding.done.getVisibility() == View.INVISIBLE) binding.done.setVisibility(View.VISIBLE)
        if (binding.addWeatherData.getVisibility() == View.INVISIBLE) binding.addWeatherData.setVisibility(
            View.VISIBLE
        )
    }

    private fun hidePhotoViewerAndShowCamera() {
        binding.imageConstraintLayout.setVisibility(View.GONE)
        binding.cameraContainer.setVisibility(View.VISIBLE)
        binding.buttonCapture.setVisibility(View.VISIBLE)
        if (mPreview != null) {
            binding.cameraContainer.removeView(mPreview)
            binding.cameraContainer.addView(mPreview)
        }
        setRealImageAndSavedImageUriWithNull()
    }

    private fun setRealImageAndSavedImageUriWithNull() {
        realImage = null
        savedImageUri = null
    }

    /* Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun setupCamera() {
        // Create an instance of Camera
        mCamera?.let { camera ->
            setupCameraHelper(camera)
        } ?: assignAndSetupCameraHelper()
    }

    private fun assignAndSetupCameraHelper() {
        mCamera = getCameraInstance()
        mCamera?.let { setupCameraHelper(it) }
    }

    private fun setupCameraHelper(camera: Camera) {
        activity?.let {
            val info = CameraInfo()
            Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info)
            correctCameraOrientation = getCorrectCameraOrientation(it, info)
            camera.setDisplayOrientation(correctCameraOrientation)
            camera.parameters.setRotation(correctCameraOrientation)

            // Create our Preview view and set it as the content of our activity.
            mPreview = CameraPreview(activity, camera)
            addViewToCameraFrameLayout(mPreview)
        }
    }

    private fun addViewToCameraFrameLayout(mPreview: CameraPreview?) {
        binding.cameraContainer.addView(mPreview)
    }

    //A safe way to get an instance of the Camera object.
    private fun getCameraInstance(): Camera? {
        var camera: Camera? = null
        camera?.release()
        try {
            camera = Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }
        return camera // returns null if camera is unavailable
    }

    private fun getCorrectCameraOrientation(activity: Activity, info: CameraInfo): Int {
        val rotation = activity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    private fun saveImageToFile() {
        if (pictureFile != null) {
            try {
                var fos: FileOutputStream? = FileOutputStream(pictureFile)
                if (isAddWeatherDataBtnVisible()) {
                    if (realImage != null) realImage!!.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        fos
                    )
                } else {
                    realImageWithWeatherData?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        ?: return
                }
                savedImageUri = onSavePhotoSuccessfully(pictureFile)
                fos!!.close()
                fos = null
                realImage = null
            } catch (e: FileNotFoundException) {
                Log.d("Info", "File not found: " + e.message)
            } catch (e: IOException) {
                Log.d("TAG", "Error accessing file: " + e.message)
            }
        }
    }

    private fun isAddWeatherDataBtnVisible(): Boolean {
        return binding.addWeatherData.getVisibility() == View.VISIBLE
    }

    private fun onAddWeatherDataSuccessed(realImageWithWeatherData: Bitmap?) {
        binding.imageView.setImageBitmap(realImageWithWeatherData)
        binding.addWeatherData.setVisibility(View.INVISIBLE)
    }

    private fun onSavePhotoSuccessfully(pictureFile: File?): Uri? {
        activity?.showSnackBar("Photo saved successfully")
        binding.done.setVisibility(View.INVISIBLE)
        binding.addWeatherData.setVisibility(View.INVISIBLE)
        return FileProvider.getUriForFile(
            requireContext(),
            "com.abanoub.photoweather.provider",  //(use your app signature + ".provider" )
            pictureFile!!
        )
    }

    private fun shareImageUri(uri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/jpg"
        startActivity(intent)
    }


    private fun errorCheckNetworkConnection() {
        activity?.showSnackBar("Check your network connection")
    }

    private fun errorPhotoNotSaved() {
        activity?.showSnackBar("You must save photo first")
    }

    private fun listenOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
    }

    fun onBackPressed() {
        if (binding.imageConstraintLayout.getVisibility() == View.VISIBLE) {
            hidePhotoViewerAndShowCamera()
        } else{
            closeTheApp()
        }
    }

    private fun closeTheApp() {
        activity?.moveTaskToBack(true)
        activity?.finish()
    }

    private fun startLocationService() {
        activity?.startService(getLocationServiceIntent())
    }

    private fun getLocationServiceIntent() = Intent(context, MyLocationService::class.java)

    private fun registerLocationBroadCastReceiver() {
        locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                getWeatherData(intent)
            }
        }
    }

    private fun getWeatherData(intent: Intent) {
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        viewModel.getWeatherData(latitude, longitude)
    }

    private fun stopLocationService() {
        activity?.stopService(getLocationServiceIntent())
    }

    override fun onStart() {
        startCameraCheck()
        if (::locationBroadcastReceiver.isInitialized)
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                locationBroadcastReceiver, IntentFilter(INTENT_LOCATION)
            )
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (::locationBroadcastReceiver.isInitialized)
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(locationBroadcastReceiver)
    }

    override fun getLayoutResId(): Int = R.layout.fragment_main

    override fun onDestroy() {
        mCamera = null
        realImage = null
        realImageWithWeatherData = null
        stopLocationService()
        permissionManager.clean()
        super.onDestroy()
    }
}