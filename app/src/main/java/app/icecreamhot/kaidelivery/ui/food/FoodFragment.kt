package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.data.menu
import app.icecreamhot.kaidelivery.data.totalPrice
import app.icecreamhot.kaidelivery.model.Food
import app.icecreamhot.kaidelivery.model.FoodType
import app.icecreamhot.kaidelivery.network.FoodAPI
import app.icecreamhot.kaidelivery.network.FoodTypeAPI
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_food_list.*
import kotlin.math.min

class FoodFragment : Fragment() {

    private val foodAPI by lazy { FoodAPI.create() }
    private val foodTypeAPI by lazy { FoodTypeAPI.create() }

    private var disposable: Disposable? = null
    var foodTypeList: List<FoodType>? = null
    var foodList: List<Food>? = null
    private val sectionAdapter = SectionedRecyclerViewAdapter()
    var hasData: Boolean = false

    private var res_id:Int = 0
    private var min_price: Double = 0.0
    lateinit var recyclerView: RecyclerView
    lateinit var txtMinPrice: TextView
    lateinit var txtMenuTotal: TextView
    lateinit var txtPriceTotal: TextView
    lateinit var cardMenuDetail: CardView

    companion object {
        fun newInstance(res_id: Int?, min_price: Double?) = FoodFragment().apply {
            arguments = Bundle().apply {
                res_id?.let {
                    putInt("res_id", it)
                }
                min_price?.let {
                    putDouble("min_price", it)
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("res_id")?.let {
            res_id = it
        }
        arguments?.getDouble("min_price")?.let {
            min_price = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_food_list, container, false)
        recyclerView = view.findViewById(R.id.foodList)
        val btnOrder = view.findViewById<Button>(R.id.btOrder)
        txtMinPrice = view.findViewById(R.id.txtMinPrice)
        txtMenuTotal = view.findViewById(R.id.txtMenuTotal)
        txtPriceTotal = view.findViewById(R.id.txtPriceTotal)
        cardMenuDetail = view.findViewById(R.id.cardMenuDetail)

        if(min_price != 0.0) {
            txtMinPrice.text = "ราคาขั้นต่ำที่คุณตั้งไว้คือ ${min_price} บาท"
            txtMinPrice.visibility = View.VISIBLE
        } else {
            txtMinPrice.visibility = View.GONE
        }
        btnOrder.setOnClickListener(setOnClickButtonOrder)

        // initial global data
        menu.clear()
        totalPrice = 0.0

        res_id = arguments!!.getInt("res_id")
        getRestaurantTypes()

        return view
    }

    private fun getRestaurantTypes() {
        disposable = foodTypeAPI.getAllFoodTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> postResID(result.foodtypeList) },
                { err -> Log.d("err", err.message) }
            )
    }

    private fun postResID(foodtypeList: List<FoodType>?) {
        foodTypeList = foodtypeList
        disposable = foodAPI.getFoodByResID(res_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> setDataBeforeAdapter(result.foodArrayList) },
                { error -> Log.e("ERROR", error.message) }
            )
    }

    fun setClickListerner() {
        if(menu.size > 0 && (totalPrice < min_price || min_price == 0.0)) {
            txtPriceTotal.setTextColor(Color.parseColor("#000000"))
            btOrder.visibility = View.VISIBLE
        } else {
            txtPriceTotal.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
            btOrder.visibility = View.GONE
        }
        txtMenuTotal.text = "เลือก ${menu.size} รายการ |"
        txtPriceTotal.text = "ทั้งหมด ${"%.2f".format(totalPrice)} บาท"
        cardMenuDetail.visibility = View.VISIBLE
    }

    private fun setDataBeforeAdapter(foodListArr: List<Food>?) {
        foodList = foodListArr
        for(item in foodTypeList.orEmpty()) {
            var checkFoodTypeID = foodList?.filter{ it.foodtype_id == item.foodtype_id }
            var replaceImg: MutableList<String> = ArrayList()
            if(checkFoodTypeID!!.isNotEmpty()) {
                for(fooditem in checkFoodTypeID) {
                    if(fooditem.food_img == null) {
                        replaceImg.add("noimg.png")
                        replaceImg.add("noimg.png")
                    } else {
                        val image = fooditem.food_img.let {
                            it.replace("[", "").replace("]", "")
                                .split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        }
                        if(image.size == 1) {
                            replaceImg.add(image[0])
                            replaceImg.add("noimg.png")
                        } else {
                            replaceImg.add(image[0])
                            replaceImg.add(image[1])
                        }
                    }
                }

                Log.d("imagesize", replaceImg.size.toString())

                sectionAdapter.addSection(FoodListAdapter(item.foodtype_name, checkFoodTypeID, replaceImg, clickListener = {
                    setClickListerner()
                }))
                hasData = true
            }
        }
        if(hasData) {
            setDataToRecyclerView()
        } else {
            txtNoData.visibility = View.VISIBLE
        }
    }

    private fun setDataToRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = sectionAdapter
    }

    val setOnClickButtonOrder = View.OnClickListener {_ ->
        btOrder.setOnClickListener { _ ->
            var confirmFoodActivity = ConfirmFoodFragment.newInstance(res_id)

            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, confirmFoodActivity)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        sectionAdapter.removeAllSections()
    }

}