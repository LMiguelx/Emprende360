package com.example.emprende360

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CuestionarioActivity : AppCompatActivity() {

    private lateinit var lblPregunta: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var btnResponder: Button
    private lateinit var btnSiguiente: Button
    private lateinit var btnSalir: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var cuestionariosRef: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var codigoAcceso: String
    private var listaCuestionarios = mutableListOf<Map<String, Any>>()
    private var indexPreguntaActual = 0
    private var puntosCuestionarioActual = 0
    private var totalPuntosAcumulados = 0
    private var cuestionarioCompletado = true
    private var selectedOption: CardView? = null
    private var respuestaCorrecta = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuestionario)

        // Inicialización de vistas
        lblPregunta = findViewById(R.id.lblPregunta)
        optionsContainer = findViewById(R.id.optionsContainer)
        btnResponder = findViewById(R.id.btnResponder)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnSalir = findViewById(R.id.btnSalir)
        progressBar = findViewById(R.id.progressBar)

        // Inicialización de Firebase Firestore
        db = FirebaseFirestore.getInstance()
        cuestionariosRef = db.collection("cuestionarios")

        // Inicialización de SharedPreferences
        sharedPreferences = getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        codigoAcceso = sharedPreferences.getString("codigoAcceso", "") ?: ""

        // Obtener el id_cuestionario enviado desde la actividad anterior
        val idCuestionario = intent.getStringExtra("eventId")
        if (idCuestionario != null) {
            obtenerCuestionarios(idCuestionario)
        } else {
            Log.e(TAG, "Error: No se recibió el id_cuestionario desde la actividad anterior")
            Toast.makeText(this, "Error al cargar el cuestionario", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Listener para el botón Responder
        btnResponder.setOnClickListener {
            validarRespuesta()
        }

        // Listener para el botón Siguiente Pregunta
        btnSiguiente.setOnClickListener {
            mostrarSiguientePregunta()
        }

        // Listener para el botón Salir del Cuestionario
        btnSalir.setOnClickListener {
            finish()
        }

        // Verificar si el cuestionario ya fue completado previamente
        verificarCuestionarioCompletado()
    }

    private fun obtenerCuestionarios(idCuestionario: String) {
        cuestionariosRef.whereEqualTo("id_cuestionario", idCuestionario)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val preguntas = mutableListOf<Map<String, Any>>()
                for (document in querySnapshot) {
                    preguntas.add(document.data)
                }
                if (preguntas.isNotEmpty()) {
                    // Obtener solo 5 preguntas al azar
                    listaCuestionarios = seleccionarPreguntasAlAzar(preguntas, 5)
                    // Mostrar la primera pregunta del cuestionario seleccionado
                    mostrarPregunta(indexPreguntaActual)
                } else {
                    Log.d(TAG, "No se encontraron cuestionarios con id_cuestionario: $idCuestionario")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener cuestionarios: $exception")
            }
    }
    private fun seleccionarPreguntasAlAzar(preguntas: List<Map<String, Any>>, cantidad: Int): MutableList<Map<String, Any>> {
        // Verificar que hay suficientes preguntas
        if (preguntas.size <= cantidad) {
            return preguntas.toMutableList()
        }

        // Lista para almacenar las preguntas seleccionadas al azar
        val preguntasSeleccionadas = mutableListOf<Map<String, Any>>()

        // Generar índices aleatorios únicos
        val indicesAleatorios = mutableSetOf<Int>()
        while (indicesAleatorios.size < cantidad) {
            val randomIndex = (0 until preguntas.size).random()
            indicesAleatorios.add(randomIndex)
        }

        // Agregar las preguntas correspondientes a los índices aleatorios generados
        for (index in indicesAleatorios) {
            preguntasSeleccionadas.add(preguntas[index])
        }

        return preguntasSeleccionadas
    }

    private fun mostrarPregunta(index: Int) {
        val cuestionario = listaCuestionarios[index]
        lblPregunta.text = cuestionario["pregunta"].toString()

        // Mostrar las opciones del cuestionario
        val opciones = cuestionario["opciones"] as Map<String, String>
        val respuestas = opciones.values.toMutableList()

        // Obtener la respuesta correcta y agregarla como una de las opciones
        respuestaCorrecta = cuestionario["respuesta"].toString()
        respuestas.add(respuestaCorrecta)

        // Limpiar el contenedor de opciones previo
        optionsContainer.removeAllViews()

        // Asignar las opciones a CardViews de forma aleatoria
        respuestas.shuffle()
        respuestas.forEach { respuesta ->
            val optionCard = createOptionCard(respuesta)
            optionsContainer.addView(optionCard)
        }

        // Limpiar selección previa
        selectedOption = null

        // Ocultar el botón Siguiente si es la última pregunta
        btnSiguiente.visibility = if (index < listaCuestionarios.size - 1) {
            View.VISIBLE
        } else {
            View.GONE
        }

        // Actualizar la barra de progreso
        progressBar.progress = (index.toFloat() / listaCuestionarios.size * 100).toInt()
    }

    private fun createOptionCard(respuesta: String): CardView {
        val cardView = CardView(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 16, 0, 16)
        cardView.layoutParams = layoutParams
        cardView.radius = 8f
        cardView.setCardBackgroundColor(Color.LTGRAY)
        cardView.setContentPadding(16, 16, 16, 16)

        val textView = TextView(this)
        textView.text = respuesta
        textView.textSize = 16f
        textView.setTextColor(Color.BLACK)

        cardView.addView(textView)

        cardView.setOnClickListener {
            selectedOption?.setCardBackgroundColor(Color.LTGRAY)
            cardView.setCardBackgroundColor(Color.YELLOW)
            selectedOption = cardView
        }

        return cardView
    }

    private fun validarRespuesta() {
        if (selectedOption != null) {
            val selectedText = (selectedOption?.getChildAt(0) as TextView).text.toString()
            if (selectedText == respuestaCorrecta) {
                selectedOption?.setCardBackgroundColor(Color.GREEN)
                puntosCuestionarioActual++
            } else {
                selectedOption?.setCardBackgroundColor(Color.RED)
            }
            btnResponder.isEnabled = false
            btnSiguiente.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarSiguientePregunta() {
        if (indexPreguntaActual < listaCuestionarios.size - 1) {
            indexPreguntaActual++
            mostrarPregunta(indexPreguntaActual)
            btnResponder.isEnabled = true
            btnSiguiente.visibility = View.GONE
        } else {
            totalPuntosAcumulados += puntosCuestionarioActual
            Toast.makeText(this, "Cuestionario completado. Puntos obtenidos: $puntosCuestionarioActual", Toast.LENGTH_LONG).show()
            guardarPuntosEnFirestore()
            btnSiguiente.visibility = View.GONE
            btnSalir.visibility = View.VISIBLE
        }
    }

    private fun guardarPuntosEnFirestore() {
        val userRef = db.collection("estudiantes").document(codigoAcceso)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val datosUsuario = documentSnapshot.data
                    val puntosAcumulados = datosUsuario?.get("puntos_cuestionario") as? Long ?: 0
                    userRef.update(
                        "puntos_cuestionario",
                        puntosAcumulados + puntosCuestionarioActual
                    )
                        .addOnSuccessListener {
                            Log.d(TAG, "Puntos acumulados actualizados en Firestore")

                            // Crear Intent para enviar datos a DetalleCuestionarioActivity
                            val intent = Intent(
                                this@CuestionarioActivity,
                                DetalleCuestionarioActivity::class.java
                            )
                            intent.putExtra("puntosObtenidos", puntosCuestionarioActual)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error al actualizar puntos acumulados en Firestore", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener datos del usuario en Firestore", e)
            }
    }


        private fun verificarCuestionarioCompletado() {
        val userRef = db.collection("estudiante").document(codigoAcceso)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val datosUsuario = documentSnapshot.data
                    val eventosCompletados = datosUsuario?.get("eventos_completados") as? List<String>
                    cuestionarioCompletado = eventosCompletados?.contains(intent.getStringExtra("eventId")) ?: false
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener datos del usuario en Firestore", e)
            }
    }

    companion object {
        private const val TAG = "CuestionarioActivity"
    }
}
