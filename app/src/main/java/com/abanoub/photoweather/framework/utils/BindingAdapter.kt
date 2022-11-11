package com.abanoub.photoweather.framework.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
fun ImageView.loadImage(imageUrl: String, placeholder: Drawable?) {
    Glide.with(context)
        .load(imageUrl)
        .placeholder(placeholder)
        .into(this)
}