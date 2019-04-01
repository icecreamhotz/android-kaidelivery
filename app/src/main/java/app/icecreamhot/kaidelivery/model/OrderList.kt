package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class OrderList(
    @SerializedName("message")
    val errorMessage: String,
    @SerializedName("order_id")
    val orderID: Int,
    @SerializedName("order_name")
    val orderName: String
)