package app.icecreamhot.kaidelivery.ui.restaurant

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Restaurant
import app.icecreamhot.kaidelivery.databinding.ItemRestaurantBinding
import javax.inject.Inject

class RestaurantListAdapter @Inject constructor() : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>() {
    private lateinit var restaurantList: List<Restaurant>
    private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantListAdapter.ViewHolder {
        val binding: ItemRestaurantBinding  = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_restaurant, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantListAdapter.ViewHolder, position: Int) {
        holder.bind(restaurantList[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "Click me !", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return if(::restaurantList.isInitialized) restaurantList.size else 0
    }

    fun updateRestaurantList(restaurant: List<Restaurant>) {
        this.restaurantList = restaurant
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root) {
        private val viewModel = RestaurantViewModel()

        fun bind(restaurant: Restaurant) {
            viewModel.bind(restaurant)
            binding.viewModel = viewModel
        }
    }
}