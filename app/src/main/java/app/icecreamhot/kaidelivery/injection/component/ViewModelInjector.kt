package app.icecreamhot.kaidelivery.injection.component

import app.icecreamhot.kaidelivery.injection.NetworkModule
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class)])

interface ViewModelInjector {
    fun inject(restaurantListViewModel: RestaurantListViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}