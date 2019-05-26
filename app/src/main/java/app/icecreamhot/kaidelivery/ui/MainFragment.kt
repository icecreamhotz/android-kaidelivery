package app.icecreamhot.kaidelivery.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.MainActivity
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.helper.LocaleHelper
import app.icecreamhot.kaidelivery.ui.Adapter.ExpandableLanguageAdapter
import app.icecreamhot.kaidelivery.ui.history.HistoryOrderFragment
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment
import app.icecreamhot.kaidelivery.ui.profile.ChangePasswordFragment
import app.icecreamhot.kaidelivery.ui.profile.ProfileFragment
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.left_side_drawer_layout.*
import java.io.IOException

class MainFragment : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "fcmToken"
    private lateinit var ref: DatabaseReference
    val header: MutableList<String> = ArrayList()
    val body: MutableList<MutableList<String>> = ArrayList()

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
        setContentView(R.layout.left_side_drawer_layout)
        setSupportActionBar(toolbar)

        val language: MutableList<String> = ArrayList()
        language.add("ไทย")
        language.add("English")

        header.add("Chosee Language")
        body.add(language)

        expandableListView.setAdapter(ExpandableLanguageAdapter(this, expandableListView, header, body))

        expandableListView.setOnChildClickListener { expandableListView, view, groupPosition, childPosition, l ->
            if(childPosition == 0) {
                LocaleHelper.setLocale(this, "th")
                recreate()
            } else {
                LocaleHelper.setLocale(this, "en")
                recreate()
            }
            false}

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
        val toggle = ActionBarDrawerToggle(Activity(), drawer_layout, toolbar, 0, R.string.character_counter_content_description)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.menu1 -> {
                replaceFragment(ProfileFragment())
            }
            R.id.menu2 -> {
                replaceFragment(ChangePasswordFragment())
            }
            R.id.menu3 -> {
                val shared = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
                shared.edit().remove("token").apply()
                Log.d("logout", shared.getString("token", "not"))
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
