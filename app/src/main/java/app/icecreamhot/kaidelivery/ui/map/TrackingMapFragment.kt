package app.icecreamhot.kaidelivery.ui.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery.model.Delivery.GoogleMapDTO
import app.icecreamhot.kaidelivery.model.Delivery.Order
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.ui.Alert.FoodDetailDialog
import app.icecreamhot.kaidelivery.ui.chat.ChatFragment
import app.icecreamhot.kaidelivery.ui.order.OrderDoned
import app.icecreamhot.kaidelivery.utils.BASE_URL_EMPLOYEE_IMG
import app.icecreamhot.kaidelivery.utils.BASE_URL_USER_IMG
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class TrackingMapFragment : Fragment() {

    private var mMap: GoogleMap? = null
    lateinit var mMapView: MapView
    lateinit var txtStatusOrder: TextView
    lateinit var btnOrderDetail: Button
    lateinit var txtEmployeeName: TextView
    lateinit var imgEmployee: CircleImageView
    lateinit var imgChatButton: ImageButton

    private var disposable: Disposable? = null
    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private lateinit var restaurant: LatLng
    private lateinit var endpoint: LatLng
    private var markerEmployee: Marker? = null
    private lateinit var markerRestaurant: Marker
    private lateinit var markerEndpoint: Marker
    private var polyline: Polyline? = null

    private lateinit var ref: DatabaseReference
    private var order_name:String? = null
    private var order_status: Int? = null

    lateinit var mOrder: ArrayList<app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.Order>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_traking_map_fragment, container, false)
        mMapView = view.findViewById(R.id.mapView)
        txtStatusOrder = view.findViewById(R.id.txtStatusOrder)
        btnOrderDetail = view.findViewById<Button>(R.id.btnOrderDetail)
        txtEmployeeName = view.findViewById(R.id.txtEmployeeName)
        imgEmployee = view.findViewById(R.id.imgEmployee)
        imgChatButton = view.findViewById(R.id.imgChat)

        mMapView.onCreate(savedInstanceState)

        mMapView.onResume()

        mMapView.getMapAsync(object: OnMapReadyCallback  {
            override fun onMapReady(googleMap: GoogleMap?) {
                mMap = googleMap
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(ContextCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap?.isMyLocationEnabled = true
                    }
                }
                else {
                    mMap?.isMyLocationEnabled = true
                }
                loadDeliveryNow()
            }
        })

        btnOrderDetail.setOnClickListener {
            val dialogDetailFragment = FoodDetailDialog.newInstance(mOrder)
            dialogDetailFragment.show(fragmentManager, "OrderDetailFragment")
        }

        return view
    }

    private fun loadDeliveryNow() {
        disposable = orderAPI.getDeliveryNow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result ->
                        getOrderStatus(result.orderList)
                        loadOrderDetail(result.orderList?.get(0)?.order_id)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun loadOrderDetail(order_id: Int?) {
        disposable = orderAPI.getOrderAndOrderDetail(order_id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> mOrder = result.data
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun getOrderStatus(orderList: ArrayList<Order>?) {
        order_name = orderList!!.get(0).order_name

        order_name?.let {
            ref = FirebaseDatabase.getInstance().getReference("Delivery")
            ref.child(it).child("status").addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value == null) {
                        val goFragement = OrderDoned.newInstance(orderList.get(0).order_name)
                        val fm = fragmentManager
                        fm?.beginTransaction()
                        ?.replace(R.id.contentContainer, goFragement)
                        ?.commitAllowingStateLoss()
                    } else {
                        val employeeName = "${orderList.get(0).employee?.emp_name} ${orderList.get(0).employee?.emp_lastname}"
                        val getEmployeeImg = orderList.get(0).employee?.emp_avatar
                        val employeeImg = BASE_URL_EMPLOYEE_IMG + if(getEmployeeImg == null) "noimg.png" else getEmployeeImg
                        val getUserImg = orderList?.get(0)?.user?.avatar
                        val userImg = BASE_URL_USER_IMG + if(getUserImg == null) "noimg.png" else getUserImg

                        order_status = p0.getValue(Int::class.java)
                        setMarkerRestaurant()
                        setAllMarker(orderList)
                        setEmployeeData(employeeName, userImg, employeeImg)
                    }
                }
            })
        }
    }

    private fun setEmployeeData(employeeName: String, userImg: String, employeeImg: String) {
        txtEmployeeName.text = employeeName
        Glide
            .with(activity!!)
            .load(employeeImg)
            .into(imgEmployee)
        imgChatButton.setOnClickListener {
            order_name?.let {
                val chatFragment = ChatFragment.newInstance(it, userImg, employeeImg)

                val transaction= fragmentManager
                transaction?.beginTransaction()
                    ?.replace(R.id.contentContainer, chatFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }

        }
    }

    private fun setAllMarker(orderList: ArrayList<Order>?) {
        restaurant = LatLng(orderList!!.get(0).restaurant!!.res_lat, orderList.get(0).restaurant!!.res_lng)
        endpoint = LatLng(orderList.get(0).endpoint_lat, orderList.get(0).endpoint_lng)

        val markerOptionRestaurant = MarkerOptions()
        markerOptionRestaurant.position(restaurant)
        markerRestaurant = mMap!!.addMarker(markerOptionRestaurant)
        markerRestaurant.title = orderList.get(0).restaurant!!.res_name
        markerRestaurant.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        val markerOptionEndpoint = MarkerOptions()
        markerOptionEndpoint.position(endpoint)
        markerEndpoint = mMap!!.addMarker(markerOptionEndpoint)
        markerEndpoint.title = orderList.get(0).endpoint_name
        markerEndpoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    }

    private fun setMarkerRestaurant() {
        ref = FirebaseDatabase.getInstance().getReference("Delivery")
        ref.child(order_name!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("err", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(markerEmployee != null) {
                    markerEmployee?.remove()
                }

                if(p0.value != null) {
                    val empLatLng = p0.getValue(OrderFB::class.java)

                    val now = LatLng(empLatLng!!.latitude, empLatLng.longitude)

                    if(markerEmployee == null) {
                        val markerOptionEmployee = MarkerOptions()
                        markerOptionEmployee.position(now).title("Employee").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        markerEmployee = mMap!!.addMarker(markerOptionEmployee)
                    } else {
                        markerEmployee?.remove()
                        val markerOptionEmployee = MarkerOptions()
                        markerOptionEmployee.position(now).title("Employee").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        markerEmployee = mMap!!.addMarker(markerOptionEmployee)
                    }

                    mMap?.uiSettings?.isZoomControlsEnabled = true
                    mMap?.setMinZoomPreference(11f)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(now))
                    mMap?.animateCamera(CameraUpdateFactory.zoomTo(17f))

                    order_status?.let {
                        if(it == 3 || it == 4) {
                            if(it == 3) {
                                txtStatusOrder.text = "สถานะ : กำลังจัดส่งอาหาร"
                            } else if(it == 4) {
                                txtStatusOrder.text = "สถานะ : ถึงแล้ว"
                            }
                            val url = getURL(now, endpoint)
                            GetDirection(url).execute()
                            markerRestaurant.remove()
                        } else {
                            if(it == 1) {
                                txtStatusOrder.text = "สถานะ : รับออเดอร์แล้ว"
                            } else if(it == 2) {
                                txtStatusOrder.text = "สถานะ : กำลังรออาหาร"
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${from.latitude},${from.longitude}&destination=${to.latitude},${to.longitude}&sensor=false&mode=driving&key=AIzaSyDCkgDceoiSbeWa29pNeJxmsNipUF7P3uw"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e: Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            if(polyline != null) {
                polyline?.remove()
            }
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            polyline = mMap!!.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}
