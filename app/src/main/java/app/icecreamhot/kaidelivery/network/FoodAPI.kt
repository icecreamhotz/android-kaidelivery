package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.FoodList
import app.icecreamhot.kaidelivery.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*

interface FoodAPI {
    @FormUrlEncoded
    @POST("foods/")
    fun getFoodByResID(@Field("res_id") res_id: Int): Observable<FoodList>

    companion object {
        fun create(): FoodAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(FoodAPI::class.java)
        }
    }
}