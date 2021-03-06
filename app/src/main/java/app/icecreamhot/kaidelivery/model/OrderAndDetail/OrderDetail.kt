package app.icecreamhot.kaidelivery.model.OrderAndFoodDetail

import com.google.gson.annotations.SerializedName

data class OrderDetail(
    val orderdetail_total: Int,
    val orderdetail_price: String,
    @SerializedName("food")
    val food: Food
)