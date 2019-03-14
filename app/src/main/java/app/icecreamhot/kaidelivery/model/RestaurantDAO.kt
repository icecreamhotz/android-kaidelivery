package app.icecreamhot.kaidelivery.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDAO {
    @get:Query("SELECT * FROM restaurant")
    val all: List<Restaurant>

    @Insert
    fun insertAll(vararg restaurant: Restaurant)
}