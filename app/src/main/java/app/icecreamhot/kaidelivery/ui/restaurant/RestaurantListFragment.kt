package app.icecreamhot.kaidelivery.ui.restaurant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Restaurant
import app.icecreamhot.kaidelivery.model.RestaurantType
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import app.icecreamhot.kaidelivery.network.RestaurantAPI
import app.icecreamhot.kaidelivery.network.RestaurantTypesAPI
import app.icecreamhot.kaidelivery.ui.food.FoodFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_restaurant_list.*

class RestaurantListFragment: Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    val TAG = "RestaurantListFragment"

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var googleApiClient: GoogleApiClient? = null
    val REQUEST_CODE = 1000

    lateinit var mLocationRequest: LocationRequest

    private val restaurantAPI by lazy { RestaurantAPI.create() }
    private val restaurantTypeAPI by lazy { RestaurantTypesAPI.create() }

    private var disposable: Disposable? = null

    lateinit var recyclerView: RecyclerView
    lateinit var totalEmployee: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_restaurant_list, container, false)
        recyclerView  = view!!.findViewById(R.id.restaurantList)
        totalEmployee = view!!.findViewById(R.id.txtEmployeeTotal)

        googleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }

        getEmployeeTotal()

        return view
    }

    private fun getEmployeeTotal() {
        val ref = FirebaseDatabase.getInstance().getReference("Employees")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.childrenCount > 0) {
                    totalEmployee.text = "ออนไลน์ ${p0.childrenCount} คน"
                } else {
                    totalEmployee.text = "ไม่มีพนักงาน"
                }
            }

        })
    }

    override fun onDetach() {
        super.onDetach()
        if (googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startLocationUpdates()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                    location: Location ->
                mLatitude = location.latitude
                mLongitude = location.longitude

                loadRestaurantTypes()
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
                        Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadRestaurantTypes() {
        disposable = restaurantTypeAPI.getRestaurantTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveRestaurantListStart() }
            .doOnTerminate { onRetrieveRestaurantListFinish() }
            .subscribe(
                { result -> loadRestaurants(result.restaurantTypeArrayList) },
                { error -> Log.e("ERROR", error.message) }
            )
    }

    private fun loadRestaurants(restaurantType: List<RestaurantType>?) {
        disposable = restaurantAPI.getRestaurants()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveRestaurantListStart() }
            .doOnTerminate { onRetrieveRestaurantListFinish() }
            .subscribe(
                { result -> setDataAdapter(restaurantType, result.employeeArrayList) },
                { error -> Log.e("ERROR", error.message) }
            )
    }

    private fun setDataAdapter(restaurantType: List<RestaurantType>?, restaurant: List<Restaurant>?) {
        for((index, item) in restaurant!!.withIndex()) {
            var restype_name: MutableList<String> = mutableListOf<String>()
            val replaceType = item.restype_id?.replace("[", "")?.replace("]", "")
                ?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()
            for(type in replaceType.orEmpty()) {
                val typename = restaurantType?.filter { it.restype_id == type.toInt() }
                restype_name.add(typename!!.get(0).restype_name)
            }
            restaurant[index].restype_name = restype_name.toString()
        }

        val RestaurantListAdapterFragment = RestaurantListAdapterFragment(restaurant)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = RestaurantListAdapterFragment
        }

        RestaurantListAdapterFragment.onItemClick = { restaurant ->
            val foodFragment = FoodFragment()
            val arguments = Bundle()
            arguments.putInt("res_id", restaurant!!.res_id)
            foodFragment.arguments = arguments

            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, foodFragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private fun onRetrieveRestaurantListStart() {
        loadingRestaurant.visibility = View.VISIBLE
    }

    private fun onRetrieveRestaurantListFinish() {
        loadingRestaurant.visibility  = View.GONE
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}