package com.example.emprende360

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emprende360.R
import com.example.emprende360.SellosRegistradosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DatosPasaporteActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_pasaporte)

        val sellosregis: Button = findViewById(R.id.SellosRegistrados)
        val ivCodigoQR: ImageView = findViewById(R.id.ivCodigoQR)


        //Aqui se muestra el nombre en pantalla
        val userName = intent.getStringExtra("userName")
        val textViewHola = findViewById<TextView>(R.id.txtnombreCuenta)
        textViewHola.text = "Hola, $userName"

        // Configurar listener para el botón SellosRegistrados
        sellosregis.setOnClickListener {
            startActivity(Intent(this, SellosRegistradosActivity::class.java))
        }

        // Inicializar FirebaseAuth
        firebaseAuth = Firebase.auth

        // Recuperar la cadena base64 del código QR desde SharedPreferences
        val sharedPreferences = getSharedPreferences("QRPrefs", Context.MODE_PRIVATE)
        val encodedString = sharedPreferences.getString("qr_code", null)

        // Si hay una cadena base64 guardada, decodificarla y mostrar el código QR
        if (encodedString != null) {
            val decodedString = Base64.decode(encodedString, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            ivCodigoQR.setImageBitmap(decodedByte)
        }
    }
}
