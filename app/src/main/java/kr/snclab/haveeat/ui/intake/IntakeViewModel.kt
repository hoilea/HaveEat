package kr.snclab.haveeat.ui.intake

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.api.foods.FoodsService
import kr.snclab.haveeat.model.FoodDetail

class IntakeViewModel @ViewModelInject constructor(private val foodsService: FoodsService) :
    ViewModel() {
    val amount = MutableLiveData("1")
    val foodDetail = MutableLiveData<FoodDetail>()

    suspend fun getDietsDate(foodId: String) {
        foodDetail.postValue(foodsService.getFoodDetail(foodId))
    }
}