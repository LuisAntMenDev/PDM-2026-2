package com.example.proyectofinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CatalogoAdapter(private val dataSet: Array<Product>) :
    RecyclerView.Adapter<CatalogoAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById(R.id.image)
        val nameView: TextView = view.findViewById(R.id.nombre)
        val priceView: TextView = view.findViewById(R.id.precio)
        val buyBtn: Button = view.findViewById(R.id.btn_compra)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.imgView.setImageResource(getDrawableId(item.img))
        viewHolder.nameView.text = item.name
        viewHolder.priceView.text = String.format("$%.2f", item.price)
        viewHolder.buyBtn.text = if (item.onCart) "Agregado" else "Agregar"
        viewHolder.buyBtn.isEnabled = !item.onCart
        viewHolder.buyBtn.setOnClickListener {
            onClickListener?.onClick(position, item)
        }
    }

    override fun getItemCount() = dataSet.size

    private fun getDrawableId(name: String): Int {
        return when (name) {
            "playera" -> R.drawable.playera
            "tenis" -> R.drawable.tenis
            "pantalon" -> R.drawable.pantalon
            "gorra" -> R.drawable.gorra
            "lentes" -> R.drawable.lentes
            "calcetines" -> R.drawable.calcetines
            else -> R.drawable.ic_launcher_foreground
        }
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Product)
    }

}