package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.utils.BASE_URL
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface OrderAPI {
    @POST("orders/")
    fun insertOrder(@Body menus: MenuList): Observable<OrderList>

    @GET("orders/delivery/user/now")
    fun getDeliveryNow(): Observable<app.icecreamhot.kaidelivery.model.Delivery.OrderList>

    @FormUrlEncoded
    @POST("orders/otp")
    fun sendOTPToUser(@Field("telephone") telephone: String): Observable<ResponseBody>

    @GET("orders/otp/{otpcode}")
    fun checkValidOTP(@Path("otpcode") otpcode: String): Observable<OneTimePasswordList>

    companion object {
        fun create(): OrderAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(OrderAPI::class.java)
        }
    }
}