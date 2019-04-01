package app.icecreamhot.kaidelivery.ui.restaurant

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.data.gAliasDistance
import app.icecreamhot.kaidelivery.data.gDistance
import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.ui.food.FoodActivity
import app.icecreamhot.kaidelivery.utils.BASE_URL_RESTAURANT_IMG
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_restaurant.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class RestaurantListAdapterFragment @Inject constructor(private val items: List<Restaurant>?): RecyclerView.Adapter<RestaurantListAdapterFragment.ViewHolder>() {

    var onItemClick: ((Restaurant?) -> Unit )? = null
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false))
    }

    override fun getItemCount() =  items!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items?.get(position))
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items?.get(adapterPosition))
            }
        }

        var res_lat: String? = null
        var res_lng: String? = null
        var restype_name: String? = null

        var distance: Int? = null

        fun bind(restaurant: Restaurant?) {
            val res_logo = if (restaurant?.res_logo == null) "noimg.png" else restaurant?.res_logo
            val image = BASE_URL_RESTAURANT_IMG + res_logo
            itemView.apply {
                res_name.text = restaurant?.res_name

                res_lat = restaurant?.res_lat
                res_lng = restaurant?.res_lng
                restype_name = restaurant?.restype_name

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

                res_details.text = getRestaurantType()
                predic_kilometers.text = getKilometerDistance()
                predic_time.text = getPredictionTimeBetweenDistance()
            }
            Glide.with(itemView.context).load(image).into(itemView.res_logo)
        }

        fun getRestaurantType(): String {
            val replaceType = restype_name?.replace("[", "")?.replace("]", "")
                ?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()
            var restype_nameAll = ""

            replaceType?.forEach {
                restype_nameAll += "$it "
            }

            return if(restype_nameAll == "") "ไม่มีข้อมูล" else restype_nameAll
        }

        fun getKilometerDistance(): String {
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.FLOOR
            if(distance!! >= 1000) {
                gDistance = distance!! / 1000.0
                gAliasDistance = "กม."
            } else {
                gDistance = distance!!.toDouble()
                gAliasDistance = "ม."
            }

            return if(res_lat != null && res_lng != null) df.format(gDistance) + gAliasDistance else "ไม่มีข้อมูล"
        }

        fun getPredictionTimeBetweenDistance(): String {
            val speedIsMetersPerMinute = 150
            val estimatedDriveTimeInMinutes = distance!! / speedIsMetersPerMinute

            return if(res_lat != null && res_lng != null) "" + estimatedDriveTimeInMinutes + " นาที ถึง " + (estimatedDriveTimeInMinutes + 5) + " นาที" else "ไม่มีข้อมูล"
        }
    }
}