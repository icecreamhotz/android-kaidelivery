package app.icecreamhot.kaidelivery.ui.Alert

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.OrderDetail
import app.icecreamhot.kaidelivery.ui.Adapter.FoodDetailAndEditAdapter
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FoodDetailsAndEditDialog: DialogFragment() {

    var disposable: Disposable? = null
    lateinit var mFood: List<OrderDetail>
    private var pref: SharedPreferences? = null

    companion object {
        fun newInstance(order: List<OrderDetail>): FoodDetailsAndEditDialog {
            val fragment = FoodDetailsAndEditDialog()
            fragment.mFood = order
            return fragment
        }
    }

    private lateinit var listFood: RecyclerView

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
        listFood = view.findViewById(R.id.listDetailFoodEdit)

        val foodDetailAndEditAdapter = FoodDetailAndEditAdapter(mFood)

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