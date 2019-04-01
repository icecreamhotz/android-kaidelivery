package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName


data class Food(
    val food_id: Int,
    val food_name: String,
    val food_price: String,
    val food_img: String? = null,
    val foodtype_id: Int,
    val res_id: Int
)