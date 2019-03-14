package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class RestaurantTypeList(
    @SerializedName("data")
    var restaurantTypeArrayList: List<RestaurantType>? = null
)