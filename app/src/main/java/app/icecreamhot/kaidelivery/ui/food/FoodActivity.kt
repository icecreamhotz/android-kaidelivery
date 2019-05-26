package app.icecreamhot.kaidelivery.ui.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

class FoodActivity : AppCompatActivity() {

    private val foodAPI by lazy { FoodAPI.create() }
    private val foodTypeAPI by lazy { FoodTypeAPI.create() }

    private var disposable: Disposable? = null
    var foodTypeList: List<FoodType>? = null
    var foodList: List<Food>? = null
    private val sectionAdapter = SectionedRecyclerViewAdapter()
    var hasData: Boolean = false
    private var res_id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        menu.clear()

        res_id = intent.getIntExtra("res_id", 235)

        getRestaurantTypes()
        setOnClickButtonOrder()
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
        val recyclerView: RecyclerView = findViewById(R.id.foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = sectionAdapter
    }

    private fun setOnClickButtonOrder() {
        btOrder.setOnClickListener {
            val intent = Intent(this, ConfirmFoodActivity::class.java)
            intent.putExtra("res_id", res_id)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
