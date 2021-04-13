package kr.snclab.haveeat.ui.signup

import android.text.Html
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentTerms1Binding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.ui.BaseFragment

@AndroidEntryPoint
class Terms1Fragment : BaseFragment<FragmentTerms1Binding>() {
    private val viewModel by viewModels<SignupViewModel>()
    override fun getLayoutResId() = R.layout.fragment_terms1

    override fun initFragment() {
        bind.vm = viewModel

        viewModel.termsText.observe(viewLifecycleOwner) {
            bind.termsText.text = Html.fromHtml(it)
        }
        bind.viewTerms1Close.setOnClickListener {
            findNavController().popBackStack()
        }

        bind.viewTerms1Button.setOnSafeClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            viewModel.getTerms()
        }
    }
}