package com.example.emprende360

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var spSemestre: Spinner
    private lateinit var spSeccion: Spinner
    private lateinit var etCodigoEstudiante: EditText
    private lateinit var spCarrera: Spinner
    private lateinit var etCodigoAcceso: EditText
    private lateinit var btnGenerar: Button

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

        // Configurar Spinners con datos del string-array
        configurarSpinner(spSemestre, R.array.Semestre)
        configurarSpinner(spSeccion, R.array.Seccion)
        configurarSpinner(spCarrera, R.array.Carreras)

        // Añadir listener a la flecha de cada Spinner
        configurarFlecha(spSemestre, R.id.spIcono1)
        configurarFlecha(spSeccion, R.id.spIcono2)
        configurarFlecha(spCarrera, R.id.spIcono3)

        btnGenerar.setOnClickListener {
            if (validarCampos()) {
                val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("NOMBRE_COMPLETO", etNombreCompleto.text.toString())
                intent.putExtra("SEMESTRE", spSemestre.selectedItem.toString())
                intent.putExtra("SECCION", spSeccion.selectedItem.toString())
                intent.putExtra("CODIGO_ESTUDIANTE", etCodigoEstudiante.text.toString())
                intent.putExtra("CARRERA", spCarrera.selectedItem.toString())
                intent.putExtra("CODIGO_ACCESO", etCodigoAcceso.text.toString())
                startActivity(intent)

                Handler().postDelayed({
                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()
                }, 3000)
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
        return etNombreCompleto.text.isNotEmpty() &&
                spSemestre.selectedItem != null &&
                spSeccion.selectedItem != null &&
                etCodigoEstudiante.text.isNotEmpty() &&
                spCarrera.selectedItem != null &&
                etCodigoAcceso.text.isNotEmpty()
    }
}
