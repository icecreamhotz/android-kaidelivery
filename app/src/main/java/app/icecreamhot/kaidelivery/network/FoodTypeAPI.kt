package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.FoodTypeList
import app.icecreamhot.kaidelivery.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FoodTypeAPI {
    @GET("foodtypes/")
    fun getAllFoodTypes(): Observable<FoodTypeList>

    companion object {
        fun create(): FoodTypeAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(FoodTypeAPI::class.java)
        }
    }
}