package com.example.emprende360

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleCursoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_curso)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val duracion = intent.getIntExtra("duracion", 0)
        val horario = intent.getLongExtra("horario", 0)
        val imagen = intent.getStringExtra("imagen") ?: ""
        val introduccion = intent.getStringExtra("introduccion") ?: ""
        val precio = intent.getIntExtra("precio", 0)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val horarioString = dateFormat.format(Date(horario))

        findViewById<TextView>(R.id.lblNombreCursoDetalle).text = nombre
        findViewById<TextView>(R.id.lblDescripcionDetalle).text = descripcion
        findViewById<TextView>(R.id.lblDuracionDetalle).text = "Duración: $duracion horas"
        findViewById<TextView>(R.id.lblHorarioDetalle).text = "Horario: $horarioString"
        findViewById<TextView>(R.id.lblIntroduccionDetalle).text = introduccion
        findViewById<TextView>(R.id.lblPrecioDetalle).text = "Precio: $$precio"

        val imageView = findViewById<ImageView>(R.id.imgFotoCursoDetalle)
        if (imagen.isNotEmpty()) {
            Glide.with(this)
                .load(imagen)
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        obtenerDetallesInscripcion()
    }

    private fun obtenerDetallesInscripcion() {
        val db = FirebaseFirestore.getInstance()
        val cursosRef = db.collection("cursos")

        cursosRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val inscripciones = document["inscripciones"] as? Map<String, Any>
                    val duracion = document["duración"] as? Long
                    val precio = document["precio"] as? Long
                    mostrarDetallesInscripcion(inscripciones, duracion, precio)
                }
            }
            .addOnFailureListener { exception ->
                mostrarDetallesInscripcion(null, null, null)
            }
    }

    private fun mostrarDetallesInscripcion(inscripciones: Map<String, Any>?, duracion: Long?, precio: Long?) {
        val inscripcionTextView = findViewById<TextView>(R.id.lblInscripcionDetalle)
        val duracionTextView = findViewById<TextView>(R.id.lblDuracionDetalle)
        val precioTextView = findViewById<TextView>(R.id.lblPrecioDetalle)

        if (inscripciones != null && inscripciones.isNotEmpty()) {
            val fechaInicio = (inscripciones["Cierre de Inscripciones"] as? com.google.firebase.Timestamp)?.toDate()
            val fechaFin = (inscripciones["Inicio de Clases"] as? com.google.firebase.Timestamp)?.toDate()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val inicio = dateFormat.format(fechaInicio)
            val fin = dateFormat.format(fechaFin)

            val inscripcionText = "Cierre de Inscripciones: $inicio\nInicio de Clases: $fin"
            inscripcionTextView.text = inscripcionText

            duracion?.let { duracionTextView.text = "Duración: $it horas" }
            precio?.let { precioTextView.text = "Precio: $$it" }
        } else {
            inscripcionTextView.text = "Detalles de inscripción no disponibles"
            duracionTextView.text = ""
            precioTextView.text = ""
        }
    }
}
