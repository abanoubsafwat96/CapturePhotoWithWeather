package com.abanoub.photoweather.framework.utils

import android.os.Environment
import android.util.Log
import com.abanoub.photoweather.framework.utils.Constants.Constants.MEDIA_TYPE_IMAGE
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    //Create a File for saving an image
    fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "PhotoWeatherApp"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PhotoWeatherApp", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File = if (type == MEDIA_TYPE_IMAGE) {
            File(
                mediaStorageDir.path + File.separator +
                        "IMG_" + timeStamp + ".jpg"
            )
        } else {
            return null
        }
        return mediaFile
    }


    fun getDataFromFile(): ArrayList<String> {
        val historyList = ArrayList<String>()
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "PhotoWeatherApp"
        )
        val files = mediaStorageDir.listFiles()
        if (files == null)
            return historyList
        else {
            for (i in files.indices) {
                historyList.add(files[i].path)
            }
        }
        return historyList
    }
}