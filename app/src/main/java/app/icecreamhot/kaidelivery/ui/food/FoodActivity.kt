package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

class FoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        val sectionAdapter = SectionedRecyclerViewAdapter()

        val itemList = arrayListOf<String>("KUY", "HEE")
        sectionAdapter.addSection(FoodListAdapter(itemList))
        sectionAdapter.addSection(FoodListAdapter(itemList))
        sectionAdapter.addSection(FoodListAdapter(itemList))

        val recyclerView: RecyclerView = findViewById(R.id.foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = sectionAdapter
    }
}
