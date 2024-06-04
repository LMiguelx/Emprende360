package com.example.emprende360

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleEventoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_evento)

        // Obtener datos del intent
        val nombre = intent.getStringExtra("nombre")
        val lugar = intent.getStringExtra("lugar")
        val aforo = intent.getIntExtra("aforo", 0)
        val descripcion = intent.getStringExtra("descripcion")
        val horarioTimestamp = intent.getLongExtra("horario", 0L)
        val imagen = intent.getStringExtra("imagen")
        val introduccion = intent.getStringExtra("introduccion")
        val temas = intent.getStringArrayListExtra("temas")

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val horario = if (horarioTimestamp != 0L) {
            dateFormat.format(Date(horarioTimestamp * 1000))
        } else {
            ""
        }

        // Configurar las vistas
        findViewById<TextView>(R.id.lblNombreEventoDetalle).text = nombre
        findViewById<TextView>(R.id.lblLugarDetalle).text = "Lugar: $lugar"
        findViewById<TextView>(R.id.lblAforoDetalle).text = "Aforo: $aforo"
        findViewById<TextView>(R.id.lblDescripcionDetalle).text = descripcion
        findViewById<TextView>(R.id.lblHorarioDetalle).text = "Horario: $horario"
        findViewById<TextView>(R.id.lblIntroduccionDetalle).text = introduccion

        val temasTextView = findViewById<TextView>(R.id.lblTemasDetalle)
        temasTextView.text = "Temas: ${temas?.joinToString(", ") ?: ""}"

        val imageView = findViewById<ImageView>(R.id.imgFotoDetalle)
        if (!imagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagen)
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }
}
