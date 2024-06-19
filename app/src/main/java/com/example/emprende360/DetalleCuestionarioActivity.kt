package com.example.emprende360

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleCuestionarioActivity : AppCompatActivity() {

    private lateinit var lblPuntos: TextView
    private lateinit var lblMensaje: TextView
    private lateinit var imgArriba: ImageView
    private lateinit var imgAbajo: ImageView
    private lateinit var btnSalir: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cuestionario)

        // Inicialización de vistas
        lblPuntos = findViewById(R.id.lblPuntos)
        lblMensaje = findViewById(R.id.lblMensaje)
        imgArriba = findViewById(R.id.imgArriba)
        imgAbajo = findViewById(R.id.imgAbajo)
        btnSalir = findViewById(R.id.btnSalir)

        // Obtener datos pasados desde CuestionarioActivity
        val puntosAcumulados = intent.getIntExtra("puntosAcumulados", 0)

        // Mostrar puntos acumulados
        lblPuntos.text = "Puntos acumulados: $puntosAcumulados"

        // Mostrar mensaje y configurar imágenes según la puntuación
        when (puntosAcumulados) {
            5 -> {
                lblMensaje.text = "¡Felicitaciones!"
                imgArriba.setImageResource(R.drawable.congratulations)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
            4 -> {
                lblMensaje.text = "Casi lo logras"
                imgArriba.setImageResource(R.drawable.happy)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
            3 -> {
                lblMensaje.text = "Esfuérzate más"
                imgArriba.setImageResource(R.drawable.casi_lo_logras)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
            2 -> {
                lblMensaje.text = "Presta más atención"
                imgArriba.setImageResource(R.drawable.mas_atenci_n)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
            1 -> {
                lblMensaje.text = "Esperamos más de ti"
                imgArriba.setImageResource(R.drawable.esperamos_mas_deti)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
            else -> {
                lblMensaje.text = "Será para la próxima"
                imgArriba.setImageResource(R.drawable.sera_la_pr_xima)
                imgAbajo.setImageResource(R.drawable.apoyo)
            }
        }

        // Configurar listener para el botón Salir
        btnSalir.setOnClickListener {
            finish()
        }
        val textView = findViewById<TextView>(R.id.lblMensaje)
        animateTextSize(textView, 10f, 25f)
    }
    private fun animateTextSize(textView: TextView, startSize: Float, endSize: Float) {
        val increaseAnimator = ValueAnimator.ofFloat(startSize, endSize).apply {
            duration = 1000 // Duración de la animación en milisegundos
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue)
            }
        }

        val decreaseAnimator = ValueAnimator.ofFloat(endSize, startSize).apply {
            duration = 1000 // Duración de la animación en milisegundos
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue)
            }
        }

        val animatorSet = AnimatorSet().apply {
            playSequentially(increaseAnimator, decreaseAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Reiniciamos el conjunto de animadores para que el bucle continúe
                    start()
                }
            })
        }
        animatorSet.start()
    }
}
