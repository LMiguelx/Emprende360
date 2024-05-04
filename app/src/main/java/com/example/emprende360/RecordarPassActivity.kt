package com.example.emprende360

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecordarPassActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordar_pass)
        val txtEmail :TextView = findViewById(R.id.txtEmailCambio)
        val btnCambiar : Button = findViewById(R.id.btnCambiar)
        btnCambiar.setOnClickListener()
        {
            sendPasswordReset(txtEmail.text.toString())
        }

        firebaseAuth = Firebase.auth
    }
    private fun sendPasswordReset(email:String)
    {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(){task->
                if (task.isSuccessful){
                    Toast.makeText(baseContext,"Correo de cambio de contraseña enviado", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(baseContext,"Error, no se pudo completar el procesos", Toast.LENGTH_SHORT).show()
                }

            }
    }
}