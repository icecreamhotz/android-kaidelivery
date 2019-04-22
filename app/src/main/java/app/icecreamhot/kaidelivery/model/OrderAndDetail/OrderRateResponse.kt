package app.icecreamhot.kaidelivery.model.OrderAndDetail

import com.google.gson.annotations.SerializedName

data class OrderRateResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<OrderRate>
)