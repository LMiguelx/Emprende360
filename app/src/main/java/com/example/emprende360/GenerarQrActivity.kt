package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Context
import com.journeyapps.barcodescanner.BarcodeEncoder
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream


class GenerarQrActivity : AppCompatActivity() {

    private lateinit var et_nombre_completo: EditText
    private lateinit var sp_semestre: Spinner
    private lateinit var sp_seccion: Spinner
    private lateinit var et_codigo_estudiante: EditText
    private lateinit var sp_carrera: Spinner
    private lateinit var et_codigo_acceso: EditText
    private lateinit var btn_generar_qr: Button
    private lateinit var ivCodigoQR: ImageView
    private lateinit var hola: TextView // Agregamos el TextView para mostrar el nombre


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generar_qr)

        et_nombre_completo = findViewById(R.id.et_nombre_completo)
        sp_semestre = findViewById(R.id.sp_semestre)
        sp_seccion = findViewById(R.id.sp_seccion)
        et_codigo_estudiante = findViewById(R.id.et_codigo_estudiante)
        sp_carrera = findViewById(R.id.sp_carrera)
        et_codigo_acceso = findViewById(R.id.et_codigo_acceso)
        btn_generar_qr = findViewById(R.id.btn_generar_qr)
        ivCodigoQR = findViewById(R.id.ivCodigoQR)

        configurarSpinner(sp_semestre, R.array.Semestre)
        configurarSpinner(sp_seccion, R.array.Seccion)
        configurarSpinner(sp_carrera, R.array.Carreras)

        configurarFlecha(sp_semestre, R.id.spIcono1)
        configurarFlecha(sp_seccion, R.id.spIcono2)
        configurarFlecha(sp_carrera, R.id.spIcono3)

        btn_generar_qr.setOnClickListener {
            if (validarCampos()) {
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val data = """
                Nombre y Apellidos: ${et_nombre_completo.text}
                Semestre: ${sp_semestre.selectedItem}
                Sección: ${sp_seccion.selectedItem}
                Código del Estudiante: ${et_codigo_estudiante.text}
                Carrera: ${sp_carrera.selectedItem}
                Acceso RFID: ${et_codigo_acceso.text}
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

                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSpinner(spinner: Spinner, arrayResId: Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayResId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun configurarFlecha(spinner: Spinner, iconoResId: Int) {
        val iconoFlecha = findViewById<ImageView>(iconoResId)
        iconoFlecha.setOnClickListener {
            spinner.performClick()
        }
    }

    private fun validarCampos(): Boolean {
        return et_nombre_completo.text.isNotEmpty() &&
                sp_semestre.selectedItem != null &&
                sp_seccion.selectedItem != null &&
                et_codigo_estudiante.text.isNotEmpty() &&
                sp_carrera.selectedItem != null &&
                et_codigo_acceso.text.isNotEmpty()
    }

    private fun obtenerDatosFormularioXComoString(): String {
        return "${et_nombre_completo.text},${sp_semestre.selectedItem},${sp_seccion.selectedItem}," +
                "${et_codigo_estudiante.text},${sp_carrera.selectedItem},${et_codigo_acceso.text}"

    }
}

