package com.example.emprende360

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var etSemestre: EditText
    private lateinit var etSeccion: EditText
    private lateinit var etCodigoEstudiante: EditText
    private lateinit var etCarrera: EditText
    private lateinit var etCodigoAcceso: EditText
    private lateinit var btnGenerar: Button

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

        btnGenerar.setOnClickListener {
            if (validarCampos()) {
                val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("NOMBRE_COMPLETO", etNombreCompleto.text.toString())
                intent.putExtra("SEMESTRE", etSemestre.text.toString())
                intent.putExtra("SECCION", etSeccion.text.toString())
                intent.putExtra("CODIGO_ESTUDIANTE", etCodigoEstudiante.text.toString())
                intent.putExtra("CARRERA", etCarrera.text.toString())
                intent.putExtra("CODIGO_ACCESO", etCodigoAcceso.text.toString())
                startActivity(intent)

                Handler().postDelayed({
                    Toast.makeText(this,"Datos enviados correctamente",Toast.LENGTH_SHORT).show()},3000)
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validarCampos(): Boolean {
        return etNombreCompleto.text.isNotEmpty() &&
                etSemestre.text.isNotEmpty() &&
                etSeccion.text.isNotEmpty() &&
                etCodigoEstudiante.text.isNotEmpty() &&
                etCarrera.text.isNotEmpty() &&
                etCodigoAcceso.text.isNotEmpty()
    }
}