package com.example.rxandroid_example.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid_example.R
import kotlinx.android.synthetic.main.example_item.view.*

class ExampleItemAdapter(var items: List<String>): RecyclerView.Adapter<ExampleItemAdapter.ExampleViewHolder>() {
    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.example_item, parent, false)
        return ExampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.itemView.title.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}