package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class UserList(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val arrUserList: User? = null
)