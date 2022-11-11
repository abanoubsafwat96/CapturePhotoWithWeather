package com.abanoub.photoweather.framework.presentation.features.fullImage

import androidx.navigation.fragment.navArgs
import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.DialogFullImageBinding
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FullImageDialog @Inject constructor() : BaseFragment<DialogFullImageBinding>() {

    private val args: FullImageDialogArgs by navArgs()

    override fun bindViews() {
        initUI()
    }

    private fun initUI() {
        binding.imageUrl = args.image
    }

    override fun getLayoutResId(): Int = R.layout.dialog_full_image
}