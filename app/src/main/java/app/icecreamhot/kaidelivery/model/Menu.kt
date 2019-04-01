package app.icecreamhot.kaidelivery.model

import android.os.Parcel
import android.os.Parcelable

data class Menu(
    val food_id: Int,
    val food_name: String,
    var orderdetails_total: Int,
    val orderdetails_price: Double
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readInt(),
        source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(food_id)
        writeString(food_name)
        writeInt(orderdetails_total)
        writeDouble(orderdetails_price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Menu> = object : Parcelable.Creator<Menu> {
            override fun createFromParcel(source: Parcel): Menu = Menu(source)
            override fun newArray(size: Int): Array<Menu?> = arrayOfNulls(size)
        }
    }
}