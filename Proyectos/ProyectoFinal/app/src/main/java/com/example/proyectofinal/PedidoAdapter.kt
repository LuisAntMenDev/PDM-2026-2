package com.example.proyectofinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PedidoAdapter(private val dataSet: ArrayList<Order>) :
    RecyclerView.Adapter<PedidoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderView: TextView = view.findViewById(R.id.pedido)
        val totalView: TextView = view.findViewById(R.id.total)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.pedido_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.orderView.text = String.format("Pedido #%d", item.id)
        viewHolder.totalView.text = String.format("Total: $%.2f", item.total)
    }

    override fun getItemCount() = dataSet.size

}