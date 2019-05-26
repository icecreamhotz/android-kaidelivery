package app.icecreamhot.kaidelivery.ui.Alert

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderHistory
import app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.OrderDetail
import app.icecreamhot.kaidelivery.ui.Adapter.FoodDetailAndEditAdapter
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FoodDetailsAndEditDialog: DialogFragment() {

    var disposable: Disposable? = null
    lateinit var mFood: OrderHistory
    private var pref: SharedPreferences? = null

    companion object {
        fun newInstance(order: OrderHistory): FoodDetailsAndEditDialog {
            val fragment = FoodDetailsAndEditDialog()
            fragment.mFood = order
            return fragment
        }
    }

    private lateinit var listFood: RecyclerView
    private lateinit var txtFullname: TextView
    private lateinit var txtOrderDetail: TextView
    private lateinit var txtAddressName: TextView
    private lateinit var txtAddressDetail: TextView
    private lateinit var txtOrderCreate: TextView
    private lateinit var txtOrderStart: TextView
    private lateinit var txtFoodPrice: TextView
    private lateinit var txtDeliveryPrice: TextView
    private lateinit var txtAllPrice: TextView
    private lateinit var headerTotal: TextView
    private lateinit var btnOk: Button

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pref = context?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle("รายการอาหาร")
        val view = inflater.inflate(R.layout.alert_fragment_detail_food_edit, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View) {
        val foodPrice = mFood.orderDetailsPrice
        val deliveryPrice = mFood.order_deliveryprice

        val allPrice = if(mFood.order_price != null) mFood.order_price!! + deliveryPrice else  foodPrice + deliveryPrice

        listFood = view.findViewById(R.id.listDetailFoodEdit)
        txtFullname = view.findViewById(R.id.txtCusFullname)
        txtOrderDetail = view.findViewById(R.id.txtCusFoodDetails)
        txtAddressName = view.findViewById(R.id.txtCusAddressName)
        txtAddressDetail = view.findViewById(R.id.txtCusAddressDetails)
        txtOrderCreate = view.findViewById(R.id.txtCusOrderSend)
        txtOrderStart = view.findViewById(R.id.txtCusOrderStart)
        txtFoodPrice = view.findViewById(R.id.txtFoodPrice)
        txtDeliveryPrice = view.findViewById(R.id.txtDeliveryPrice)
        txtAllPrice = view.findViewById(R.id.txtAllPrice)
        headerTotal = view.findViewById(R.id.headerTotal)
        btnOk = view.findViewById(R.id.btnOkOrderDetail)

        txtFullname.text = "${mFood.employee?.emp_name} ${mFood.employee?.emp_lastname}"
        txtOrderDetail.text = if (mFood.endpoint_details == null) "ไม่มี" else mFood.endpoint_details
        txtAddressName.text = mFood.endpoint_name
        txtAddressDetail.text = if (mFood.order_details == null) "ไม่มี" else mFood.order_details
        txtOrderCreate.text = mFood.created_at
        txtOrderStart.text = mFood.order_start

        txtFoodPrice.text = String.format("%.2f", foodPrice)
        txtDeliveryPrice.text = String.format("%.2f", deliveryPrice)
        headerTotal.text = if(mFood.order_price == null) activity?.resources?.getString(R.string.total) else activity?.resources?.getString(R.string.totalcustom)
        txtAllPrice.text = "${if(mFood.order_price != null) "(${mFood.order_price!!})" else  ""} ${String.format("%.2f", allPrice)}"

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        val foodDetailAndEditAdapter = FoodDetailAndEditAdapter(mFood.orderDetail)

        listFood.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = foodDetailAndEditAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

}