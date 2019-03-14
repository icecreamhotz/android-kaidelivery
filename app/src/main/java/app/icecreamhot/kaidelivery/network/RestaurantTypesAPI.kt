package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.RestaurantTypeList
import io.reactivex.Observable
import retrofit2.http.GET
import java.util.*

interface RestaurantTypesAPI {

    @GET("restauranttypes/")
    fun getRestaurantTypes(): Observable<RestaurantTypeList>
}