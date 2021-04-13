package kr.snclab.haveeat.ui.main.history

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class HistoryDayRecyclerView : RecyclerView {constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling((velocityX*0.8).toInt(), velocityY)
    }
}