package app.icecreamhot.kaidelivery.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RestaurantType(
    @SerializedName("restype_id")
    @field:PrimaryKey
    val restype_id: Int,
    @SerializedName("restype_name")
    val restype_name: String
)