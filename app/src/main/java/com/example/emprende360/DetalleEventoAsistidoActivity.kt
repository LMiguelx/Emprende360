package com.example.emprende360

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

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
        lblHorarioDetalle.text = evento["horario"].toString()
        lblIntroduccionDetalle.text = evento["introduccion"].toString()
        lblTemasDetalle.text = evento["temas"].toString()
    }
}
