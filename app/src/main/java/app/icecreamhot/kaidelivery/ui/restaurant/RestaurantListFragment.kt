package app.icecreamhot.kaidelivery.ui.restaurant

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Delivery.Order
import app.icecreamhot.kaidelivery.model.Restaurant
import app.icecreamhot.kaidelivery.model.RestaurantType
import app.icecreamhot.kaidelivery.model.mLatitude
import app.icecreamhot.kaidelivery.model.mLongitude
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.network.RestaurantAPI
import app.icecreamhot.kaidelivery.network.RestaurantTypesAPI
import app.icecreamhot.kaidelivery.ui.food.FoodFragment
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment
import app.icecreamhot.kaidelivery.ui.order.WatingOrderFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS
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

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var googleApiClient: GoogleApiClient? = null
    val REQUEST_CODE = 1000

    lateinit var mLocationRequest: LocationRequest

    private val restaurantAPI by lazy { RestaurantAPI.create() }
    private val restaurantTypeAPI by lazy { RestaurantTypesAPI.create() }

    private var disposable: Disposable? = null

    lateinit var recyclerView: RecyclerView
    lateinit var totalEmployee: TextView
    lateinit var edtMinPrice: EditText

    private var minPrice: Double? = null

    private var pref: SharedPreferences? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pref = context?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_restaurant_list, container, false)
        recyclerView  = view.findViewById(R.id.restaurantList)
        totalEmployee = view.findViewById(R.id.txtEmployeeTotal)
        edtMinPrice = view.findViewById(R.id.edtMinPrice)

        googleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
        getEmployeeTotal()

        edtMinPrice.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(value: Editable?) {
                minPrice = value.toString().toDoubleOrNull()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkDeliveryIsExist()
    }

    private fun checkDeliveryIsExist() {
        val token = pref?.getString("token", null)
        Log.d("jwttoken", token)
        token?.let {
            disposable = orderAPI.getDeliveryNow(it)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrieveRestaurantListStart() }
                .doOnTerminate { onRetrieveRestaurantListFinish() }
                .subscribe(
                    {
                            result -> afterCheckOrderExist(result.orderList?.get(0))

                    },
                    {
                            err ->  Log.d("errza", err.message)
                    }
                )
        }
    }

    private fun afterCheckOrderExist(orderList: Order?) {
        if(orderList?.order_name != null) {
            var goFragement = Fragment()
            when(orderList.order_status) {
                "0" -> {
                    goFragement = WatingOrderFragment.newInstance(orderList.order_id, orderList.order_name)
                }
                "1", "2", "3" -> {
                    goFragement = TrackingMapFragment()
                }
            }
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.replace(R.id.contentContainer, goFragement)
                ?.commitAllowingStateLoss()
        } else {
            loadRestaurantTypes()
        }
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
        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startLocationUpdates()

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                    location ->
                if(location != null) {
                    mLatitude = location.latitude
                    mLongitude = location.longitude
                }
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

        RestaurantListAdapterFragment.onItemClick = { resItem ->
            val foodFragment = FoodFragment.newInstance(resItem?.res_id, minPrice)

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

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}