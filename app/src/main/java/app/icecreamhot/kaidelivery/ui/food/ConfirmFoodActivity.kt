package app.icecreamhot.kaidelivery.ui.food

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.data.gAliasDistance
import app.icecreamhot.kaidelivery.data.gDistance
import app.icecreamhot.kaidelivery.data.menu
import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.network.RateAPI
import app.icecreamhot.kaidelivery.ui.map.MapsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_confirm_food.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class ConfirmFoodActivity : AppCompatActivity() {

    private val rateAPI by lazy { RateAPI.create() }

    private var disposable: Disposable? = null
    private var res_id: Int = 0
    private var rateListArray: List<Rate>? = null
    var totalPrice = 0.0
    var deliveryPrice = 0
    var totalPriceAndDeliveryPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_food)

        res_id = intent.getIntExtra("res_id", 235)

        getRatePrice()
        setMenuToAdapter()
        setNowLocation()

        val locationClickListener = View.OnClickListener { _ ->
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        val confirmOrderClickListener = View.OnClickListener { _ ->
            saveOrderToDB()
        }
        btnSearchLocation.setOnClickListener(locationClickListener)
        btConfirmOrder.setOnClickListener(confirmOrderClickListener)
    }

    private fun saveOrderToDB() {
//        val timenow = Calendar.getInstance()
//        val hour = timenow.get(Calendar.HOUR_OF_DAY)
//        val minute = timenow.get(Calendar.MINUTE)
//        val second = timenow.get(Calendar.SECOND)
//
//        val order = MenuList(res_id,
//            rateListArray!!.get(0).rate_id,
//            btnSearchLocation.text.toString().trim(),
//            mRestaurantLatitude,
//            mRestaurantLongitude,
//            deliveryPrice.toDouble(),
//            "${hour}:${minute}:${second}",
//            edtOrderDetails.text.toString().trim(),
//            edtEndpointDetails.text.toString().trim(),
//            minMinute,
//            menu)
//
//        Log.d("order", order.toString())
//
//        val intent = Intent(this, OTPActivity::class.java)
//        intent.putExtra("order", order)
//        startActivity(intent)
    }

    private fun getRatePrice() {
        disposable = rateAPI.getDeliveryRate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    result -> calculatePrice(result.rateList)
                },
                {
                    error -> Log.d("error", error.message)
                }
            )
    }

    private fun setMenuToAdapter() {
        val confirmFoodAdapter = ConfirmFoodAdapter(menu)

        orderList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = confirmFoodAdapter
        }
    }

    private fun calculatePrice(rateList: List<Rate>?) {
        rateListArray = rateList

        menu.map { it -> totalPrice += (it.orderdetails_total * it.orderdetails_price) }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val specialTime = LocalTime.of(20, 0)
            val current = LocalDateTime.now()
            val timeNow = LocalTime.of(current.hour, current.minute)

            if(timeNow.isAfter(specialTime)) {
                deliveryPrice = checkDistance(rateList!![2].rate_price, rateList[1].rate_price)
            } else {
                deliveryPrice = checkDistance(rateList!![0].rate_price, rateList[1].rate_price)
            }

        } else {
            val date = Date()
            val timeone = Calendar.getInstance()
            timeone.time = date
            timeone.get(Calendar.HOUR_OF_DAY)
            timeone.get(Calendar.MINUTE)
            timeone.get(Calendar.SECOND)

            val timetwo = Calendar.getInstance()
            timetwo.set(Calendar.HOUR_OF_DAY, 20)
            timetwo.set(Calendar.MINUTE, 0)
            timetwo.set(Calendar.SECOND, 0)

            if(timeone.after(timetwo)) {
                deliveryPrice = checkDistance(rateList!![2].rate_price, rateList[1].rate_price)
            } else {
                deliveryPrice = checkDistance(rateList!![0].rate_price, rateList[1].rate_price)
            }
        }

        totalPriceAndDeliveryPrice = totalPrice + deliveryPrice

        txtFoodAllCal.text = "$totalPrice บาท"
        txtDeliveryCal.text = "${deliveryPrice} บาท"
        txtCalFoodAndDelivery.text ="${totalPriceAndDeliveryPrice} บาท"
    }

    private fun checkDistance(standardPrice: Int, overDistanceRate: Int): Int {
        if(gAliasDistance == "กม.") {
            if(gDistance >= 4.0) {
                val deleteOverDistance = gDistance - 3.0
                val calculateOverDistance = overDistanceRate * deleteOverDistance.toInt()
                return standardPrice + calculateOverDistance
            } else {
                return standardPrice
            }
        }
        return standardPrice
    }

    private fun setNowLocation() {
        try {
            val geocoder = Geocoder(this.applicationContext, Locale.getDefault())
            val address: List<Address> = geocoder.getFromLocation(mLatitude, mLongitude, 1)
            if(address.isEmpty()) {
                btnSearchLocation.text = resources.getString(R.string.loadinglocation)
            } else {
                if(address.size > 0) {
                    val location = address.get(0).getAddressLine(0)
                    btnSearchLocation.text = location
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        setNowLocation()
        Log.d("data", "resumed")
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

