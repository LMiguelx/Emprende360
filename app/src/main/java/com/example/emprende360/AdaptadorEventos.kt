package com.example.emprende360

import java.util.Date
import com.google.firebase.Timestamp
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorEventos(private val listaEventos: List<Map<String, Any>>) :
    RecyclerView.Adapter<AdaptadorEventos.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = listaEventos[position]
        holder.bind(evento)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleEventoActivity::class.java)

            // Pasar datos al intent
            intent.putExtra("nombre", evento["nombre"] as? String)
            intent.putExtra("lugar", evento["Lugar"] as? String)
            intent.putExtra("aforo", (evento["aforo"] as? Long)?.toInt() ?: 0)
            intent.putExtra("descripcion", evento["descripcion"] as? String)
            intent.putExtra("horario", (evento["horario"] as? Timestamp)?.seconds)
            intent.putExtra("imagen", evento["imagen"] as? String)
            intent.putExtra("introduccion", evento["introduccion"] as? String)
            val temas = evento["temas"] as? List<String>
            intent.putStringArrayListExtra("temas", ArrayList(temas ?: emptyList()))

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreEventoTextView: TextView = itemView.findViewById(R.id.lblNombreEvento)
        private val lugarTextView: TextView = itemView.findViewById(R.id.lblLugar)
        private val horarioTextView: TextView = itemView.findViewById(R.id.lblHorario)
        private val imagenView: ImageView = itemView.findViewById(R.id.imgFoto)

        fun bind(evento: Map<String, Any>) {
            nombreEventoTextView.text = evento["nombre"] as? String ?: ""
            lugarTextView.text = "Lugar: ${evento["lugar"] as? String ?: ""}"

            // Convertir el timestamp a una fecha legible
            val timestamp = evento["horario"] as? Timestamp
            val horario = if (timestamp != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                "Horario: ${dateFormat.format(timestamp.toDate())}"
            } else {
                "Horario: "
            }
            horarioTextView.text = horario

            val imageUrl = evento["imagen"] as? String

            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .into(imagenView)
            } else {
                imagenView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}
