package app.icecreamhot.kaidelivery.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.ui.history.HistoryOrderFragment
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment
import app.icecreamhot.kaidelivery.ui.order.OrderDoned
import app.icecreamhot.kaidelivery.ui.order.OrderDonedRestaurant
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main_fragment.*
import java.io.IOException

class MainFragment : AppCompatActivity() {

    private val TAG = "fcmToken"
    private lateinit var ref: DatabaseReference

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        when(item.itemId) {
            R.id.restaurant -> {
                replaceFragment(RestaurantListFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.order -> {
                replaceFragment(TrackingMapFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                replaceFragment(HistoryOrderFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.contentContainer, RestaurantListFragment())
                .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initView()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun initView() {
        Thread(Runnable {
            try {
                val fcmToken = FirebaseInstanceId.getInstance().getToken(getString(R.string.senderid), "FCM")
                fcmToken?.let {
                    Log.i(TAG, it)
                    val pref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
                    val userId = pref.getString("user_id", "")
                    ref = FirebaseDatabase.getInstance().getReference("FCMToken")
                        .child("users")
                        .child(userId)
                    ref.setValue(it)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

}
