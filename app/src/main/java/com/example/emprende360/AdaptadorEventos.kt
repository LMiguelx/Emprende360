package com.example.emprende360

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorEventos(private val listaEventos: List<Map<String, Any>>) :
    RecyclerView.Adapter<AdaptadorEventos.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = listaEventos[position]
        holder.bind(evento)
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreEventoTextView: TextView = itemView.findViewById(R.id.lblNombreEvento)
        private val lugarTextView: TextView = itemView.findViewById(R.id.lblLugar)
        private val horarioTextView: TextView = itemView.findViewById(R.id.lblHorario)
        private val imagenview: ImageView = itemView.findViewById(R.id.imgFoto)

        fun bind(evento: Map<String, Any>) {
            nombreEventoTextView.text = evento["nombre"] as? String ?: ""
            lugarTextView.text = evento["Lugar"] as? String ?: ""
            horarioTextView.text = evento["introduccion"] as? String ?: ""
        }
    }
}
