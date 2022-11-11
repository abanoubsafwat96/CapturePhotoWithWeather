package com.abanoub.photoweather.framework.presentation.features.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abanoub.photoweather.framework.presentation.enums.ToolbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    val toolbarTypeLiveData: MutableLiveData<ToolbarType> = MutableLiveData()

    fun updateToolbarType(toolbarType: ToolbarType) {
        toolbarTypeLiveData.value = toolbarType
    }
}