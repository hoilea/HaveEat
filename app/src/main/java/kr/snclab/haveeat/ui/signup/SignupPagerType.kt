package kr.snclab.haveeat.ui.signup

import kr.snclab.haveeat.R


enum class SignupPagerType(val titleResId: Int, val layoutId: Int) {
    Terms(R.string.signup_terms_title, R.layout.cell_signup_terms),
    Info(R.string.signup_info_title, R.layout.cell_signup_info),
    Check(R.string.signup_check_title, R.layout.cell_signup_check)
}