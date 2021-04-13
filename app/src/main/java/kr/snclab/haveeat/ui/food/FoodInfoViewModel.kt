package kr.snclab.haveeat.ui.food

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.foods.FoodsService
import kr.snclab.haveeat.model.Food
import timber.log.Timber

class FoodInfoViewModel @ViewModelInject constructor(private val foodsService: FoodsService) :
    ViewModel() {
    val itemList = MutableLiveData<Array<Food>>()
    var searchText = MutableLiveData<String>("")

    suspend fun search() {
        val text = searchText.value
        if (text != null && (text.length > 1 || (text.length == 1 && text[0]>='가' && text[0]<='힣'))) {
            Timber.d("search: $text")
            val result = foodsService.getFoods(text, 0.toString(), 200.toString())
            itemList.postValue(result.rows)
            //TODO DataSource.Factory 구현.
        }
    }

    val splitter = ","
    fun getHistory(): Array<String> {
        return SharedData.foodInfoHistory.split(splitter).toTypedArray()
    }

    fun addHistory() {
        SharedData.foodInfoHistory =
            if (SharedData.foodInfoHistory == null || SharedData.foodInfoHistory.isEmpty()) {
                searchText.value
            } else {
                SharedData.foodInfoHistory + splitter + searchText.value
            }
    }
}