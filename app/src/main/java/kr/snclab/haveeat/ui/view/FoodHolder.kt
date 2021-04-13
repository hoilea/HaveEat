package kr.snclab.haveeat.ui.view

import android.view.View
import androidx.navigation.NavController
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kr.snclab.haveeat.GlideApp
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.CellFoodBinding
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.ui.BindingViewHolder
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import kr.snclab.haveeat.ui.main.history.HistoryDetailFragment
import kr.snclab.haveeat.ui.main.history.HistoryDetailFragmentDirections


class FoodHolder(
    view: View,
    private var foodCellType: FoodCellType,
    private val findNavController: NavController,
    private var deleteListener: View.OnClickListener? = null
) : BindingViewHolder<CellFoodBinding, Food>(view) {
    override fun bindTo(data: Food) {
        setData(data)
        setType(foodCellType)

        binding.viewFoodCellChange.setOnClickListener {
            if (foodCellType == FoodCellType.HistoryDetailEdit) {
                findNavController.navigate(
                    HistoryDetailFragmentDirections.actionIntakeFragment(adapterPosition, data)
                )
            }
        }

        binding.root.setOnClickListener {
            if (foodCellType == FoodCellType.HistoryDetail || foodCellType == FoodCellType.FoodInfo)
                findNavController.navigate(
                    MainFragmentDirections.actionFoodInfoDetailFragment(
                        data.id
                    )
                )
        }

        binding.viewFoodCellAdd.setOnClickListener {
            findNavController.previousBackStackEntry?.savedStateHandle?.set(
                HistoryDetailFragment.ARG_ADD_FOOD, data
            )
            findNavController.popBackStack()
        }

        binding.viewFoodCellDelete.setOnClickListener(deleteListener)
    }

    override fun clear() {

    }

    fun setType(foodCellType: FoodCellType) {
        this.foodCellType = foodCellType
        when (foodCellType) {
            FoodCellType.HistoryDetail -> {
                binding.viewFoodCellDetail.visibility = View.VISIBLE
                binding.viewFoodCellAdd.visibility = View.GONE
                binding.viewFoodCellChange.visibility = View.GONE
                binding.viewFoodCellDelete.visibility = View.GONE
            }
            FoodCellType.HistoryDetailEdit -> {
                binding.viewFoodCellDetail.visibility = View.GONE
                binding.viewFoodCellAdd.visibility = View.GONE
                binding.viewFoodCellChange.visibility = View.VISIBLE
                binding.viewFoodCellDelete.visibility = View.VISIBLE
            }
            FoodCellType.FoodAdd -> {
                binding.viewFoodCellDetail.visibility = View.GONE
                binding.viewFoodCellAdd.visibility = View.VISIBLE
                binding.viewFoodCellChange.visibility = View.GONE
                binding.viewFoodCellDelete.visibility = View.GONE
            }
            FoodCellType.FoodInfo -> {
                binding.viewFoodCellDetail.visibility = View.GONE
                binding.viewFoodCellAdd.visibility = View.GONE
                binding.viewFoodCellChange.visibility = View.GONE
                binding.viewFoodCellDelete.visibility = View.GONE
            }
        }
    }

    private fun setData(food: Food) {
        binding.viewFoodCellDelete.tag = adapterPosition
        binding.viewFoodCellName.text = food.name
        binding.viewFoodCellCalorie.text = food.getIntakeMessage(context)
        food.imageUrl?.let {
            //TODO : 나중에 GlideUrl 필요 없으면 지우자.
            val url = GlideUrl(
                it, LazyHeaders.Builder()
                    .addHeader("User-Agent", "your-user-agent")
                    .build()
            )
            GlideApp.with(context).load(url).circleCrop().into(binding.viewFoodCellImage)
        } ?: run {
            binding.viewFoodCellImage.setImageResource(R.drawable.bg_oval_white)
        }
    }
}

enum class FoodCellType {
    HistoryDetail, HistoryDetailEdit, FoodAdd, FoodInfo
}