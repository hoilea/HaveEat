package kr.snclab.haveeat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseActivity<T : ViewDataBinding, R : ViewModel> : AppCompatActivity() {

    lateinit var viewDataBinding: T

    /**
     * activity에서 setcontentview() 로 호출 하는 layout resource id
     */
    abstract val layoutResourceId: Int

    /**
     * view model
     */
    abstract val viewModel: R

    /**
     * layout 초기화..
     */
    abstract fun initLayout()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
        initLayout()
    }

}