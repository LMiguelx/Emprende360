package com.example.emprende360

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleCuestionarioActivity : AppCompatActivity() {

    private lateinit var lblPuntosObtenidos: TextView
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cuestionario)

        lblPuntosObtenidos = findViewById(R.id.lblPuntosObtenidos)
        btnSalir = findViewById(R.id.btnSalir)

        // Obtener los datos del Intent
        val puntosObtenidos = intent.getIntExtra("puntosObtenidos", 0)

        // Mostrar los puntos obtenidos en el TextView
        lblPuntosObtenidos.text = "Puntos obtenidos: $puntosObtenidos"

        btnSalir.setOnClickListener {
            val intent = Intent(this, EventosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
