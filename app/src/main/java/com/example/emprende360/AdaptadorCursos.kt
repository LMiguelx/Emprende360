package com.example.emprende360

import android.content.Context
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

class AdaptadorCursos(private val context: Context, private val listaCursos: List<Map<String, Any>>) :
    RecyclerView.Adapter<AdaptadorCursos.CursoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = listaCursos[position]
        holder.bind(curso)
    }

    override fun getItemCount(): Int = listaCursos.size

    inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nombreTextView: TextView = itemView.findViewById(R.id.NombreCurso)
        private val introduccionTextView: TextView = itemView.findViewById(R.id.IntroduccionCurso)
        private val inscripcionTextView: TextView = itemView.findViewById(R.id.InscripcionCurso)
        private val precioTextView: TextView = itemView.findViewById(R.id.PrecioCurso)
        private val imagenImageView: ImageView = itemView.findViewById(R.id.imgFotoCursos)

        fun bind(curso: Map<String, Any>) {
            nombreTextView.text = "${curso["nombre"] as? String ?: ""}"
            introduccionTextView.text = "${curso["introduccion"] as? String ?: ""}"
            inscripcionTextView.text = parseInscripcion(curso["inscripciones"] as? Map<String, Any>)
            precioTextView.text = "Precio: ${curso["precio"]?.toString() ?: ""}"

            val imagenUrl = curso["imagen"] as? String ?: ""
            if (imagenUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(imagenUrl)
                    .into(imagenImageView)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val curso = listaCursos[position]
                val intent = Intent(context, DetalleCursoActivity::class.java).apply {
                    putExtra("nombre", curso["nombre"] as? String ?: "")
                    putExtra("descripcion", curso["descripcion"] as? String ?: "")
                    putExtra("duracion", curso["duracion"] as? Int ?: 0)
                    putExtra("horario", curso["horario"] as? Long ?: 0L)
                    putExtra("imagen", curso["imagen"] as? String ?: "")
                    putExtra("introduccion", curso["introduccion"] as? String ?: "")
                    putExtra("precio", curso["precio"] as? Int ?: 0)
                }
                context.startActivity(intent)
            }
        }

        private fun parseHorario(timestamp: Any?): String {
            if (timestamp is Long) {
                val isDateTime = timestamp.toString().length > 10

                val dateFormat = if (isDateTime) {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                } else {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                }

                val date = Date(timestamp)
                return dateFormat.format(date)
            }
            return ""
        }

        private fun parseInscripcion(inscripcion: Map<String, Any>?): String {
            inscripcion?.let { map ->
                val fechaInicio = map["Cierre de Inscripciones"] as? Long ?: 0L
                val fechaFin = map["Inicio de Clases"] as? Long ?: 0L
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val inicio = dateFormat.format(Date(fechaInicio))
                val fin = dateFormat.format(Date(fechaFin))

                return "Cierre de Inscripciones: $inicio\nInicio de Clases: $fin"
            }
            return ""
        }
    }
}
