package app.icecreamhot.kaidelivery.ui.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.OrderAndFoodDetail.OrderDetail
import kotlinx.android.synthetic.main.item_detail_food_edit.view.*
import javax.inject.Inject

class FoodDetailAndEditAdapter @Inject constructor(val food: List<OrderDetail>): RecyclerView.Adapter<FoodDetailAndEditAdapter.ViewHolder>() {

    override fun getItemCount() = food.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodDetailAndEditAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail_food_edit, parent, false))
    }

    override fun onBindViewHolder(holder: FoodDetailAndEditAdapter.ViewHolder, position: Int) {
        holder.bind(food[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(food: OrderDetail) {
            val foodNameText = food.food.food_name
            val foodPriceText = food.orderdetail_price
            itemView.apply {
                foodName.text = foodNameText
                foodTotal.text = "x ${food.orderdetail_total}"
                foodPrice.text = "${foodPriceText} $"
            }
        }
    }
}