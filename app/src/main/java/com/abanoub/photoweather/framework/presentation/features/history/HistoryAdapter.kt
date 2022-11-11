package com.abanoub.photoweather.framework.presentation.features.history

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abanoub.photoweather.framework.presentation.callback.OnItemClickListener

internal class HistoryAdapter(var context: Context, var history_list: ArrayList<String>) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    var listener: OnItemClickListener<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(history_list[position])
    }

    override fun getItemCount(): Int {
        return history_list.size
    }

    fun setClickListener(communicator: OnItemClickListener<String>?) {
        this.listener = communicator
    }
}