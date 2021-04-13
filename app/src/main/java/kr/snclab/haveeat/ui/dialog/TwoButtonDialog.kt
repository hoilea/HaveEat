package kr.snclab.haveeat.ui.dialog

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.NavGraphDirections
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.DialogOneButtonBinding
import kr.snclab.haveeat.databinding.DialogTwoButtonBinding
import kr.snclab.haveeat.ui.BaseDialogFragment
import kr.snclab.haveeat.ui.main.history.HistoryDetailFragment

@AndroidEntryPoint
class TwoButtonDialog : BaseDialogFragment<DialogTwoButtonBinding>() {
    companion object {
        const val ARG_POSITIVE = "positive"
        fun show(fragment: Fragment, @StringRes titleResId: Int, @StringRes messageResId: Int, @StringRes positiveTextResId: Int = 0) {
            findNavController(fragment).navigate(
                NavGraphDirections.actionTwoButtonDialog(titleResId, messageResId, positiveTextResId)
            )
        }
    }

    private val args: TwoButtonDialogArgs by navArgs()

    override fun getLayoutResId(): Int = R.layout.dialog_two_button

    override fun initFragment() {
        bind.title.setText(args.titleResId)
        bind.message.setText(args.messageResId)
        if(args.positiveTextResId != 0) {
            bind.button.setText(args.positiveTextResId)
        }
        bind.button.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ARG_POSITIVE, true
            )
            dismiss()
        }
        bind.cancelButton.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ARG_POSITIVE, false
            )
            dismiss()
        }
    }
}