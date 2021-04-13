package kr.snclab.haveeat.ui.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.CellFoodinfoHistoryBinding
import kr.snclab.haveeat.ui.BindingViewHolder


class HistoryAdapter(private var historyArray: Array<String>, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<HistoryAdapter.FoodHolder>(){

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): FoodHolder {
        val view = LayoutInflater.from(group.context).inflate(R.layout.cell_foodinfo_history, group, false)
        return FoodHolder(view)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bindTo(historyArray[position])
    }

    override fun getItemCount() = historyArray.size

    override fun onViewRecycled(holder: FoodHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    fun setData(historyArray: Array<String>) {
        this.historyArray = historyArray
        notifyDataSetChanged()
    }

    inner class FoodHolder(val view: View) : BindingViewHolder<CellFoodinfoHistoryBinding, String>(view) {
        override fun bindTo(data: String) {
            binding.viewFoodCellHistoryName.text = data
            binding.root.setOnClickListener {
                itemClickListener.onClick(data)
            }
        }

        override fun clear() {
        }
    }

    interface ItemClickListener {
        fun onClick(history: String)
    }
}