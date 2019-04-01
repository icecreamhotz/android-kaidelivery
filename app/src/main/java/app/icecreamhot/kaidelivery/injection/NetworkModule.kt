package app.icecreamhot.kaidelivery.injection

import app.icecreamhot.kaidelivery.network.FoodAPI
import app.icecreamhot.kaidelivery.network.RestaurantAPI
import app.icecreamhot.kaidelivery.network.RestaurantTypesAPI
import app.icecreamhot.kaidelivery.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
@Suppress("unused")
object NetworkModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRestaurantsAPI(retrofit: Retrofit): RestaurantAPI {
        return retrofit.create(RestaurantAPI::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRestaurantTypesAPI(retrofit: Retrofit): RestaurantTypesAPI {
        return retrofit.create(RestaurantTypesAPI::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
}