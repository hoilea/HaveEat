package kr.snclab.haveeat.ui.signup

import androidx.annotation.StringRes

interface SignupInterface  {
    fun showTerms1()
    fun showTerms2()
    fun moveSigninComplete()
    fun showDialog(@StringRes titleResId: Int, @StringRes messageResId: Int)
}