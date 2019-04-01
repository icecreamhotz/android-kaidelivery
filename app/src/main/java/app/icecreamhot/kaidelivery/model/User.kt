package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("message")
    val message: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("expiresIn")
    val expiresIn: Int?
)