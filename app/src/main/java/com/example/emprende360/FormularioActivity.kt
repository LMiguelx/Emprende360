package com.example.emprende360

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var etSemestre: EditText
    private lateinit var etSeccion: EditText
    private lateinit var etCodigoEstudiante: EditText
    private lateinit var etCarrera: EditText
    private lateinit var etCodigoAcceso: EditText
    private lateinit var btnGenerar: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var correo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        etSemestre = findViewById(R.id.etSemestre)
        etSeccion = findViewById(R.id.etSeccion)
        etCodigoEstudiante = findViewById(R.id.etCodigoEstudiante)
        etCarrera = findViewById(R.id.etCarrera)
        etCodigoAcceso = findViewById(R.id.etCodigoAcceso)
        btnGenerar = findViewById(R.id.btnGenerar)

        db = FirebaseFirestore.getInstance()

        // Obtener el UID y el correo del intent
        userId = intent.getStringExtra("userId").toString()
        correo = intent.getStringExtra("correo").toString()

        btnGenerar.setOnClickListener {
            if (validarCampos()) {
                validarCodigoAccesoYEnviarDatos()
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
        configurarEditTextComoSpinner(etSemestre, findViewById(R.id.spIcono1), resources.getStringArray(R.array.Semestre))
        configurarEditTextComoSpinner(etSeccion, findViewById(R.id.spIcono2), resources.getStringArray(R.array.Seccion))
        configurarEditTextComoSpinner(etCarrera, findViewById(R.id.spIcono3), resources.getStringArray(R.array.Carreras))


    }

    private fun validarCampos(): Boolean {
        return etNombreCompleto.text.isNotEmpty() &&
                etSemestre.text.isNotEmpty() &&
                etSeccion.text.isNotEmpty() &&
                etCodigoEstudiante.text.isNotEmpty() &&
                etCarrera.text.isNotEmpty() &&
                etCodigoAcceso.text.isNotEmpty()
    }

    private fun validarCodigoAccesoYEnviarDatos() {
        val codigoAcceso = etCodigoAcceso.text.toString()

        db.collection("tarjetas").document(codigoAcceso).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getBoolean("estado") == false) {
                    enviarDatosAFirestore(codigoAcceso)
                } else {
                    Toast.makeText(this, "Código de acceso no válido o ya usado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al validar el código de acceso", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enviarDatosAFirestore(codigoAcceso: String) {
        val nombreCompleto = etNombreCompleto.text.toString()
        val semestre = etSemestre.text.toString()
        val seccion = etSeccion.text.toString()
        val codigoEstudiante = etCodigoEstudiante.text.toString()
        val carrera = etCarrera.text.toString()

        val estudiante = hashMapOf(
            "nombreCompleto" to nombreCompleto,
            "semestre" to semestre,
            "seccion" to seccion,
            "codigoEstudiante" to codigoEstudiante,
            "carrera" to carrera,
            "codigoAcceso" to codigoAcceso,
            "correo" to correo,
            "userId" to userId
        )

        db.collection("estudiantes").document(codigoAcceso)
            .set(estudiante)
            .addOnSuccessListener {
                // Actualizar estado del código de acceso a true
                db.collection("tarjetas").document(codigoAcceso)
                    .update("estado", true)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()
                        abrirDatosPasaporteActivity(nombreCompleto)
                        abrirDatosPrincipalActivity(codigoAcceso)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar el estado del código de acceso", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirDatosPasaporteActivity(nombreCompleto: String) {
        val intent = Intent(this, DatosPasaporteActivity::class.java).apply {
            putExtra("nombreCompleto", nombreCompleto)
            // Añade otros extras si es necesario
        }
        startActivity(intent)
    }

    private fun abrirDatosPrincipalActivity(codigoAcceso: String) {
        val intent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("codigoAcceso", codigoAcceso)
        }
        startActivity(intent)
    }

    private fun configurarEditTextComoSpinner(editText: EditText, flecha: ImageView, opciones: Array<String>) {
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            editText.setText(opciones[which])
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(opciones, dialogClickListener)
        val dialog = builder.create()

        flecha.setOnClickListener {
            dialog.show()
        }

        // Deshabilitar la edición de texto
        editText.isFocusable = false
        editText.isClickable = true
        editText.isLongClickable = false
        editText.keyListener = null
        editText.inputType = InputType.TYPE_NULL
    }
}

