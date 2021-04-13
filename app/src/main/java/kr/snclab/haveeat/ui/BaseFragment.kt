package kr.snclab.haveeat.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kr.snclab.haveeat.ErrorDialogException
import kr.snclab.haveeat.R
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.ui.dialog.Dialog
import retrofit2.HttpException
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    lateinit var bind: T

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun initFragment()
    protected var rootView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        bind.lifecycleOwner = viewLifecycleOwner
        rootView = bind.root
        initFragment()
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bind.unbind()
        hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    fun setTitle(@StringRes resId: Int) {
        activity?.findViewById<AppCompatTextView>(R.id.view_title_message)?.setText(resId)
//        (activity as AppCompatActivity).supportActionBar?.apply {
//            show()
//            title = getString(resId)
//        }
    }

    fun setTitle(title: String) {
        activity?.findViewById<AppCompatTextView>(R.id.view_title_message)?.text = title
    }

    fun setBackButton(clickListener: View.OnClickListener) {
        bind.root.findViewById<AppCompatImageView>(R.id.view_title_back)?.apply {
            visibility = View.VISIBLE
            setOnSafeClickListener(clickListener)
        }
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        bind.root?.also {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showKeyboard(v: EditText) {
        if (v.isAttachedToWindow) {
            v.isFocusableInTouchMode = true
            v.requestFocus()
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(v, 0)
        } else {
            v.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    v.let {
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        v.isFocusableInTouchMode = true
                        v.requestFocus()
                        val imm =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(v, 0)
                    }
                }
            })
        }
    }

    protected val scopeExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
//        println("Caught $exception")
        Progress.dismiss()
        if (exception is ErrorDialogException) {
            Dialog.show(this, exception.titleResId, exception.messageResId)
        } else {
            Dialog.show(this, R.string.error_title, R.string.error_message)
        }
    }
}