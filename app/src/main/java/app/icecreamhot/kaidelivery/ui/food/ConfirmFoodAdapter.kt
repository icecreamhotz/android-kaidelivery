package app.icecreamhot.kaidelivery.ui.food

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Menu
import kotlinx.android.synthetic.main.item_menu.view.*

class ConfirmFoodAdapter(val items: ArrayList<Menu>): RecyclerView.Adapter<ConfirmFoodAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(menu: Menu) {
            itemView.apply {
                txtFoodName.text = menu.food_name
                txtTotalFood.text = "x${menu.orderdetails_total}"
                txtFoodPrice.text = "${menu.orderdetails_price} บาท"
            }
        }
    }

}