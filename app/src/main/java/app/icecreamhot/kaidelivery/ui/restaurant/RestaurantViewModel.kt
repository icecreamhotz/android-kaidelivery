package app.icecreamhot.kaidelivery.ui.restaurant

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.util.Log
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.base.BaseViewModel
import app.icecreamhot.kaidelivery.data.gAliasDistance
import app.icecreamhot.kaidelivery.data.gDistance
import app.icecreamhot.kaidelivery.model.Restaurant
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import app.icecreamhot.kaidelivery.utils.BASE_URL_RESTAURANT_IMG
import java.math.RoundingMode
import java.text.DecimalFormat



class RestaurantViewModel(): BaseViewModel() {
    val res_name = MutableLiveData<String>()
    var res_logo: String? = null
    var res_lat: String? = null
    var res_lng: String? = null
    var restype_name: String? = null

    var distance: Int? = null
    val predicTime = MutableLiveData<String>()
    val predicKm = MutableLiveData<String>()
    val show_restypeName = MutableLiveData<String>()

    fun bind(restaurant: Restaurant) {

        res_name.value = restaurant.res_name
        res_logo = if (restaurant.res_logo == null) "noimg.png" else restaurant.res_logo
        res_lat = restaurant.res_lat
        res_lng = restaurant.res_lng
        restype_name = restaurant.restype_name

        if(res_lat != null && res_lng != null) {
            val startPoint = Location("locationOne")
            startPoint.setLatitude(mLatitude)
            startPoint.setLongitude(mLongitude)

            val endPoint = Location("locationTwo")
            endPoint.setLatitude(res_lat!!.toDouble())
            endPoint.setLongitude(res_lng!!.toDouble())

            distance = startPoint.distanceTo(endPoint).toInt()
        } else {
            distance = 0
        }
    }

    fun getRestaurantName():MutableLiveData<String> {
        return res_name
    }

    fun getRestaurantLogo(): String {
        return BASE_URL_RESTAURANT_IMG + res_logo
    }

    fun getRestaurantType():MutableLiveData<String> {
        val replaceType = restype_name?.replace("[", "")?.replace("]", "")
            ?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
        var restype_nameAll = ""

        replaceType?.forEach {
            restype_nameAll += "$it "
        }

        show_restypeName.value = if(restype_nameAll == "") "ไม่มีข้อมูล" else restype_nameAll

        return show_restypeName
    }

    fun getKilometerDistance(): MutableLiveData<String> {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.FLOOR
        if(distance!! >= 1000) {
            gDistance = distance!! / 1000.0
            gAliasDistance = "กม."
        } else {
            gDistance = distance!!.toDouble()
            gAliasDistance = "ม."
        }

        predicKm.value = if(res_lat != null && res_lng != null) df.format(gDistance) + gAliasDistance else "ไม่มีข้อมูล"

        return predicKm
    }

    fun getPredictionTimeBetweenDistance(): MutableLiveData<String> {
        val speedIsMetersPerMinute = 150
        val estimatedDriveTimeInMinutes = distance!! / speedIsMetersPerMinute
        predicTime.value = if(res_lat != null && res_lng != null) "" + estimatedDriveTimeInMinutes + " นาที ถึง " + (estimatedDriveTimeInMinutes + 5) + " นาที" else "ไม่มีข้อมูล"
        return predicTime
    }

}