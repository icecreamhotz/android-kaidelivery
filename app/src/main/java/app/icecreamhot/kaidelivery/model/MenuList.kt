package app.icecreamhot.kaidelivery.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MenuList(
    val res_id: Int,
    val rate_id: Int,
    val endpoint_name: String,
    val endpoint_lat: Double,
    val endpoint_lng: Double,
    val order_deliveryprice: Double,
    val order_start: String,
    val order_details: String?,
    val endpoint_details: String?,
    @SerializedName("menu")
    val menus: List<Menu>? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readString(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.createTypedArrayList(Menu.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(res_id)
        writeInt(rate_id)
        writeString(endpoint_name)
        writeDouble(endpoint_lat)
        writeDouble(endpoint_lng)
        writeDouble(order_deliveryprice)
        writeString(order_start)
        writeString(order_details)
        writeString(endpoint_details)
        writeTypedList(menus)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MenuList> = object : Parcelable.Creator<MenuList> {
            override fun createFromParcel(source: Parcel): MenuList = MenuList(source)
            override fun newArray(size: Int): Array<MenuList?> = arrayOfNulls(size)
        }
    }
}