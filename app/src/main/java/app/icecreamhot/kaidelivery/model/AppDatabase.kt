package app.icecreamhot.kaidelivery.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(Restaurant::class, RestaurantType::class), version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun restaurantDAO(): RestaurantDAO
    abstract fun restaurantTypeDAO(): RestaurantTypeDAO
}