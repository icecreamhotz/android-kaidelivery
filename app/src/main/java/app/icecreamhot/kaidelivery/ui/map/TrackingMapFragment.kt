package app.icecreamhot.kaidelivery.ui.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery.model.Delivery.GoogleMapDTO
import app.icecreamhot.kaidelivery.model.Delivery.Order
import app.icecreamhot.kaidelivery.network.OrderAPI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class TrackingMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var disposable: Disposable? = null
    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private lateinit var endpoint: LatLng
    private lateinit var markerEmployee: Marker
    private lateinit var markerEndpoint: Marker
    private var polyline: Polyline? = null

    private lateinit var ref: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        return inflater.inflate(R.layout.activity_traking_map_fragment, container, false)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
        else {
            mMap.isMyLocationEnabled = true
        }
        loadDeliveryNow()
    }

    private fun loadDeliveryNow() {
        disposable = orderAPI.getDeliveryNow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> setMarkerRestaurant(result.orderList)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun setMarkerRestaurant(orderList: ArrayList<Order>?) {

            endpoint = LatLng(orderList!!.get(0)!!.restaurant!!.res_lat, orderList!!.get(0)!!.restaurant!!.res_lng)

            ref = FirebaseDatabase.getInstance().getReference("Delivery")

            ref.child(orderList!!.get(0)!!.order_name).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val empLatLng = p0.getValue(OrderFB::class.java)
                    Log.d("latlng", empLatLng.toString())
                    val now = LatLng(empLatLng!!.latitude, empLatLng!!.longitude)

                    val markerOptionEmployee = MarkerOptions()
                    markerOptionEmployee.position(now)
                    markerEmployee = mMap.addMarker(markerOptionEmployee)
                    markerEmployee.title = "Employee"
                    markerEmployee.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                    mMap.uiSettings.isZoomControlsEnabled = true
                    mMap.setMinZoomPreference(11f)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(now))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))

                    val url = getURL(now, endpoint)
                    GetDirection(url).execute()
                }
            })

            val markerOptionRestaurant = MarkerOptions()
            markerOptionRestaurant.position(endpoint)
            markerEndpoint = mMap.addMarker(markerOptionRestaurant)
            markerEndpoint.title = orderList!!.get(0)!!.restaurant!!.res_name
            markerEndpoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

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
            polyline?.let {
                it.remove()
            }
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            polyline = mMap.addPolyline(lineoption)
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
}
