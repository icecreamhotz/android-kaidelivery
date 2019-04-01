package app.icecreamhot.kaidelivery.injection

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.model.AppDatabase
import app.icecreamhot.kaidelivery.ui.restaurant.RestaurantListViewModel
import java.lang.IllegalArgumentException

//open class ViewModelFactory(private val activity: Application): ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if(modelClass.isAssignableFrom(RestaurantListViewModel::class.java)) {
//            val db = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "restaurants").build()
//            @Suppress("UNCHECKED_CAST")
//            return RestaurantListViewModel(db.restaurantDAO(), db.restaurantTypeDAO()) as T
//        }
//        throw IllegalArgumentException("Unknkown ViewModel Class")
//    }
//}