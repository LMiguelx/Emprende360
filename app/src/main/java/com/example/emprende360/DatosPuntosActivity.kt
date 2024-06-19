package com.example.emprende360

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class DatosPuntosActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences(
            "profile_prefs",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_puntos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar puntos desde Firestore
        cargarPuntosDesdeFirestore()
    }

    // Función para obtener el studentId (codigoAcceso) desde SharedPreferences
    private fun getStudentId(): String? {
        return sharedPreferences.getString("codigoAcceso", null)
    }

    // Función para cargar los puntos desde Firestore usando el studentId
    private fun cargarPuntosDesdeFirestore() {
        val studentId = getStudentId() // Obtener el studentId desde SharedPreferences

        if (studentId != null) {
            // Referencia a Firestore y obtener el documento del estudiante
            val db = FirebaseFirestore.getInstance()

            db.collection("estudiantes").document(studentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtener puntos de asistencia y puntos de cuestionario
                        val puntosAsistencia = document.getLong("puntos_asistencia")
                        val puntosCuestionario = document.getLong("puntos_cuestionario")

                        // Actualizar los TextViews con los puntos obtenidos
                        findViewById<TextView>(R.id.puntoscuestioanrios)?.text = "Puntos Obtenidos : $puntosCuestionario"
                        findViewById<TextView>(R.id.puntosasistencias)?.text = "Puntos Obtenidos: $puntosAsistencia"
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }
}
