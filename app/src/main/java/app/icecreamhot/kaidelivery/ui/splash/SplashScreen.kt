package app.icecreamhot.kaidelivery.ui.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.MainActivity
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.ui.MainFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS

class SplashScreen: Activity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var delay_time: Long? = null
    private var time: Long = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.splash_screen)

        handler = Handler()

        runnable = Runnable {
            checkAuthentication()
        }
    }

    private fun checkAuthentication() {
        val pref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val token = pref.getString("token", null)
        if(token != null) {
            val intent = Intent(applicationContext, MainFragment::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        delay_time = time
        delay_time?.let {
            handler.postDelayed(runnable, it)
        }
        time = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        delay_time?.let {
            time = it - (System.currentTimeMillis() - time)
        }
    }
}