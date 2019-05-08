package app.icecreamhot.kaidelivery.ui.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderHistory
import app.icecreamhot.kaidelivery.network.OrderAPI
import app.icecreamhot.kaidelivery.ui.Adapter.OrderHistoryAdapter
import app.icecreamhot.kaidelivery.ui.Alert.FoodDetailsAndEditDialog
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HistoryOrderFragment: Fragment() {

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    lateinit var rvHistoryOrder: RecyclerView

    private var disposable: Disposable? = null
    private var pref: SharedPreferences? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pref = context?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        rvHistoryOrder = view.findViewById(R.id.recyclerViewHistoryOrder)
        loadHistoryEmployeeOrder()
        return view
    }

    private fun loadHistoryEmployeeOrder() {
        val token = pref?.getString("token", null)
        token?.let {
            disposable = orderAPI.getHistoryOrderCustomer(it)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            result -> setValueToView(result.data)
                    },
                    {
                            err -> Log.d("errorja", err.message)
                    }
                )
        }
    }

    private fun setValueToView(data: ArrayList<OrderHistory>) {
        Log.d("dataja", data.toString())
        val historyAdapter = OrderHistoryAdapter(data)
        rvHistoryOrder.apply {
            layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = historyAdapter
        }
        historyAdapter.onItemClick = { order ->
            val dialogFoodDetailsAndEdit = FoodDetailsAndEditDialog.newInstance(order.orderDetail)
            dialogFoodDetailsAndEdit.show(fragmentManager, "OrderDetailsAndEditFragment")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}