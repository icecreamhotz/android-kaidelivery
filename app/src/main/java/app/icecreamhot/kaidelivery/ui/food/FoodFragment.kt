package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.data.menu
import app.icecreamhot.kaidelivery.model.Food
import app.icecreamhot.kaidelivery.model.FoodType
import app.icecreamhot.kaidelivery.network.FoodAPI
import app.icecreamhot.kaidelivery.network.FoodTypeAPI
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_food_list.*

class FoodFragment : Fragment() {
    val TAG = "FoodFragment"

    private val foodAPI by lazy { FoodAPI.create() }
    private val foodTypeAPI by lazy { FoodTypeAPI.create() }

    private var disposable: Disposable? = null
    var foodTypeList: List<FoodType>? = null
    var foodList: List<Food>? = null
    private val sectionAdapter = SectionedRecyclerViewAdapter()
    var hasData: Boolean = false
    private var res_id:Int = 0

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_food_list, container, false)
        recyclerView = view.findViewById(R.id.foodList)
        val btnOrder = view.findViewById<Button>(R.id.btOrder)

        menu.clear()
        res_id = arguments!!.getInt("res_id")
        getRestaurantTypes()
        btnOrder.setOnClickListener(setOnClickButtonOrder)

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
        if(menu.size > 0) {
            btOrder.visibility = View.VISIBLE
        } else {
            btOrder.visibility = View.GONE
        }
    }

    private fun setDataBeforeAdapter(foodListArr: List<Food>?) {
        foodList = foodListArr
        for(item in foodTypeList.orEmpty()) {
            var checkFoodTypeID = foodList?.filter{ it.foodtype_id == item.foodtype_id }
            var replaceImg: Array<String>? = null
            if(checkFoodTypeID!!.isNotEmpty()) {
                for(fooditem in checkFoodTypeID) {
                    if(fooditem.food_img == null) {
                        replaceImg = arrayOf("noimg.jpg", "noimg.jpg")
                    } else {
                        replaceImg = fooditem.food_img.let {
                            it.replace("[", "").replace("]", "")
                                .split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        }
                    }
                }
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
        btOrder.setOnClickListener { view ->
            var confirmFoodActivity = ConfirmFoodFragment()
            var arguments = Bundle()
            arguments.putInt("res_id", res_id)
            confirmFoodActivity.arguments = arguments

            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, confirmFoodActivity)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        sectionAdapter.removeAllSections()
    }

}