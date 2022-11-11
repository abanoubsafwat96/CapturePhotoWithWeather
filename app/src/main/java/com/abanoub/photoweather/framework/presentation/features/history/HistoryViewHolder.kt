package com.abanoub.photoweather.framework.presentation.features.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abanoub.photoweather.databinding.HistorySingleItemBinding
import com.abanoub.photoweather.framework.presentation.callback.OnItemClickListener

class HistoryViewHolder(private val binding: HistorySingleItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String) {
        binding.item = item
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: OnItemClickListener<String>?
        ): HistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HistorySingleItemBinding.inflate(layoutInflater, parent, false)
            binding.listener = listener
            return HistoryViewHolder(binding)
        }
    }
}