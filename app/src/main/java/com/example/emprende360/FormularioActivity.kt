package com.example.emprende360


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var spSemestre: Spinner
    private lateinit var spSeccion: Spinner
    private lateinit var etCodigoEstudiante: EditText
    private lateinit var spCarrera: Spinner
    private lateinit var etCodigoAcceso: EditText
    private lateinit var btnGenerar: Button
    private lateinit var ivCodigoQR: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        spSemestre = findViewById(R.id.spSemestre)
        spSeccion = findViewById(R.id.spSeccion)
        etCodigoEstudiante = findViewById(R.id.etCodigoEstudiante)
        spCarrera = findViewById(R.id.spCarrera)
        etCodigoAcceso = findViewById(R.id.etCodigoAcceso)
        btnGenerar = findViewById(R.id.btnGenerar)
        ivCodigoQR = findViewById(R.id.ivCodigoQR)

        configurarSpinner(spSemestre, R.array.Semestre)
        configurarSpinner(spSeccion, R.array.Seccion)
        configurarSpinner(spCarrera, R.array.Carreras)

        configurarFlecha(spSemestre, R.id.spIcono1)
        configurarFlecha(spSeccion, R.id.spIcono2)
        configurarFlecha(spCarrera, R.id.spIcono3)

        btnGenerar.setOnClickListener {
            if (validarCampos()) {
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val data = """
                Nombre y Apellidos: ${etNombreCompleto.text}
                Semestre: ${spSemestre.selectedItem}
                Sección: ${spSeccion.selectedItem}
                Código del Estudiante: ${etCodigoEstudiante.text}
                Carrera: ${spCarrera.selectedItem}
                Acceso RFID: ${etCodigoAcceso.text}
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
                    val intent = Intent(this, GenerarQrActivity::class.java)
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

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        return
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
        return etNombreCompleto.text.isNotEmpty() &&
                spSemestre.selectedItem != null &&
                spSeccion.selectedItem != null &&
                etCodigoEstudiante.text.isNotEmpty() &&
                spCarrera.selectedItem != null &&
                etCodigoAcceso.text.isNotEmpty()
    }

    private fun obtenerDatosFormularioComoString(): String {
        return "${etNombreCompleto.text},${spSemestre.selectedItem},${spSeccion.selectedItem}," +
                "${etCodigoEstudiante.text},${spCarrera.selectedItem},${etCodigoAcceso.text}"
    }
}