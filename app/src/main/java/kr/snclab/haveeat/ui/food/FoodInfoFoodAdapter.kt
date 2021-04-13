package kr.snclab.haveeat.ui.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kr.snclab.haveeat.R
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.ui.view.FoodCellType
import kr.snclab.haveeat.ui.view.FoodHolder


class FoodInfoFoodAdapter(private var foodCellType : FoodCellType, private val findNavController: NavController) : RecyclerView.Adapter<FoodHolder>(){
//    private var foodCellType : FoodCellType = FoodCellType.FoodInfo
    private var foodArray: Array<Food> = arrayOf()
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): FoodHolder {
        val view = LayoutInflater.from(group.context).inflate(R.layout.cell_food, group, false)
        return FoodHolder(view, foodCellType, findNavController)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bindTo(foodArray[position])
    }

    override fun getItemCount() = foodArray.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onViewRecycled(holder: FoodHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    fun setData(foodArray: Array<Food>) {
        this.foodArray = foodArray
        notifyDataSetChanged()
    }
}