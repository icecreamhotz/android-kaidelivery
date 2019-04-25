package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery.model.MenuList
import app.icecreamhot.kaidelivery.model.OrderList
import app.icecreamhot.kaidelivery.network.OrderAPI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.mukesh.OtpView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_otp.*

class OTPFragment: Fragment() {

    private val orderAPI by lazy { OrderAPI.create() }

    private var disposable: Disposable? = null
    private var order: MenuList? = null

    companion object {
        fun newInstance(menuList: MenuList) = OTPFragment().apply {
            arguments = Bundle().apply {
                putParcelable("order", menuList)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<MenuList>("order")?.let {
            order = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_otp, container, false)
        val btnSendOTP = view.findViewById<Button>(R.id.btnRequestOTP)
        val otpView = view.findViewById<OtpView>(R.id.otp_view)

        val listenerOtpSend = View.OnClickListener { _ ->
            onSendOTPClick()
        }

        btnSendOTP.setOnClickListener(listenerOtpSend)

        otpView.setOtpCompletionListener { otp ->
            if(otp.length == 6) {
                disposable = orderAPI.checkValidOTP(otp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { onSendOTPStart() }
                    .subscribe(
                        {
                            onValidOTPSuccess()
                        },
                        {
                            onValidOTPError()
                        }
                    )}
        }

        return view
    }

    private fun onSendOTPClick() {
        if(edtTelephone.text.length < 10) {
            onSnack(activityotp_view, resources.getString(R.string.pleaseinputtelephoneagain))
        } else {
            disposable = orderAPI.sendOTPToUser(edtTelephone.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onSendOTPStart() }
                .doOnTerminate { onSendOTPEnd() }
                .subscribe(
                    {
                        onSendOTPSuccess()
                    }, {
                        onSendOTPError()
                    }
                )
        }
    }

    private fun onSendOTPStart() {
        loadingOTP.visibility = View.VISIBLE
    }

    private fun onSendOTPEnd() {
        loadingOTP.visibility = View.GONE
    }

    private fun onSendOTPSuccess() {
        onSnack(activityotp_view, resources.getString(R.string.sendotpsuccess))
    }

    private fun onSendOTPError() {
        onSnack(activityotp_view, resources.getString(R.string.somethinghaswrong))
    }

    private fun onValidOTPSuccess() {
        disposable = orderAPI.insertOrder(order!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate { onSendOTPEnd() }
            .subscribe(
                {
                        result -> onAfterValidOTPSuccess(result)
                },
                {
                        err -> Log.d("errotp", err.toString())
                }
            )
    }

    private fun onAfterValidOTPSuccess(orderList: OrderList) {
        val ref = FirebaseDatabase.getInstance().getReference("Orders")

        val order = OrderFB(0.0, 0.0)

        ref.child(orderList.orderName).setValue(order).addOnCompleteListener {
//            val manager = activity?.getSupportFragmentManager()
//            manager?.let {
//                val first = manager?.getBackStackEntryAt(0)
//                manager?.popBackStack(first!!.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            }

            val successFragment = SuccessFragment.newInstance(orderList.orderID, orderList.orderName)
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.replace(R.id.contentContainer, successFragment)
                ?.commit()
        }
    }

    private fun onValidOTPError() {
        onSnack(activityotp_view, resources.getString(R.string.wrongotp))
        onSendOTPEnd()
    }

    private fun onSnack(view: View, message: String) {
        val snackbar = Snackbar.make(view, message,
            Snackbar.LENGTH_LONG).setAction(resources.getString(R.string.retryth), null)

        snackbar.show()

        if(loadingOTP.visibility == View.VISIBLE)
            View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}