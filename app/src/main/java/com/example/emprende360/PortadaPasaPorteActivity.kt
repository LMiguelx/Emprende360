package com.example.emprende360

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprende360.DatosPasaporteActivity
import com.example.emprende360.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PortadaPasaPorteActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portada_pasa_porte)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Configuración del temporizador para el splash screen
        val handler = Handler()
        handler.postDelayed({
            // Acción a realizar después del tiempo especificado
            startActivity(Intent(this,LoginActivity::class.java))
            finish() // Para que el usuario no pueda volver atrás con el botón de retroceso
        }, 5000) // Tiempo de espera en milisegundos (en este caso, 2000ms = 2 segundos)
    }
}