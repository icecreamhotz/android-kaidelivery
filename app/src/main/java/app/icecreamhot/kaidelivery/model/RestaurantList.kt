package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class RestaurantList(
    @SerializedName("data")
    var employeeArrayList: List<Restaurant>? = null
)