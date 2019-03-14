package app.icecreamhot.kaidelivery.base

import androidx.lifecycle.ViewModel
import app.icecreamhot.kaidelivery.injection.NetworkModule
import app.icecreamhot.kaidelivery.injection.component.ViewModelInjector
import app.icecreamhot.kaidelivery.injection.component.DaggerViewModelInjector
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListViewModel

abstract class BaseViewModel: ViewModel() {

    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject() {
        when (this) {
            is RestaurantListViewModel -> injector.inject(this)
        }
    }
}