package kr.snclab.haveeat.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ToastUtil {
    private var mToast: Toast? = null

    fun show(context: Context?, msg: String, duration: Int = Toast.LENGTH_SHORT) {
        if (context != null) {
            GlobalScope.launch(Dispatchers.Main) {
                if (mToast != null) {
                    mToast!!.setText(msg)
                } else {
                    mToast = Toast.makeText(context, msg, duration)
                }
                mToast?.show()
            }
        }
    }

    fun show(context: Context?, resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        if (context != null) {
            show(context, context.getString(resId), duration)
        }
    }
}