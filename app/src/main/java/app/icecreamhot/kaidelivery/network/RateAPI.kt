package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.RateList
import app.icecreamhot.kaidelivery.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RateAPI {
    @GET("rates/")
    fun getDeliveryRate(): Observable<RateList>

    companion object {
        fun create(): RateAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(RateAPI::class.java)
        }
    }
}