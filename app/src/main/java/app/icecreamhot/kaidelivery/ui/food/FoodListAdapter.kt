package app.icecreamhot.kaidelivery.ui.food

import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.data.menu
import app.icecreamhot.kaidelivery.data.totalPrice
import app.icecreamhot.kaidelivery.model.Food
import app.icecreamhot.kaidelivery.model.Menu
import app.icecreamhot.kaidelivery.utils.BASE_URL_FOOD_IMG
import com.bumptech.glide.Glide

class FoodListAdapter constructor(
    val headerList: String,
    val itemList: List<Food>,
    val foodImg: MutableList<String>?,
    val clickListener: () -> Unit): StatelessSection(SectionParameters.builder()
    .itemResourceId(R.layout.content_food_list)
    .headerResourceId(R.layout.header_food_list)
    .build()) {

    internal inner class MyItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvFoodName : TextView
        var tvFoodPrice: TextView
        var imgFoodOne: ImageView
        var imgFoodTwo: ImageView
        var btAdd: ImageButton
        var btDelete: ImageButton
        var tvTotal: TextView
        init {
            tvFoodName = itemView.findViewById(R.id.txtFoodName) as TextView
            tvFoodPrice = itemView.findViewById(R.id.txtFoodPrice) as TextView
            imgFoodOne = itemView.findViewById(R.id.foodImgOne) as ImageView
            imgFoodTwo = itemView.findViewById(R.id.foodImgTwo) as ImageView
            btAdd = itemView.findViewById(R.id.addFood) as ImageButton
            btDelete = itemView.findViewById(R.id.minusFood) as ImageButton
            tvTotal = itemView.findViewById(R.id.txtTotalFood) as TextView

        }
    }

    internal inner class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvItemHeader : TextView
        init {
            tvItemHeader = itemView.findViewById(R.id.txtFoodType) as TextView
        }
    }

    override fun getContentItemsTotal() = itemList.size

    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder {
        return MyItemViewHolder(view!!)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val itemHolder = holder as MyItemViewHolder
        var index = if(position == 0) 0 else position + position

        // bind your view here
        itemHolder.tvFoodName.text = itemList.get(position).food_name
        itemHolder.tvFoodPrice.text = "${itemList.get(position).food_price} บาท"


        setImageToGlide(itemHolder.imgFoodOne, foodImg!!.get(index).replace("\"", ""))
        setImageToGlide(itemHolder.imgFoodTwo, foodImg!!.get(index+1).replace("\"", ""))


        itemHolder.btAdd.setOnClickListener {
            val foodId = itemList.get(position).food_id
            val foodName = itemList.get(position).food_name
            val foodPrice = itemList.get(position).food_price.toDouble()

            val checkDuplicateMenu = menu.filter{ it.food_id == foodId }

            if(checkDuplicateMenu.isNotEmpty()) {
                val iterator = menu.iterator()

                iterator.forEach {
                    if(it.food_id == foodId) {
                        val newTotal = it.orderdetails_total + 1
                        it.orderdetails_total = newTotal
                        itemHolder.tvTotal.text = newTotal.toString()
                        Log.d("menu", menu.toString())
                    }
                }
            } else {
                val setMenu = Menu(foodId,  foodName,1, foodPrice)
                menu.add(setMenu)
                itemHolder.tvTotal.text = "1"
            }
            Log.d("menu", menu.toString())
            totalPrice += foodPrice
            clickListener()
        }

        itemHolder.btDelete.setOnClickListener {
            val foodId = itemList.get(position).food_id
            val foodPrice = itemList.get(position).food_price.toDouble()

            val checkDuplicateMenu = menu.filter{ it.food_id == foodId }

            if(checkDuplicateMenu.isNotEmpty()) {
                val iterator = menu.iterator()

                iterator.forEach {
                    if(it.food_id == foodId) {
                        val newTotal = it.orderdetails_total - 1
                        if(newTotal == 0) {
                            iterator.remove()
                        } else {
                            it.orderdetails_total = newTotal
                        }
                        itemHolder.tvTotal.text = newTotal.toString()
                        Log.d("menu", menu.toString())
                    }
                }
                totalPrice -= foodPrice
                clickListener()
            }
        }

    }

    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder {
        return HeaderViewHolder(view!!)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val itemHolder = holder as HeaderViewHolder

        itemHolder.tvItemHeader.text = headerList
    }

    private fun setImageToGlide(view: ImageView, foodImg: String) {
        Glide.with(view).load(BASE_URL_FOOD_IMG + foodImg).into(view)
    }

}
