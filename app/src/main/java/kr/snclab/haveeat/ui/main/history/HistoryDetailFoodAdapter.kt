package kr.snclab.haveeat.ui.main.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kr.snclab.haveeat.R
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.ui.view.FoodCellType
import kr.snclab.haveeat.ui.view.FoodHolder
import kr.snclab.haveeat.util.Log


class HistoryDetailFoodAdapter(
    private val viewModel: HistoryDetailViewModel,
    private val findNavController: NavController
) : RecyclerView.Adapter<FoodHolder>(){
    var foodCellType : FoodCellType = FoodCellType.HistoryDetail
    private var foodList: MutableList<Food> = mutableListOf()
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): FoodHolder {
        val view = LayoutInflater.from(group.context).inflate(R.layout.cell_food, group, false)
        return FoodHolder(view, foodCellType, findNavController) {
            (it.tag as? Int)?.let { position ->
//                deleteItem(it, position)
                viewModel.diets.value?.foods?.let { foods ->
                    val list = foods.toMutableList()
                    list.removeAt(position)
                    viewModel.diets.value?.foods = list.toTypedArray()
                }
                foodList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.setType(foodCellType)
        holder.bindTo(foodList[position])
    }

    override fun getItemCount() = foodList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onViewRecycled(holder: FoodHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    fun getData() = foodList

    fun setType(type: FoodCellType) {
        foodCellType = type
        notifyDataSetChanged()
    }

    fun reset() {
        foodList = viewModel.diets.value?.foods?.toMutableList()?: mutableListOf()
//        notifyDataSetChanged()
    }

    fun addFood(food: Food) {
        foodList.add(food)
        notifyDataSetChanged()
    }


//    private val animationDuration = 300
//    private fun deleteItem(view: View, position: Int) {
//        val anim: Animation = AnimationUtils.loadAnimation(view.context,
//            android.R.anim.slide_out_right
//        )
//        anim.duration = 300
//        view.startAnimation(anim)
//        Handler().postDelayed(Runnable {
//////            if (itemCount === 0) {
//////                addEmptyView() // adding empty view instead of the RecyclerView
//////                return@Runnable
//////            }
//            foodList.removeAt(position)
//            notifyDataSetChanged()
//        }, anim.duration)
//    }
}