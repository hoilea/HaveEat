package kr.snclab.haveeat.ui.food

import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentFoodinfoDetailBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.FoodDetail
import kr.snclab.haveeat.ui.BaseFragment


@AndroidEntryPoint
class FoodInfoDetailFragment : BaseFragment<FragmentFoodinfoDetailBinding>() {

    private val viewModel by viewModels<FoodInfoDetailViewModel>()
    private val args: FoodInfoDetailFragmentArgs by navArgs()
    override fun getLayoutResId() = R.layout.fragment_foodinfo_detail

    override fun initFragment() {
        bind.viewFoodInfoDetailClose.setOnSafeClickListener {
            findNavController().popBackStack()
        }

        viewModel.foodDetail.observe(viewLifecycleOwner) {
            setData(it)
        }

        lifecycleScope.launch {
            viewModel.getFoodDetail(args.foodId)
        }
    }

    private fun setData(foodDetail: FoodDetail) {
        bind.viewFoodInfoDetailType.text = foodDetail.category?.joinToString(" - ")
        bind.viewFoodInfoDetailName.text = foodDetail.name
        bind.viewFoodInfoDetailDescription.text = foodDetail.description
        bind.viewFoodInfoDetailGramType.text = String.format("인분 (1인분, %d g)", foodDetail.servings?.toInt()?:0)
        bind.viewFoodInfoDetailValueCarbohydrateValue.text = Define.toGramFormat(foodDetail.carbohydrate)
        bind.viewFoodInfoDetailValueProteinValue.text = Define.toGramFormat(foodDetail.protein)
        bind.viewFoodInfoDetailValueFatValue.text = Define.toGramFormat(foodDetail.fat)
        bind.viewFoodInfoDetailValueSugarValue.text = Define.toGramFormat(foodDetail.tsg)
        bind.viewFoodInfoDetailCalValue.text = foodDetail.calorie?.toString()?:"1"
        val ingredients = foodDetail.ingredients.map {
            it.toString()
        }
        if(ingredients.isEmpty()) {
            bind.viewFoodInfoDetailImageTitle.visibility = View.GONE
        } else {
            bind.viewFoodInfoDetailIngredients.layoutParams =
                bind.viewFoodInfoDetailIngredients.layoutParams.apply {
                    height =
                        (resources.getDimension(R.dimen.foodInfoDetailIngredients_height) * ingredients.size).toInt()
                }
            bind.viewFoodInfoDetailIngredients.adapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.cell_food_ingredients,
                R.id.view_foodIngredients_historyName,
                ingredients
            )
        }
    }
}