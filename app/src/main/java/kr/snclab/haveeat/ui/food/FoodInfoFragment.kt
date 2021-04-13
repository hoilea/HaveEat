package kr.snclab.haveeat.ui.food

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentFoodinfoBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.view.FoodCellType

@AndroidEntryPoint
class FoodInfoFragment : BaseFragment<FragmentFoodinfoBinding>() {

    private val viewModel by viewModels<FoodInfoViewModel>()
    private var searchJob: Job? = null

    private val writeMode: Boolean by lazy {
        try {
            val args: FoodInfoFragmentArgs? by navArgs()
            args?.writeMode == true
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun getLayoutResId() = R.layout.fragment_foodinfo

    override fun initFragment() {
        bind.vm = viewModel

        if (writeMode) {
            bind.viewFoodInfoTitle.text = getString(R.string.foodInfo_title_add)
            bind.viewFoodInfoClose.visibility = View.VISIBLE
            bind.viewFoodInfoClose.setOnSafeClickListener { findNavController().popBackStack() }
        }
        if (bind.viewFoodInfoHistory.adapter == null) {
            bind.viewFoodInfoHistory.adapter =
                HistoryAdapter(viewModel.getHistory(), object : HistoryAdapter.ItemClickListener {
                    override fun onClick(history: String) {
                        viewModel.searchText.postValue(history)
                    }
                })
        }

        if (bind.viewFoodInfoResult.adapter == null) {
            bind.viewFoodInfoResult.adapter = FoodInfoFoodAdapter(
                if (writeMode) FoodCellType.FoodAdd else FoodCellType.FoodInfo,
                findNavController()
            )
        }
        bind.viewFoodInfoSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.addHistory()
                updateHistory()
                search()
            }
            true
        }

        viewModel.searchText.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                search(true)
            } else {
                showHistory()
            }
        }

        viewModel.itemList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                hideHistory()
                (bind.viewFoodInfoResult.adapter as? FoodInfoFoodAdapter)?.setData(it)
            }
        }
    }

    private fun updateHistory() {
        (bind.viewFoodInfoHistory.adapter as? HistoryAdapter)?.let {
            it.setData(viewModel.getHistory())
        }
    }

    private fun showHistory() {
        updateHistory()
        bind.viewFoodInfoHistory.visibility = View.VISIBLE
        bind.viewFoodInfoHistoryHead.visibility = View.VISIBLE
        bind.viewFoodInfoResult.visibility = View.GONE
    }

    private fun hideHistory() {
        bind.viewFoodInfoHistory.visibility = View.GONE
        bind.viewFoodInfoHistoryHead.visibility = View.GONE
        bind.viewFoodInfoResult.visibility = View.VISIBLE
    }

    private fun search(debounced: Boolean = false) {
        searchJob?.cancel()
        if (!debounced) {
            hideKeyboard()
        }
        searchJob = lifecycleScope.launch {
            if (debounced) {
                delay(1000)
            }
            viewModel.search()
        }
    }

}