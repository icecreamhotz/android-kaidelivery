package app.icecreamhot.kaidelivery.ui.order

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.network.EmployeeAPI
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.ui.Alert.Dialog
import app.icecreamhot.kaidelivery.ui.history.HistoryOrderFragment
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WatingOrderFragment: Fragment() {

    companion object {
        fun newInstance(order_id: Int, order_name: String): WatingOrderFragment {
            val fragment = WatingOrderFragment()
            val args = Bundle()
            args.putString("order_name", order_name)
            args.putInt("order_id", order_id)
            fragment.arguments = args
            return fragment
        }
    }

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private var disposable: Disposable? = null

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private lateinit var ref: DatabaseReference

    private lateinit var txtWaitingOrder: TextView
    private lateinit var txtEmployeeTotal: TextView
    private lateinit var txtBeforeMyQueue: TextView
    private lateinit var txtMyQueue: TextView
    private lateinit var btnCancelOrder: Button
    private lateinit var loading: ProgressBar

    private var order_name = ""
    private var order_id = 0

    private lateinit var mListener: ValueEventListener
    private var pref: SharedPreferences? = null

    var dotText = -1

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString("order_name")?.let {
            order_name = it
        }
        arguments?.getInt("order_id")?.let {
            order_id = it
        }
        pref = context?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_waiting_order, container, false)
        txtWaitingOrder = view.findViewById(R.id.txtWaitingOrder)
        txtEmployeeTotal = view.findViewById(R.id.txtEmployeeIdle)
        txtBeforeMyQueue = view.findViewById(R.id.txtBeforeMyQueue)
        txtMyQueue = view.findViewById(R.id.txtMyQueue)
        btnCancelOrder = view.findViewById(R.id.btnCancelOrder)
        loading = view.findViewById(R.id.loading)

        mHandler = Handler()

        mRunnable = Runnable {
            dotText++

            if(dotText == 0) {
                txtWaitingOrder.text = activity?.resources?.getString(R.string.orderwaittextone)
            } else if(dotText == 1) {
                txtWaitingOrder.text = activity?.resources?.getString(R.string.orderwaittexttwo)
            } else if(dotText == 2) {
                txtWaitingOrder.text = activity?.resources?.getString(R.string.orderwaittextthree)
                dotText = -1
            }
            Log.d("data", dotText.toString())

            mHandler.postDelayed(
                mRunnable,
                1000
            )
        }

        mHandler.postDelayed(
            mRunnable,
            1000
        )

        loadEmployeeTotal()
        getMyQueueAndTotalQueue()
        triggerConfirmedOrder()
        triggerCancelOrder()

        btnCancelOrder.setOnClickListener(setOnClickDeclineOrder)

        return view
    }

    private val setOnClickDeclineOrder = View.OnClickListener { _ ->
        val dialogConfirm = Dialog()
        dialogConfirm.Confirm(activity, "Calcel Order", "Do you need cancel this order really ?"
            ,"Yes", "No", Runnable {
                val token = pref?.getString("token", null)
                token?.let {
                    disposable = orderAPI.updateStatusOrder(
                        order_id,
                        5,
                        null,
                        null,
                        null,
                        it
                    )
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { loading.visibility = View.VISIBLE }
                        .doOnTerminate { loading.visibility = View.GONE }
                        .subscribe(
                            {
                                deleteOrderFromFirebase(order_name)
                            },
                            { err ->
                                Log.d("err", err.message)
                            }
                        )
                }
            }, Runnable {
                null
            })
    }

    private fun deleteOrderFromFirebase(order_name: String) {
        ref = FirebaseDatabase.getInstance().getReference("Orders").child(order_name)
        ref.removeValue().addOnSuccessListener {
            Toast.makeText(context, "Cancel Success", Toast.LENGTH_LONG).show()
            backToHistoryFragment()
        }
    }


    private fun loadEmployeeTotal() {
        ref = FirebaseDatabase.getInstance().getReference("Employees")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var notWorking = 0
                for (child in p0.getChildren()) {
                    when(child.child("status").getValue(Int::class.java)) {
                        0 -> notWorking++
                    }
                }
                txtEmployeeTotal.text = "จำนวนพนักงานที่ว่าง ${notWorking} คน"
            }

        })
    }

    private fun getMyQueueAndTotalQueue() {
        ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var position = 0
                for (child in p0.getChildren()) {
                    val getOrderName = child.key
                    if(order_name == getOrderName) {
                        txtBeforeMyQueue.text = "คิวก่อนหน้าคุณคือ ${position}"
                        txtMyQueue.text = "คิวของคุณคือ ${position + 1}"
                    } else {
                        position++
                    }
                }
            }

        })
    }

    private fun triggerConfirmedOrder() {
        ref = FirebaseDatabase.getInstance().getReference("Delivery")
        ref.child(order_name).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.value?.let {
                    val mapFragment = TrackingMapFragment()
                    val fm = fragmentManager
                    fm?.beginTransaction()
                        ?.replace(R.id.contentContainer, mapFragment)
                        ?.commit()
                    Log.d("aluna", p0.value.toString())
                }
            }

        })
    }

    private fun triggerCancelOrder() {
        ref = FirebaseDatabase.getInstance().getReference("Orders")
        mListener = ref.child(order_name).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value == null) {
                    ifCancelOrder()
                }
            }

        })
    }

    private fun ifCancelOrder() {
        ref = FirebaseDatabase.getInstance().getReference("Delivery")
        if(order_name != null) {
            ref.child(order_name).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value == null) {
                        disposable = orderAPI.getOrderAndOrderDetail(order_id!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            //            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                            //            .doOnTerminate { loadingOrder.visibility = View.GONE }
                            .subscribe(
                                {
                                        result ->
                                    Log.d("test", result.toString())
                                    val order = result.data.get(0).order_status
                                    if(order == "5" && result.data.get(0).order_statusdetails != "เกิดเหตุฉุกเฉิน") {
                                        Toast.makeText(context, "พนักงานได้ยกเลิกออเดอร์ของคุณ", Toast.LENGTH_LONG).show()
                                        backToHistoryFragment()
                                    }
                                },
                                {
                                        err -> Log.d("err", err.message)
                                }
                            )
                    }
                }
            })
        }
    }

    private fun backToHistoryFragment() {
        val goFragment = HistoryOrderFragment()
        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.contentContainer, goFragment)
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacks(mRunnable)
        ref.child(order_name).removeEventListener(mListener)
        disposable?.dispose()
    }

}