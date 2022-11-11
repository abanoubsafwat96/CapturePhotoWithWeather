package com.abanoub.photoweather.framework.presentation.features.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.abanoub.photoweather.NavigationGraphDirections
import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.ActivityMainBinding
import com.abanoub.photoweather.framework.presentation.enums.ToolbarType
import com.abanoub.photoweather.framework.utils.findNavController
import com.abanoub.photoweather.framework.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private val mainViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUI()
        subscribeOnObservers()
    }

    private fun subscribeOnObservers() {
        mainViewModel.toolbarTypeLiveData.observe(this) {
            when (it) {
                ToolbarType.MAIN -> binding.toolbar.historyTV.visibility = View.VISIBLE
                ToolbarType.HISTORY ->  binding.toolbar.historyTV.visibility = View.INVISIBLE
                else -> {}
            }
        }
    }

    private fun initUI() {
        binding.lifecycleOwner = this
        binding.toolbar.historyTV.setOnClickListener {
            findNavController()?.navigateSafe(NavigationGraphDirections.toHistory())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}