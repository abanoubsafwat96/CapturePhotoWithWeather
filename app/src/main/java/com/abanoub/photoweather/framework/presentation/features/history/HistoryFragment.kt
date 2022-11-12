package com.abanoub.photoweather.framework.presentation.features.history

import androidx.navigation.fragment.findNavController
import com.abanoub.photoweather.R
import com.abanoub.photoweather.databinding.FragmentHistoryBinding
import com.abanoub.photoweather.framework.presentation.callback.OnItemClickListener
import com.abanoub.photoweather.framework.presentation.enums.ToolbarType
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import com.abanoub.photoweather.framework.utils.FileUtils.getDataFromFile
import com.abanoub.photoweather.framework.utils.navigateSafe
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

    private fun setupRecyclerView(history_list: ArrayList<String>) {
        binding.isEmpty = history_list.size == 0
        val adapter = HistoryAdapter(requireContext(), history_list)
        adapter.setClickListener(clickListener)
        binding.recyclerView.adapter = adapter
    }

    fun onItemClick(historyItem: String) {
        findNavController().navigateSafe(HistoryFragmentDirections.toFullImage(historyItem))
    }

    override fun getLayoutResId(): Int = R.layout.fragment_history
}