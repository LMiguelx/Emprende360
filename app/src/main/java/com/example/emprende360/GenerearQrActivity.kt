package com.example.emprende360

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream

class GenerearQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generar_qr)

        val ivCodigoQR: ImageView = findViewById(R.id.ivCodigoQR)
        val etNombre: EditText = findViewById(R.id.etNombre)
        val etApellidos: EditText = findViewById(R.id.etApellidos)
        val etCodigoEstudiante: EditText = findViewById(R.id.etCodigoEstudiante)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etCarrera: EditText = findViewById(R.id.etCarrera)
        val btnGenerar: Button = findViewById(R.id.btnGenerar)

        btnGenerar.setOnClickListener {
            try {
                val barcodeEncoder = BarcodeEncoder()
                val data = """
                    Nombre: ${etNombre.text}
                    Apellidos: ${etApellidos.text}
                    Código del Estudiante: ${etCodigoEstudiante.text}
                    Correo Email: ${etEmail.text}
                    Carrera: ${etCarrera.text}
                """.trimIndent()

                val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                    data,
                    BarcodeFormat.QR_CODE,
                    750,
                    750
                )

                ivCodigoQR.setImageBitmap(bitmap)

                // Convertir el bitmap a una cadena base64
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)

                // Guardar la cadena base64 en SharedPreferences
                val sharedPreferences = getSharedPreferences("QRPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("qr_code", encodedString)
                editor.apply()

                // Iniciar DatosPasaporteActivity
                val intent = Intent(this, DatosPasaporteActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
