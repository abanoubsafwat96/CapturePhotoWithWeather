package com.abanoub.photoweather.framework.presentation.features.main

import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.FragmentMainBinding
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class MainFragment @Inject constructor() : BaseFragment<FragmentMainBinding>() {

    override fun bindViews() {

    }

    override fun getLayoutResId(): Int = R.layout.fragment_main
}