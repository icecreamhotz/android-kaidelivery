package app.icecreamhot.kaidelivery.ui.restaurant

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.databinding.ActivityRestaurantListBinding
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class RestaurantListActivity: AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: ActivityRestaurantListBinding
    private lateinit var viewModel: RestaurantListViewModel
    private var errorSnackbar: Snackbar? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CODE = 1000

    private lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

    }

    private fun showError(@StringRes errorMessage:Int){
        errorSnackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, viewModel.errorClickListener)
        errorSnackbar?.show()
    }

    private fun hideError(){
        errorSnackbar?.dismiss()
    }

    override fun onStart() {
        super.onStart()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    override fun onConnected(p0: Bundle?) {
         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
         }
        startLocationUpdates()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                location: Location? ->
                mLatitude = location!!.latitude
                mLongitude = location!!.longitude

                binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_list)
                binding.restaurantList.layoutManager = LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
                )
                binding.restaurantList.isNestedScrollingEnabled = false

                viewModel = ViewModelProviders.of(this).get(RestaurantListViewModel::class.java)
                viewModel.errorMessage.observe(this, Observer {
                        errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
                })
                binding.viewModel = viewModel
            }
    }

     private fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000)
    }


    override fun onConnectionSuspended(p0: Int) {
        googleApiClient!!.connect();
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        googleApiClient!!.disconnect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE -> {
                if(grantResults.isNotEmpty()) {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}