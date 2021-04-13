package kr.snclab.haveeat.ui.signup

import android.text.Html
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentTerms2Binding
import kr.snclab.haveeat.ui.BaseFragment

@AndroidEntryPoint
class Terms2Fragment : BaseFragment<FragmentTerms2Binding>() {
    private val viewModel by viewModels<SignupViewModel>()
    override fun getLayoutResId() = R.layout.fragment_terms2

    override fun initFragment() {
        bind.vm = viewModel

        viewModel.privacyText.observe(viewLifecycleOwner) {
            bind.privacyText.text = Html.fromHtml(it)
        }

        bind.viewTerms2Close.setOnClickListener {
            findNavController().popBackStack()
        }

        bind.viewTerms2Button.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            viewModel.getPrivacy()
        }
    }
}