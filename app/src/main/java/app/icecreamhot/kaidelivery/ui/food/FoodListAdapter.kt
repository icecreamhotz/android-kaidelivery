package app.icecreamhot.kaidelivery.ui.food

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import app.icecreamhot.kaidelivery.R

class FoodListAdapter(val itemList: ArrayList<String>): StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.content_food_list)
        .headerResourceId(R.layout.header_food_list)
        .build()) {

    internal inner class MyItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvItemTwo : TextView
        init {
            tvItemTwo = itemView.findViewById(R.id.txtItem) as TextView
        }
    }

    internal inner class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvItemHeader : TextView
        init {
            tvItemHeader = itemView.findViewById(R.id.txtHeader) as TextView
        }
    }

    override fun getContentItemsTotal(): Int {
        return itemList.size
    }

    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder {
        return MyItemViewHolder(view!!)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val itemHolder = holder as MyItemViewHolder

        // bind your view here
        itemHolder.tvItemTwo.text = itemList.get(position)
    }

    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder {
        return HeaderViewHolder(view!!)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val itemHolder = holder as HeaderViewHolder

        itemHolder.tvItemHeader.text = "EEIEI"
    }
}
