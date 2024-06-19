package com.example.emprende360

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

class DetalleEventoAsistidoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_evento_asistido)

        val evento = intent.getSerializableExtra("evento") as HashMap<String, Any>

        val lblNombreEventoDetalle: TextView = findViewById(R.id.lblNombreEventoDetalle)
        val imgFotoDetalle: ImageView = findViewById(R.id.imgFotoDetalle)
        val lblLugarDetalle: TextView = findViewById(R.id.lblLugarDetalle)
        val lblAforoDetalle: TextView = findViewById(R.id.lblAforoDetalle)
        val lblDescripcionDetalle: TextView = findViewById(R.id.lblDescripcionDetalle)
        val lblHorarioDetalle: TextView = findViewById(R.id.lblHorarioDetalle)
        val lblIntroduccionDetalle: TextView = findViewById(R.id.lblIntroduccionDetalle)
        val lblTemasDetalle: TextView = findViewById(R.id.lblTemasDetalle)

        lblNombreEventoDetalle.text = evento["nombre"].toString()
        Glide.with(this).load(evento["imagen"].toString()).into(imgFotoDetalle)
        lblLugarDetalle.text = evento["lugar"].toString()
        lblAforoDetalle.text = evento["aforo"].toString()
        lblDescripcionDetalle.text = evento["descripcion"].toString()
        lblIntroduccionDetalle.text = evento["introduccion"].toString()
        lblTemasDetalle.text = evento["temas"].toString()

        // Obtener el valor del timestamp del evento
        val timestamp = evento["horario"] as Timestamp  // Obtener el timestamp como com.google.firebase.Timestamp

        // Convertir el Timestamp a Date
        val date = timestamp.toDate()

        // Crear un objeto SimpleDateFormat para formatear la fecha
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm")

        // Formatear la fecha y establecerla en el TextView
        lblHorarioDetalle.text = sdf.format(date)
    }
}
