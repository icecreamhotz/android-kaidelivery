package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery.model.MenuList
import app.icecreamhot.kaidelivery.model.Order
import app.icecreamhot.kaidelivery.model.OrderList
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_otp.*

class OTPActivity : AppCompatActivity() {

//    private val orderAPI by lazy { OrderAPI.create() }
//
//    private var disposable: Disposable? = null
//    private var order: MenuList? = null
//
//    private val pref = activity?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_otp)
//
//
//        order = intent.getParcelableExtra("order")
//
//        val listenerOtpSend = View.OnClickListener { _ ->
//            onSendOTPClick()
//        }
//
//        btnRequestOTP.setOnClickListener(listenerOtpSend)
//
//        otp_view.setOtpCompletionListener { otp ->
//            if(otp.length == 6) {
//                disposable = orderAPI.checkValidOTP(otp)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { onSendOTPStart() }
//                .subscribe(
//                    {
//                        onValidOTPSuccess()
//                    },
//                    {
//                        onValidOTPError()
//                    }
//                )}
//        }
//    }
//
//    private fun onSendOTPClick() {
//        if(edtTelephone.text.length < 10) {
//            onSnack(activityotp_view, resources.getString(R.string.pleaseinputtelephoneagain))
//        } else {
//            disposable = orderAPI.sendOTPToUser(edtTelephone.text.toString())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { onSendOTPStart() }
//                .doOnTerminate { onSendOTPEnd() }
//                .subscribe(
//                    {
//                        onSendOTPSuccess()
//                    }, {
//                        onSendOTPError()
//                    }
//                )
//        }
//    }
//
//    private fun onSendOTPStart() {
//        loadingOTP.visibility = View.VISIBLE
//    }
//
//    private fun onSendOTPEnd() {
//        loadingOTP.visibility = View.GONE
//    }
//
//    private fun onSendOTPSuccess() {
//        onSnack(activityotp_view, resources.getString(R.string.sendotpsuccess))
//    }
//
//    private fun onSendOTPError() {
//        onSnack(activityotp_view, resources.getString(R.string.somethinghaswrong))
//    }
//
//    private fun onValidOTPSuccess() {
//        disposable = orderAPI.insertOrder(order!!)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnTerminate { onSendOTPEnd() }
//            .subscribe(
//                {
//                    result -> onAfterValidOTPSuccess(result)
//                },
//                {
//                    onValidOTPError()
//                }
//            )
//    }
//
//    private fun onAfterValidOTPSuccess(orderList: OrderList) {
//        val ref = FirebaseDatabase.getInstance().getReference("Orders")
//
////        val order = OrderFB(83, orderList.orderID)
////
////        ref.child(orderList.orderName).setValue(order).addOnCompleteListener {
////            val intent = Intent(this, SuccessOrderActivity::class.java)
////            startActivity(intent)
////        }
//    }
//
//    private fun onValidOTPError() {
//        onSnack(activityotp_view, resources.getString(R.string.somethinghaswrong))
//    }
//
//    private fun onSnack(view: View, message: String) {
//        val snackbar = Snackbar.make(view, message,
//            Snackbar.LENGTH_LONG).setAction(resources.getString(R.string.retryth), null)
//
//        snackbar.show()
//
//        if(loadingOTP.visibility == View.VISIBLE)
//            View.GONE
//    }
//
//    override fun onPause() {
//        super.onPause()
//        disposable?.dispose()
//    }
}
