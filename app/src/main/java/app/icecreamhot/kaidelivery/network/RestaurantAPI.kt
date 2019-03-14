package app.icecreamhot.kaidelivery.network

import io.reactivex.Observable
import retrofit2.http.GET
import app.icecreamhot.kaidelivery.model.RestaurantList


interface RestaurantAPI {
    @GET("restaurants/")
    fun getRestaurants(): Observable<RestaurantList>
}