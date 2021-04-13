package kr.snclab.haveeat.ui.signup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.snclab.haveeat.ErrorDialogException
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.CellSignupCheckBinding
import kr.snclab.haveeat.databinding.CellSignupInfoBinding
import kr.snclab.haveeat.databinding.CellSignupTermsBinding
import kr.snclab.haveeat.extension.isValidEmail
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.ui.Progress
import retrofit2.HttpException

class SignupPagerAdapter(
    private val viewModel: SignupViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val signupInterface: SignupInterface
) : RecyclerView.Adapter<SignupPagerAdapter.ClipListHolder>() {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ClipListHolder {
//        val type = SignupPagerType.values().getOrElse(viewType){
//            SignupPagerType.Terms
//        }
//        val view = when(type) {
//            SignupPagerType.Terms -> ViewSignupTerms(group.context).apply { setViewModel(viewModel) }
//            SignupPagerType.Info -> ViewSignupInfo(group.context).apply { setViewModel(viewModel) }
//            SignupPagerType.Check -> ViewSignupCheck(group.context).apply { setViewModel(viewModel) }
//        }

        val view = SignupPagerType.values().getOrElse(viewType) { SignupPagerType.Terms }.let {
            LayoutInflater.from(group.context).inflate(it.layoutId, group, false)
        }
        return ClipListHolder(view)
    }

    override fun onBindViewHolder(holder: ClipListHolder, position: Int) {
        holder.bindTo()
    }

    override fun getItemCount() = SignupPagerType.values().size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: ClipListHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class ClipListHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
        fun bindTo() {
            when (binding) {
                is CellSignupTermsBinding -> {
                    bindCellSignupTerms(binding)
                }
                is CellSignupInfoBinding -> {
                    bindCellSignupInfo(binding)
                }
                is CellSignupCheckBinding -> {
                    bindCellSignupCheck(binding)
                }
            }

            binding.lifecycleOwner = lifecycleOwner
        }

        fun clear() {
        }

        private fun bindCellSignupTerms(bind: CellSignupTermsBinding) {
            bind.vm = viewModel
            viewModel.terms1.observe(lifecycleOwner) {
                bind.viewSignupTermsButton.isEnabled = viewModel.terms1.value == true && viewModel.terms2.value == true
            }
            viewModel.terms2.observe(lifecycleOwner) {
                bind.viewSignupTermsButton.isEnabled = viewModel.terms1.value == true && viewModel.terms2.value == true
            }
            bind.viewSignupTermsButton.isEnabled = false

            bind.viewSignupTermsShow1.setOnSafeClickListener {
                signupInterface.showTerms1()
            }
            bind.viewSignupTermsShow2.setOnSafeClickListener {
                signupInterface.showTerms2()
            }
            bind.viewSignupTermsButton.setOnSafeClickListener {
                viewModel.moveInfo()
            }
        }

        private fun bindCellSignupInfo(bind: CellSignupInfoBinding) {
            bind.vm = viewModel
            bind.viewSignupInfoButton.isEnabled = false

            bind.viewSignupInfoEmail.setOnEditorActionListener(editorActionListener)
            bind.viewSignupInfoPassword.setOnEditorActionListener(editorActionListener)
            bind.viewSignupInfoPasswordre.setOnEditorActionListener(editorActionListener)
            bind.viewSignupInfoButton.setOnClickListener {
                signup(bind)
            }
            viewModel.email.observe(lifecycleOwner) {
                bind.viewSignupInfoButton.isEnabled = viewModel.formValid()
            }
            viewModel.password.observe(lifecycleOwner) {
                bind.viewSignupInfoButton.isEnabled = viewModel.formValid()
            }
            viewModel.password2.observe(lifecycleOwner) {
                bind.viewSignupInfoButton.isEnabled = viewModel.formValid()
            }
        }

        private fun bindCellSignupCheck(bind: CellSignupCheckBinding) {
            bind.vm = viewModel
            bind.viewSignupCheckButton.setOnSafeClickListener {
                checkEmail(bind)
            }
            bind.viewSignupCheckEmail.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkEmail(bind)
                }
                true
            }
        }

        private fun signup(bind: CellSignupInfoBinding) {
            var valid = true
            if (!viewModel.email.value.isValidEmail()) {
                valid = false
                bind.viewSignupInfoEmail.error =
                    bind.root.context.getText(R.string.signup_info_email_error)
            }

            if (viewModel.password.value == null || viewModel.password.value!!.length < 6) {
                //TODO : password format 수정
                valid = false
                bind.viewSignupInfoPassword.error =
                    bind.root.context.getText(R.string.signup_info_message2)
            } else if (viewModel.password.value != null && viewModel.password.value != viewModel.password2.value) {
                valid = false
                bind.viewSignupInfoPasswordre.error =
                    bind.root.context.getText(R.string.signup_info_passwordre_error)
            }

            if (valid) {
                bind.viewSignupInfoEmail.error = null
                bind.viewSignupInfoPassword.error = null
                bind.viewSignupInfoPasswordre.error = null

                GlobalScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
                    //가입
                    if (viewModel.email.value?.isNotEmpty() == true && viewModel.password.value?.isNotEmpty() == true) {
                        Progress.show(bind.root.context)
                        viewModel.signupEmail(viewModel.email.value!!, viewModel.password.value!!)
                        viewModel.signupViewMode.postValue(SignupPagerType.Check)
                        Progress.dismiss()
                    } else {
                        throw ErrorDialogException(
                            R.string.signin_error_title,
                            R.string.signin_error_message
                        )
                    }
                }
            }
        }

        private fun checkEmail(bind: CellSignupCheckBinding) {
            if (viewModel.checkNumber.value.isNullOrEmpty()) {
                viewModel.errorResId.postValue(R.string.signup_check_error)
            } else {
                GlobalScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
                    //가입
                    if (viewModel.email.value?.isNotEmpty() == true && viewModel.password.value?.isNotEmpty() == true) {
                        viewModel.checkNumber(
                            viewModel.email.value!!,
                            viewModel.checkNumber.value!!
                        )
                        signupInterface.moveSigninComplete()
                    } else {
                        throw ErrorDialogException(
                            R.string.signin_error_title,
                            R.string.signin_error_message
                        )
                    }
                }
            }
        }

        private val editorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
            if (binding is CellSignupInfoBinding) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (v == binding.viewSignupInfoEmail) {
                        binding.viewSignupInfoPassword.requestFocus()
                    } else if (v == binding.viewSignupInfoPassword) {
                        binding.viewSignupInfoPasswordre.requestFocus()
                    }
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signup(binding)
                }
            } else if (binding is CellSignupCheckBinding) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signupInterface.moveSigninComplete()
                }
            }

            true
        }

        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
            Progress.dismiss()
            if (exception is HttpException) {
                if (exception.code() == 450) {
                    signupInterface.showDialog(
                        R.string.signin_error_title,
                        R.string.signin_error_auth_message
                    )
                } else if (exception.code() == 401) {
                    signupInterface.showDialog(
                        R.string.signin_error_title,
                        R.string.signin_error_title
                    )
                } else if (exception.code() == 400) {
                    signupInterface.showDialog(
                        R.string.error_title,
                        R.string.signin_error_already
                    )
                } else {
                    signupInterface.showDialog(
                        R.string.error_title,
                        R.string.error_message
                    )
                }
            }
        }
    }
}

