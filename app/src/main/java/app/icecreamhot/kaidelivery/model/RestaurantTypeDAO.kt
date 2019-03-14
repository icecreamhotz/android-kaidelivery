package app.icecreamhot.kaidelivery.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantTypeDAO {
    @get:Query("SELECT * FROM restauranttype")
    val all: List<RestaurantType>

    @Insert
    fun insertAll(vararg restaurantType: RestaurantType)
}