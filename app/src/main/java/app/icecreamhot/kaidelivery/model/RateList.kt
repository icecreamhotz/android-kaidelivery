package app.icecreamhot.kaidelivery.model

import com.google.gson.annotations.SerializedName

data class RateList(
    @SerializedName("data")
    var rateList: List<Rate>? = null
)