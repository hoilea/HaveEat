package kr.snclab.haveeat.ui.dialog

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.NavGraphDirections
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.DialogOneButtonBinding
import kr.snclab.haveeat.ui.BaseDialogFragment

@AndroidEntryPoint
class Dialog : BaseDialogFragment<DialogOneButtonBinding>() {
    companion object {
        fun show(fragment: Fragment, @StringRes titleResId: Int, @StringRes messageResId: Int) {
            findNavController(fragment).navigate(
                NavGraphDirections.actionDialog(titleResId, messageResId)
            )
        }
        fun error(fragment: Fragment, @StringRes messageResId: Int) {
            findNavController(fragment).navigate(
                NavGraphDirections.actionDialog(R.string.error_title, messageResId)
            )
        }
    }

    private val args: DialogArgs by navArgs()

    override fun getLayoutResId(): Int = R.layout.dialog_one_button

    override fun initFragment() {
        bind.viewDialogTitle.setText(args.titleResId)
        bind.viewDialogMessage.setText(args.messageResId)
        bind.viewDialogButton.setOnClickListener {
            dismiss()
        }
    }
}