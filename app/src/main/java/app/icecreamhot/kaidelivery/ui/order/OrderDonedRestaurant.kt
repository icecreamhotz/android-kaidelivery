package app.icecreamhot.kaidelivery.ui.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderRate
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.ui.history.HistoryOrderFragment
import app.icecreamhot.kaidelivery.utils.BASE_URL_EMPLOYEE_IMG
import app.icecreamhot.kaidelivery.utils.BASE_URL_RESTAURANT_IMG
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OrderDonedRestaurant: Fragment() {

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private var disposable: Disposable? = null
    private var order_id: Int = 0
    private var user_id: Int = 0
    private var res_id: Int = 0

    lateinit var resImg: CircleImageView
    lateinit var restaurantName: TextView
    lateinit var rateStar: RatingBar
    lateinit var edtScore: EditText
    lateinit var btnSave: Button

    companion object {
        fun newInstance(order_id: Int, user_id: Int, res_id: Int) =  OrderDonedRestaurant().apply {
            arguments = Bundle().apply {
                putInt("order_id", order_id)
                putInt("user_id", user_id)
                putInt("res_id", res_id)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("order_id")?.let {
            order_id = it
        }
        arguments?.getInt("user_id")?.let {
            user_id = it
        }
        arguments?.getInt("res_id")?.let {
            res_id = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rate_star_restaurant, container, false)
        resImg = view.findViewById(R.id.resImg)
        restaurantName = view.findViewById(R.id.restaurantName)
        rateStar = view.findViewById(R.id.ratingBar)
        edtScore = view.findViewById(R.id.edtComplaint)
        btnSave = view.findViewById(R.id.btnSaveRate)

        loadOrderIsDoned()

        return view
    }

    private fun loadOrderIsDoned() {
        disposable = orderAPI.getOrderIsDoned(order_id)
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

        Glide.with(this)
            .load("${BASE_URL_RESTAURANT_IMG}${newData.restaurant.res_logo}")
            .into(resImg)

        restaurantName.text = newData.restaurant.res_name

        btnSave.setOnClickListener {
            val comment = edtScore.text.trim().toString()
            val rating = rateStar.rating.toInt()

            if(rating != 0) {
                ifHaveRating(rating, comment)
            } else {
                commitHistoryFragment()
            }
        }
    }

    private fun ifHaveRating(rating: Int, comment: String) {
        disposable = orderAPI.updateRestaurantScoreAfterDelivered(order_id, rating, comment, user_id, res_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> if(result.status)  {
                            commitHistoryFragment()
                        }
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun commitHistoryFragment() {
        val historyOrderFragment = HistoryOrderFragment()

        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.contentContainer, historyOrderFragment)
            ?.commit()
    }

}