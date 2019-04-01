package app.icecreamhot.kaidelivery.ui.restaurant

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Restaurant
import app.icecreamhot.kaidelivery.databinding.ItemRestaurantBinding
import app.icecreamhot.kaidelivery.model.mRestaurantLatitude
import app.icecreamhot.kaidelivery.model.mRestaurantLongitude
import app.icecreamhot.kaidelivery.ui.food.ConfirmFoodActivity
import app.icecreamhot.kaidelivery.ui.food.FoodActivity
import app.icecreamhot.kaidelivery.ui.map.MapsActivity
import javax.inject.Inject

class RestaurantListAdapter @Inject constructor() : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>() {
    lateinit var restaurantList: List<Restaurant>
    lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantListAdapter.ViewHolder {
        val binding: ItemRestaurantBinding  = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_restaurant, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantListAdapter.ViewHolder, position: Int) {
        holder.bind(restaurantList[position])
        holder.itemView.setOnClickListener {
            mRestaurantLatitude = restaurantList[position].res_lat!!.toDouble()
            mRestaurantLongitude = restaurantList[position].res_lng!!.toDouble()

            val intent = Intent(context, FoodActivity::class.java)
            intent.putExtra("res_id", restaurantList[position].res_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if(::restaurantList.isInitialized) restaurantList.size else 0
    }

    fun updateRestaurantList(restaurant: List<Restaurant>) {
        this.restaurantList = restaurant
        notifyDataSetChanged()

        Log.d("nahum", restaurant.toString())
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root) {
        private val viewModel = RestaurantViewModel()

        fun bind(restaurant: Restaurant) {
            viewModel.bind(restaurant)
//            binding.viewModel = viewModel
            Log.d("nahumssss", "Das")
        }
    }
}