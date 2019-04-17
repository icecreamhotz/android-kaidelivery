package app.icecreamhot.kaidelivery.ui.map

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import app.icecreamhot.kaidelivery.ui.food.ConfirmFoodActivity
import app.icecreamhot.kaidelivery.ui.food.ConfirmFoodFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception
import java.util.*



class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    lateinit var btnAcceptLocation: Button
    private var res_id = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        res_id = arguments!!.getInt("res_id")

        val view = inflater.inflate(R.layout.activity_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        btnAcceptLocation = view.findViewById(R.id.btAcceptLocation)

        btnAcceptLocation.setOnClickListener {
            var confirmFoodFragment = ConfirmFoodFragment()
            var args = Bundle()
            args.putInt("res_id", res_id)
            confirmFoodFragment.arguments = args

            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, confirmFoodFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        return view
    }


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

        mMap.clear()

        mMap.apply {
            uiSettings.isZoomControlsEnabled = true
            mMap.setMinZoomPreference(11f)

            val latlng = LatLng(mLatitude, mLongitude)
            mMap.addMarker(MarkerOptions().position(latlng).title("you"))
            moveCamera(CameraUpdateFactory.newLatLng(latlng))
            animateCamera(CameraUpdateFactory.zoomTo(17f))

            setOnCameraMoveListener(this@MapFragment)
            setOnCameraIdleListener(this@MapFragment)
        }
    }

    override fun onCameraMove() {
        mMap.clear()
    }

    override fun onCameraIdle() {
        mLatitude = mMap.cameraPosition.target.latitude
        mLongitude = mMap.cameraPosition.target.longitude

        try {
            val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
            val address: List<Address> = geocoder.getFromLocation(mLatitude, mLongitude, 1)
            if(address.isEmpty()) {
                txtResultAddressMap.text = resources.getString(R.string.loadinglocation)
            } else {
                if(address.size > 0) {
                    val location = address.get(0).getAddressLine(0)
                    txtResultAddressMap.text = location
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}