package kr.snclab.haveeat.ui.main.history

import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kr.snclab.haveeat.model.Diets
import kr.snclab.haveeat.model.HistoryPartType
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import java.util.*


class HistoryDietsAdapter(
    private val calendar: Calendar,
    var dietsArray: Array<Diets>,
    private val findNavController: NavController
) :
    RecyclerView.Adapter<HistoryDietsAdapter.HistoryDietsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDietsViewHolder {
        return HistoryDietsViewHolder(
            HistoryDietsView(parent.context)
        )
    }

    override fun onBindViewHolder(holder: HistoryDietsViewHolder, position: Int) {
        holder.bindTo(dietsArray[position])
    }

    fun setList(dietsArray: Array<Diets>) {
        this.dietsArray = dietsArray
    }
//    override fun onViewRecycled(holder: HistoryDietsViewHolder) {
//        super.onViewRecycled(holder)
//        holder.clear()
//    }

    override fun getItemCount(): Int = dietsArray.size

    inner class HistoryDietsViewHolder(private val view: HistoryDietsView) :
        RecyclerView.ViewHolder(view) {

        fun bindTo(data: Diets) {
            view.setData(calendar, HistoryPartType.Snack, data)
            view.setOnClickListener {
                findNavController.navigate(MainFragmentDirections.actionHistoryDetailFragment(data))
            }
        }
    }
}