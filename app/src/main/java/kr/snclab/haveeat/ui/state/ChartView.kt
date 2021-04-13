package kr.snclab.haveeat.ui.state

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.ViewChartBinding
import kr.snclab.haveeat.extension.px
import timber.log.Timber
import java.text.DecimalFormat


class ChartView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val binding: ViewChartBinding
    val root: ConstraintLayout
    private val barWidth = 30.px
    //화면에서 가장 높은 바가 화면의 몇% 를 차지 하는지
    private val maxBarHeightPercent = 65
    private var guidelineList: List<Guideline>? = null

    private var barList: List<View>? = null

    private var titleList: List<AppCompatTextView>? = null

    private var valueList: List<AppCompatTextView>? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding =
            DataBindingUtil.inflate(inflater, R.layout.view_chart, this, true)
        root = binding.parent
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.attachViewToParent(child, index, params)
    }

    fun setData(dataArray: Array<ChartData>) {
        removeChartView()
        val maxHeight = root.height / 100 * maxBarHeightPercent
        val maxValue = dataArray.maxOf { it.value }
        guidelineList = dataArray.mapIndexed { index, chartData ->
            makeGuideline(1f.div(dataArray.size+1) * (index+1))
        }.also { gl ->
            barList = dataArray.mapIndexed { index, chartData ->
                val height = if(chartData.value == 0 || maxValue == 0) 1 else chartData.value*maxHeight/maxValue
                makeBar( height, chartData.color, gl[index])
            }

            titleList = dataArray.mapIndexed { index, chartData ->
                makeTitle( chartData.title, gl[index])
            }

            valueList = dataArray.mapIndexed { index, d ->
                makeValue( d.value, d.color, d.valueType, d.valueFormat, barList!![index].id, gl[index])
            }
        }
    }

    /**
     * 차트 바의 기준이 될 Guideline 을 먼저 root 에 붙인다.
     */
    private fun makeGuideline(percent: Float) : Guideline{
        val centerGuideline = Guideline(context)
        centerGuideline.id = View.generateViewId()

        val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        centerParams.guidePercent = percent
        centerParams.orientation = LayoutParams.VERTICAL

        root.addView(centerGuideline, centerParams)
        return centerGuideline
    }

    /**
     * 차트 바는 Guideline 의 중앙, bottomLine 의 위에 위치 한다.
     */
    private fun makeBar(height: Int, @ColorRes color: Int, guideline: Guideline): View {
        Timber.d("makeBar:$height")
        val shape = ShapeDrawable(
            RoundRectShape(
                floatArrayOf(
                    5.px.toFloat(),
                    5.px.toFloat(),
                    5.px.toFloat(),
                    5.px.toFloat(),
                    0f,
                    0f,
                    0f,
                    0f
                ), null, null
            )
        )
        shape.paint.color = ContextCompat.getColor(context, color)

        return View(context).apply {
            id = View.generateViewId()
            val params = LayoutParams(
                barWidth,
                height
            )
            layoutParams = params
            background = shape
            root.addView(this, layoutParams)

            val constraintSet = ConstraintSet()
            constraintSet.clone(root)
            constraintSet.connect(
                id,
                ConstraintSet.START,
                guideline.id,
                ConstraintSet.START,
                0
            )
            constraintSet.connect(
                id,
                ConstraintSet.END,
                guideline.id,
                ConstraintSet.END,
                0
            )
            constraintSet.connect(
                id,
                ConstraintSet.BOTTOM,
                binding.bottomLine.id,
                ConstraintSet.TOP,
                0
            )
            constraintSet.applyTo(root)
        }
    }

    /**
     * bar의 title 은 Guideline 의 중앙, bottomLine 의 아래에 위치한다.
     */
    private fun makeTitle(title: String, guideline: Guideline): AppCompatTextView {
        val v = AppCompatTextView(context).apply {
            id = View.generateViewId()
            text = title
            textSize = 13.5f
            setTextColor(Color.parseColor("#333333"))
        }
        val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//        centerParams.setMargins(10.px)
        v.setPadding(8.px)
        root.addView(v, centerParams)

        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        constraintSet.connect(
            v.id,
            ConstraintSet.START,
            guideline.id,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            v.id,
            ConstraintSet.END,
            guideline.id,
            ConstraintSet.END,
            0
        )
        constraintSet.connect(
            v.id,
            ConstraintSet.TOP,
            binding.bottomLine.id,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.applyTo(root)
        return v
    }

    /**
     * 바의 값을 표시 해주는건 Guideline 의 중앙, bar의 위에 위치한다.
     */
    private fun makeValue(value: Int, @ColorRes color: Int, valueType: String, valueFormat: DecimalFormat, barId: Int, guideline: Guideline): AppCompatTextView {
        val v = AppCompatTextView(context).apply {
            id = View.generateViewId()
            textSize = 11f
            setTextColor(Color.parseColor("#737373"))
            val spannable = SpannableString("${valueFormat.format(value)} $valueType")
            val numberEndPosition = spannable.length-valueType.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                numberEndPosition,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(
                AbsoluteSizeSpan(16.px),
                0,
                numberEndPosition,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)),
                0,
                numberEndPosition,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = spannable
        }
        val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        v.setPadding(8.px)
        root.addView(v, centerParams)

        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        constraintSet.connect(
            v.id,
            ConstraintSet.START,
            guideline.id,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            v.id,
            ConstraintSet.END,
            guideline.id,
            ConstraintSet.END,
            0
        )
        constraintSet.connect(
            v.id,
            ConstraintSet.BOTTOM,
            barId,
            ConstraintSet.TOP,
            0
        )
        constraintSet.applyTo(root)
        return v
    }

    private fun removeChartView(){
        guidelineList?.forEach {
            root.removeView(it)
        }
        barList?.forEach {
            root.removeView(it)
        }
        titleList?.forEach {
            root.removeView(it)
        }
        valueList?.forEach {
            root.removeView(it)
        }
    }
}

data class ChartData(
    val title: String,
    val value: Int,
    @ColorRes
    val color: Int,
    val valueType: String,
    val valueFormat: DecimalFormat = DecimalFormat("###,###")
)