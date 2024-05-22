package com.example.emprende360

import android.content.Intent
import android.os.Bundle
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

        val datosingresados : Button = findViewById(R.id.Ingresoadatospersonales)

        datosingresados.setOnClickListener {
            startActivity(Intent(this, DatosPasaporteActivity::class.java))
        }
        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth
    }
}