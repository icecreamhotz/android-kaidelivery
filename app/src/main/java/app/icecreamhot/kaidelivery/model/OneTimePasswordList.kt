package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class OneTimePasswordList(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val onetimePasswordList: OneTimePassword? = null
)