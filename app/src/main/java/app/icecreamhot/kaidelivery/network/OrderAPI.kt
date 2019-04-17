package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderRateResponse
import app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.OrderResponse
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

    @GET("orders/{orderId}")
    fun getOrderAndOrderDetail(@Path(value = "orderId", encoded= true) orderId: Int): Observable<OrderResponse>

    @GET("orders/doned/{orderName}")
    fun getOrderIsDoned(@Path(value = "orderName", encoded= true) orderName: String): Observable<OrderRateResponse>

    @GET("orders/delivery/user/now")
    fun getDeliveryNow(): Observable<app.icecreamhot.kaidelivery.model.Delivery.OrderList>

    @FormUrlEncoded
    @POST("orders/otp")
    fun sendOTPToUser(@Field("telephone") telephone: String): Observable<ResponseBody>

    @GET("orders/otp/{otpcode}")
    fun checkValidOTP(@Path("otpcode") otpcode: String): Observable<OneTimePasswordList>

    @FormUrlEncoded
    @POST("orders/delete")
    fun deleteOrderByID(@Field("order_id") order_id: Int): Observable<ResponseBody>

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