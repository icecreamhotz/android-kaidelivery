package app.icecreamhot.kaidelivery.model.Delivery

import com.google.gson.annotations.SerializedName


data class OrderList(
    @SerializedName("data")
    val orderList: ArrayList<Order>? = null
)