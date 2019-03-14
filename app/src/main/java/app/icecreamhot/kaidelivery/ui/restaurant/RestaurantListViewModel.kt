package app.icecreamhot.kaidelivery.ui.restaurant

import androidx.lifecycle.MutableLiveData
import android.util.Log
import android.view.View
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.base.BaseViewModel
import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.network.RestaurantAPI
import app.icecreamhot.kaidelivery.network.RestaurantTypesAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class RestaurantListViewModel(private val restaurantDAO: RestaurantDAO, private val restaurantTypeDAO: RestaurantTypeDAO):BaseViewModel(){
    @Inject
    lateinit var restaurantAPI: RestaurantAPI
    @Inject
    lateinit var restaurantTypesAPI: RestaurantTypesAPI

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadRestaurants() }
    val restaurantListAdapter: RestaurantListAdapter = RestaurantListAdapter()

    private lateinit var subscription: Disposable

    init {
        loadRestaurants()
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadRestaurants() {
        subscription = Observable.fromCallable { restaurantDAO.all }
            .concatMap {
                dbRestaurantList ->
                    if(dbRestaurantList.isEmpty())
                        restaurantAPI.getRestaurants().concatMap {
                            apiRestaurantList -> restaurantDAO.insertAll(*apiRestaurantList.employeeArrayList!!.toTypedArray())
                            Observable.just(apiRestaurantList)
                        }
                else
                        Observable.just(dbRestaurantList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveRestaurantListStart() }
            .doOnTerminate { onRetrieveRestaurantListFinish() }
            .subscribe(
                { result -> loadRestaurantTypes(result)
                },
                { error -> onRetrieveRestaurantListFailed(error) }
            )
    }

    private fun loadRestaurantTypes(restaurantList: Any?) {
        subscription = Observable.fromCallable{ restaurantTypeDAO.all }
            .concatMap {
                dbRestaurantTypeList ->
                    if(dbRestaurantTypeList.isEmpty())
                        restaurantTypesAPI.getRestaurantTypes().concatMap {
                            apiRestaurantTypeList -> restaurantTypeDAO.insertAll(*apiRestaurantTypeList.restaurantTypeArrayList!!.toTypedArray())
                            Observable.just(apiRestaurantTypeList)
                        }
                else
                        Observable.just(dbRestaurantTypeList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveRestaurantListStart() }
            .doOnTerminate { onRetrieveRestaurantListFinish() }
            .subscribe(
                {
                    result -> onRetrieveRestaurantListSuccess(restaurantList, result)
                },
                {
                    error -> Log.d("err", error.message)
                }
            )
    }

    private fun onRetrieveRestaurantListStart() {
        loadingVisibility.value = View.VISIBLE
    }

    private fun onRetrieveRestaurantListFinish() {
        loadingVisibility.value = View.GONE
    }

    private fun onRetrieveRestaurantListSuccess(restaurantList:Any?, restaurantTypeList:Any?) {
        if(restaurantList is List<*> && restaurantTypeList is List<*>) {
            val a: List<Restaurant> = restaurantList.filterIsInstance<Restaurant>()
            val b: List<RestaurantType> = restaurantTypeList.filterIsInstance<RestaurantType>()
            for((index, item) in a.withIndex()) {
                var restype_name: MutableList<String> = mutableListOf<String>()
                val replaceType = item.restype_id?.replace("[", "")?.replace("]", "")
                    ?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()
                for(type in replaceType.orEmpty()) {
                    val typename = b.filter { it.restype_id == type.toInt() }
                    restype_name.add(typename[0].restype_name)
                }
                a[index].restype_name = restype_name.toString()
            }
            restaurantListAdapter.updateRestaurantList(a)
        }
    }

    private fun onRetrieveRestaurantListFailed(error:Throwable) {
        errorMessage.value = R.string.post_error
        Log.d("error", error.message)
    }
}