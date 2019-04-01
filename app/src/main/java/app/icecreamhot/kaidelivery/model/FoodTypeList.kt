package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class FoodTypeList(
    @SerializedName("data")
    val foodtypeList: List<FoodType>? = null
)