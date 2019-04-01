package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class FoodList(
    @SerializedName("data")
    var foodArrayList: List<Food>? = null
)