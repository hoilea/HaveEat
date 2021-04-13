package kr.snclab.haveeat.ui.food

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.foods.FoodsService
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.model.FoodDetail
import timber.log.Timber

class FoodInfoDetailViewModel @ViewModelInject constructor(private val foodsService: FoodsService) :
    ViewModel() {
    var foodDetail = MutableLiveData<FoodDetail>()


    suspend fun getFoodDetail(id:String) {
        foodDetail.postValue(foodsService.getFoodDetail(id))
    }
}