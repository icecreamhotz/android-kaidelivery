package app.icecreamhot.kaidelivery.model.Delivery

import com.google.gson.annotations.SerializedName

data class OrderTotalPrice(
    @SerializedName("totalPrice")
    val totalPrice: Double
)