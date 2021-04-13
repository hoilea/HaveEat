package kr.snclab.haveeat.ui.main.history

import android.net.Uri
import androidx.core.net.toFile
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.diets.DietsService
import kr.snclab.haveeat.api.diets.PutDietsRequest
import kr.snclab.haveeat.api.diets.VisionResponse
import kr.snclab.haveeat.api.foods.FoodsService
import kr.snclab.haveeat.api.user.EatenFood
import kr.snclab.haveeat.api.user.PostDietsRequest
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.extension.toCacheFile
import kr.snclab.haveeat.model.Diets
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.model.FoodDetail
import kr.snclab.haveeat.model.HistoryPartType
import kr.snclab.haveeat.ui.view.FoodCellType
import kr.snclab.haveeat.util.Log
import kr.snclab.haveeat.util.SingleLiveEvent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class HistoryDetailViewModel @ViewModelInject constructor(private val dietsService: DietsService, private val usersService: UsersService, private val foodsService: FoodsService) : ViewModel() {

    var diets : SingleLiveEvent<Diets> = SingleLiveEvent()
    var foodCellType : MutableLiveData<FoodCellType> = MutableLiveData(FoodCellType.HistoryDetail)
    var dietsOrg : Diets? = null
//    var editFoodList: MutableList<Food> = mutableListOf()
    var visionResponse = MutableLiveData<VisionResponse>()

    suspend fun getDietsDetail(id: Int) {
        val d = dietsService.getDiets(id)

        dietsOrg = d.copy()
        diets.postValue(d)
        d.totalCalorie = d.totalCalorie
        if(d.imageUrl?.isEmpty() == false) {
            visionResponse.postValue(dietsService.getDietsVision(id))
        }
    }

    fun updateTime(date: Date) {
        diets.value?.date = date
    }

    suspend fun vision(imageFile: File) : VisionResponse {
        val file = MultipartBody.Part.createFormData(
            "file",
            imageFile.name,
            imageFile.asRequestBody()
        )
        return dietsService.vision(file).also {
            visionResponse.postValue(it)
        }
    }

    suspend fun delete() {
        diets.value?.id?.let {
            try {
                dietsService.deleteDiets(it)
            } catch (e: Exception) {

            }
        }
    }

    suspend fun save() {
        diets.value?.let { diets ->
            if(diets.id == -1) {
                //생성
                usersService.postDiets(PostDietsRequest(Define.SERVER_DATE_FORMAT.format(diets.date), Define.SERVER_TIME_FORMAT.format(diets.date), diets.type().value, diets.foods?.map {
                    EatenFood(it.id, it.amount?:1f)
                }?.toTypedArray(), visionResponse.value?.filestoreId))
            } else {
                //수정
                dietsService.putDiets(diets.id, PutDietsRequest(diets))
            }
            dietsOrg = diets
        }
    }

    suspend fun getFood(foodId: String) : FoodDetail{
        return foodsService.getFoodDetail(foodId)
    }

    fun cancel() {
        dietsOrg?.let { org ->
            diets.value =  org.copy()
        }

    }
}