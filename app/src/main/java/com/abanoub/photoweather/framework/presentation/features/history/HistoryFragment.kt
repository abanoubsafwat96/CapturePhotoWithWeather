package com.abanoub.photoweather.framework.presentation.features.history

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.FragmentHistoryBinding
import com.abanoub.photoweather.framework.presentation.callback.OnItemClickListener
import com.abanoub.photoweather.framework.presentation.enums.ToolbarType
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import com.abanoub.photoweather.framework.utils.FileUtils.getDataFromFile
import com.abanoub.photoweather.framework.utils.navigateSafe
import com.abanoub.photoweather.framework.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryFragment: BaseFragment<FragmentHistoryBinding>() {

    private val clickListener = object :OnItemClickListener<String>{
        override fun onItemClicked(item: String) {
            onItemClick(item)
        }
    }

    override fun bindViews() {
        initUI()
    }

    private fun initUI() {
        mainViewModel.updateToolbarType(ToolbarType.HISTORY)
        setupRecyclerView(getDataFromFile())
    }


    private fun setupRecyclerView(history_list: java.util.ArrayList<String>) {
        if (history_list.size == 0) {
            binding.noData.setVisibility(View.VISIBLE)
        } else
            binding.noData.setVisibility(View.GONE)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = HistoryAdapter(requireContext(), history_list)
        adapter.setClickListener(clickListener)
        binding.recyclerView.adapter = adapter
    }

    fun onItemClick(history_item: String) {
        findNavController().navigateSafe(HistoryFragmentDirections.toFullImage(history_item))
    }

    override fun getLayoutResId(): Int = R.layout.fragment_history
}