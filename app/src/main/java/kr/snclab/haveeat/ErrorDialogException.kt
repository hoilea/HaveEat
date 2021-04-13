package kr.snclab.haveeat

import androidx.annotation.StringRes
import java.lang.Exception

class ErrorDialogException(@StringRes val titleResId: Int, @StringRes val messageResId: Int) : Exception("ErrorDialogException")