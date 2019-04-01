package app.icecreamhot.kaidelivery.network

import io.reactivex.Observable
import retrofit2.http.GET
import app.icecreamhot.kaidelivery.model.RestaurantList
import app.icecreamhot.kaidelivery.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


interface RestaurantAPI {
    @GET("restaurants/open")
    fun getRestaurants(): Observable<RestaurantList>

    companion object {
        fun create(): RestaurantAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(RestaurantAPI::class.java)
        }
    }
}