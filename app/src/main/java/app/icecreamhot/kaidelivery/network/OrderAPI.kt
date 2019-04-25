package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.*
import app.icecreamhot.kaidelivery.model.OrderAndDetail.OrderHistoryResponse
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

    @GET("orders/doned/{orderId}")
    fun getOrderIsDoned(@Path(value = "orderId", encoded= true) orderId: Int): Observable<OrderRateResponse>

    @GET("orders/history/user")
    fun getHistoryOrderCustomer(): Observable<OrderHistoryResponse>

    @GET("orders/delivery/user/now")
    fun getDeliveryNow(): Observable<app.icecreamhot.kaidelivery.model.Delivery.OrderList>

    @FormUrlEncoded
    @POST("orders/otp")
    fun sendOTPToUser(@Field("telephone") telephone: String): Observable<ResponseBody>

    @GET("orders/otp/{otpcode}")
    fun checkValidOTP(@Path("otpcode") otpcode: String): Observable<OneTimePasswordList>

    @FormUrlEncoded
    @POST("orders/update/status")
    fun updateStatusOrder(@Field("order_id") order_id: Int,
                          @Field("order_status") order_status: Int,
                          @Field("order_statusdetails") order_statusdetails: String?,
                          @Field("message") message: String?,
                          @Field("token") token: String?): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/comment/employee")
    fun updateEmployeeScoreAfterDelivered(@Field("order_id") order_id: Int,
                                          @Field("rating") rating: Int,
                                          @Field("comment") comment: String?,
                                          @Field("user_id") userId: Int,
                                          @Field("emp_id") empId: Int): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/comment/restaurant")
    fun updateRestaurantScoreAfterDelivered(@Field("order_id") order_id: Int,
                                            @Field("rating") rating: Int,
                                            @Field("comment") comment: String?,
                                            @Field("user_id") userId: Int,
                                            @Field("res_id") empId: Int): Observable<ResponseMAS>

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