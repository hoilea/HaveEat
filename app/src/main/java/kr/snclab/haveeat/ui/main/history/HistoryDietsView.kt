package kr.snclab.haveeat.ui.main.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.GlideApp
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.CellHistoryDietsBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.Diets
import kr.snclab.haveeat.model.HistoryPartType
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import timber.log.Timber
import java.util.*


class HistoryDietsView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val binding: CellHistoryDietsBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding =
            DataBindingUtil.inflate(inflater, R.layout.cell_history_diets, this, true)
    }

    fun setData(calendar: Calendar, partType: HistoryPartType, diets: Diets? = null) {
        Timber.d("partType:${partType.name} diets:${(diets != null)}")
        binding.viewHistoryPartListTitle.text = context.getString(partType.resId)
        binding.viewHistoryPartListOval.isSelected = diets != null
        if (diets != null) {
            binding.viewHistoryPartListCal.visibility = View.VISIBLE
            binding.viewHistoryPartListCal.text = diets.totalCalorie.toInt().toString()
            binding.viewHistoryPartListMessage.visibility = View.VISIBLE
            binding.viewHistoryPartListMessage.text = diets.timeString
            binding.viewHistoryPartListCalType.visibility = View.VISIBLE
            binding.viewHistoryPartListEmpty.visibility = View.GONE
            diets.image?.let {
                GlideApp.with(context).load("${Define.Api.FileStore.GET_FILE}${it}").circleCrop().into(binding.viewHistoryPartListOval)
            }?: run {
                binding.viewHistoryPartListOval.setImageResource(R.drawable.shape_history_item)
            }
            binding.root.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionHistoryDetailFragment(diets))
            }
        } else {
            binding.viewHistoryPartListCal.visibility = View.GONE
            binding.viewHistoryPartListMessage.visibility = View.GONE
            binding.viewHistoryPartListCalType.visibility = View.GONE
            binding.viewHistoryPartListEmpty.visibility = View.VISIBLE
            binding.viewHistoryPartListOval.setImageResource(R.drawable.shape_history_item)
            binding.root.setOnSafeClickListener {
                val now = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
                findNavController().navigate(MainFragmentDirections.actionWriteDietsFragment(
                    Diets(-1, partType.value, 0f, 0f, arrayOf(), calendar.time)
                ))
            }
        }

    }
}