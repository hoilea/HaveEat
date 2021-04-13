package kr.snclab.haveeat.api.diets

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.Define

data class VisionResponse(
    @SerializedName("filestoreId")
    val filestoreId: String,

    @SerializedName("data")
    val data: Array<VisionData>,
) {
    fun getImageUrl(): String {
        return "${Define.Api.FileStore.GET_FILE}${filestoreId}"
    }
}

data class VisionData(
    val classId: String,
    val className: String,
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val frameWidth: Int,
    val frameHeight: Int
)