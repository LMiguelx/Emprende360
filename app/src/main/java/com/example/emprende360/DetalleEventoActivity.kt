package com.example.emprende360

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DetalleEventoActivity : AppCompatActivity() {

    // Preferencias compartidas para obtener el código de acceso
    private lateinit var sharedPreferences: SharedPreferences

    // Referencia a Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_evento)

        // Activar Edge-to-Edge
        enableEdgeToEdge()

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

        // Obtener datos del intent
        val eventId = intent.getStringExtra("eventId")
        val nombre = intent.getStringExtra("nombre")
        val lugar = intent.getStringExtra("lugar")
        val aforo = intent.getIntExtra("aforo", 0)
        val descripcion = intent.getStringExtra("descripcion")
        val horarioTimestamp = intent.getLongExtra("horario", 0L)
        val imagen = intent.getStringExtra("imagen")
        val introduccion = intent.getStringExtra("introduccion")
        val temas = intent.getStringArrayListExtra("temas")

        // Formatear la fecha y hora del evento
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val horario = if (horarioTimestamp != 0L) {
            dateFormat.format(Date(horarioTimestamp * 1000))
        } else {
            ""
        }

        // Configurar las vistas con los datos del evento
        findViewById<TextView>(R.id.lblNombreEventoDetalle).text = nombre
        findViewById<TextView>(R.id.lblLugarDetalle).text = "Lugar: $lugar"
        findViewById<TextView>(R.id.lblAforoDetalle).text = "Aforo: $aforo"
        findViewById<TextView>(R.id.lblDescripcionDetalle).text = descripcion
        findViewById<TextView>(R.id.lblHorarioDetalle).text = "Horario: $horario"
        findViewById<TextView>(R.id.lblIntroduccionDetalle).text = introduccion
        findViewById<TextView>(R.id.lblTemasDetalle).text = "Temas: ${temas?.joinToString(", ") ?: ""}"

        // Cargar la imagen del evento
        val imageView = findViewById<ImageView>(R.id.imgFotoDetalle)
        if (!imagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagen)
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Botón para registrar la asistencia al evento
        val btnRegistrarse = findViewById<Button>(R.id.btnregistrarse)
        btnRegistrarse.setOnClickListener {
            val codigoAcceso = sharedPreferences.getString("codigoAcceso", null)
            if (codigoAcceso != null) {
                registrarAsistencia(eventId, codigoAcceso, nombre)
            } else {
                Toast.makeText(this, "No se pudo obtener el código de acceso", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para ir al cuestionario
        val btnCuestionario = findViewById<Button>(R.id.btncuestionario)
        btnCuestionario.setOnClickListener {
            // Crear el Intent y pasar el eventId como extra
            val intent = Intent(this, CuestionarioActivity::class.java).apply {
                putExtra("eventId", eventId) // Aquí pasamos el eventId obtenido del intent inicial
            }
            startActivity(intent)
        }
    }

    private fun registrarAsistencia(eventId: String?, codigoAcceso: String, nombre: String?) {
        // Verificar si ya existe un registro para el evento
        val registroRef = db.collection("registro")
            .whereEqualTo("eventoId", eventId)
            .whereEqualTo("codigoAcceso", codigoAcceso)

        registroRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result
                if (documents != null && !documents.isEmpty) {
                    // Ya está registrado para este evento
                    Toast.makeText(this, "Ya estás registrado para este evento", Toast.LENGTH_SHORT).show()
                } else {
                    // No está registrado
                    val builder = AlertDialog.Builder(this, R.style.AlertDialogPersonalizado)
                        .setTitle("Confirmar Asistencia")
                        .setMessage("¿Estás seguro de que deseas registrarte para el evento '$nombre'?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            val nuevoRegistro = hashMapOf(
                                "eventoId" to eventId,
                                "codigoAcceso" to codigoAcceso
                            )
                            db.collection("registro")
                                .add(nuevoRegistro)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "¡Te has registrado para el evento '$nombre'!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al registrar la asistencia: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } else {
                Toast.makeText(this, "Error al verificar el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
