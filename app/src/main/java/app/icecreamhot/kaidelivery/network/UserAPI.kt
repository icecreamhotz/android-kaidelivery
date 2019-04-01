package app.icecreamhot.kaidelivery.network

import app.icecreamhot.kaidelivery.model.User
import app.icecreamhot.kaidelivery.model.UserList
import app.icecreamhot.kaidelivery.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {

    @FormUrlEncoded
    @POST("users/signup")
    fun signup(@Field("username") username: String?,
                    @Field("password") password: String?,
                    @Field("name") name: String?,
                    @Field("lastname") lastname: String?,
                    @Field("email") email: String?,
                    @Field("telephone") telephone: String?,
                    @Field("address") address: String?): Observable<User>

    @FormUrlEncoded
    @POST("users/auth/facebook/")
    fun loginWithFacebook(@Field("provider_id") provider_id: String?,
                          @Field("email") email: String?,
                          @Field("image") image: String?,
                          @Field("name") name: String?): Observable<User>

    @FormUrlEncoded
    @POST("users/login")
    fun loginCommon(@Field("username") username: String?,
                    @Field("password") password: String?): Observable<UserList>

    companion object {
        fun create(): UserAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(UserAPI::class.java)
        }
    }
}