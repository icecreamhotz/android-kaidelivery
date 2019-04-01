package app.icecreamhot.kaidelivery.ui.map

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import app.icecreamhot.kaidelivery.ui.food.ConfirmFoodActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btAcceptLocation.setOnClickListener {
            val intent = Intent(applicationContext, ConfirmFoodActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
        else {
            mMap.isMyLocationEnabled = true
        }

        mMap.clear()

        mMap.apply {
            uiSettings.isZoomControlsEnabled = true
            val latlng = LatLng(mLatitude, mLongitude)

            moveCamera(CameraUpdateFactory.newLatLng(latlng))
            animateCamera(CameraUpdateFactory.zoomTo(17f))

            setOnCameraMoveListener(this@MapsActivity)
            setOnCameraIdleListener(this@MapsActivity)
        }
    }

    override fun onCameraMove() {
        mMap.clear()
    }

    override fun onCameraIdle() {
        mLatitude = mMap.cameraPosition.target.latitude
        mLongitude = mMap.cameraPosition.target.longitude

        try {
            val geocoder = Geocoder(this.applicationContext, Locale.getDefault())
            val address: List<Address> = geocoder.getFromLocation(mLatitude, mLongitude, 1)
            if(address.isEmpty()) {
                txtResultAddressMap.text = resources.getString(R.string.loadinglocation)
            } else {
                if(address.size > 0) {
                    val location = address.get(0).getAddressLine(0)
                    txtResultAddressMap.text = location
                }
            }
        } catch(e:Exception) {
            e.printStackTrace()
        }
    }

}
