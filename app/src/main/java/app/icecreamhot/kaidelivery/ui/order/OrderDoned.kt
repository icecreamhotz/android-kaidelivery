package app.icecreamhot.kaidelivery.ui.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderRate
import app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.Order
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.utils.BASE_URL_EMPLOYEE_IMG
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Text

class OrderDoned: Fragment() {

    companion object {
        fun newInstance(order_name: String): OrderDoned {
            val fragment = OrderDoned()
            val args = Bundle()
            args.putString("order_name", order_name)
            fragment.arguments = args
            return fragment
        }
    }

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private var disposable: Disposable? = null
    private var order_name = ""

    lateinit var empImage: CircleImageView
    lateinit var empName: TextView
    lateinit var restaurantName: TextView
    lateinit var orderEndpointName: TextView
    lateinit var foodPrice: TextView
    lateinit var deliveryPrice: TextView
    lateinit var txtAllPrice: TextView
    lateinit var rateStar: RatingBar
    lateinit var edtScore: EditText
    lateinit var btnSave: Button

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString("order_name")?.let {
            order_name = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rate_star, container, false)
        empImage = view.findViewById(R.id.empImage)
        empName = view.findViewById(R.id.empName)
        restaurantName = view.findViewById(R.id.orderRestaurant)
        orderEndpointName = view.findViewById(R.id.orderEndpoint)
        foodPrice = view.findViewById(R.id.txtCalFood)
        deliveryPrice = view.findViewById(R.id.txtCalDelivery)
        txtAllPrice = view.findViewById(R.id.txtCalFoodAndDelivery)
        rateStar = view.findViewById(R.id.ratingBar)
        edtScore = view.findViewById(R.id.edtComplaint)
        btnSave = view.findViewById(R.id.btnSaveRate)

        loadOrderIsDoned()

        return view
    }

    private fun loadOrderIsDoned() {
        disposable = orderAPI.getOrderIsDoned(order_name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> setValueToView(result.data)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun setValueToView(data: ArrayList<OrderRate>) {
        val newData = data.get(0)

        var fPrice = 0.0
        val dPrice = newData.order_deliveryprice

        for(calprice in newData.order_detail) {
            fPrice += calprice.orderdetail_price.toDouble() * calprice.orderdetail_total
        }

        val aPrice = fPrice + dPrice

        Glide.with(this)
            .load("${BASE_URL_EMPLOYEE_IMG}${newData.employee.emp_avatar}")
            .into(empImage)
        empName.text = "${newData.employee.emp_name} ${newData.employee.emp_lastname}"
        restaurantName.text = newData.restaurant.res_name
        orderEndpointName.text = newData.endpoint_name
        foodPrice.text = String.format("%.2f", fPrice)
        deliveryPrice.text = String.format("%.2f", dPrice)
        txtAllPrice.text = String.format("%.2f", aPrice)
    }

}