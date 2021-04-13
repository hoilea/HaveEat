package kr.snclab.haveeat.ui

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView


abstract class BindingViewHolder<out T : ViewDataBinding, in U>(view: View, lifecycleOwner: LifecycleOwner? = null) : RecyclerView.ViewHolder(view) {
    val context = view.context
    val binding: T = DataBindingUtil.bind(view)!!

    abstract fun bindTo(data: U)
    abstract fun clear()

    init {
        lifecycleOwner?.let {
            binding.lifecycleOwner = it
        }
    }
}
