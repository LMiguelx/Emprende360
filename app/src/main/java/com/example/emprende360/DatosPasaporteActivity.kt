package com.example.emprende360

import android.graphics.Color
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import java.text.SimpleDateFormat
import java.util.Locale
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.TypedValue


class DatosPasaporteActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var nombreUsuario: String
    private val eventosAsistidosRegistrados = mutableListOf<String>()
    private var fechasUnicas = HashSet<String>()

    //Foto de Perfil
    private lateinit var imageViewProfile: ImageView
    private val sharedPreferences by lazy {
        getSharedPreferences(
            "profile_prefs",
            Context.MODE_PRIVATE
        )
    }
    private fun saveCodigoAcceso(codigoAcceso: String?, nombreCompleto: String?) {
        codigoAcceso?.let {
            sharedPreferences.edit().putString("codigoAcceso", it).apply()
            nombreCompleto?.let { nombre ->
                sharedPreferences.edit().putString("nombreCompleto", nombre).apply()
            }
        }
    }
    private var currentImageUri: Uri? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_pasaporte)

        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val nombreCompleto = intent.getStringExtra("nombreCompleto")
        val semestre = intent.getStringExtra("semestre")
        val seccion = intent.getStringExtra("seccion")
        val codigoEstudiante = intent.getStringExtra("codigoEstudiante")
        val carrera = intent.getStringExtra("carrera")
        val codigoAcceso = intent.getStringExtra("codigoAcceso")

        mostrarDatos(
            nombreCompleto,
            semestre,
            seccion,
            codigoEstudiante,
            carrera,
            codigoAcceso,
            null,
            null
        )

        //funcion del boton de navegacion inferior-------------------------------------------
        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "Home", R.drawable.baseline_home_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Asistencia", R.drawable.baseline_123_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "Pasaporte", R.drawable.baseline_perm_identity_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4, "Eventos", R.drawable.baseline_ballot_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(5, "Cursos", R.drawable.baseline_assignment_24)
        )

        bottomNavigation.setOnClickMenuListener { item ->
            when (item.id) {
                1 -> {
                    replaceActivity(PrincipalActivity::class.java)
                    true
                }

                2 -> {
                    replaceActivity(EventosAsistidosActivity::class.java)
                    true
                }

                3 -> {
                    replaceActivity(DatosPasaporteActivity::class.java)
                    true
                }

                4 -> {
                    replaceActivity(EventosActivity::class.java)
                    true
                }

                5 -> {
                    replaceActivity(CursosActivity::class.java)
                    true
                }

                else -> false
            }
        }
        bottomNavigation.show(3)

        val textView = findViewById<TextView>(R.id.textbienvenido)
        animateTextSize(textView, 10f, 25f)
    }

    private fun animateTextSize(textView: TextView, startSize: Float, endSize: Float) {
        val increaseAnimator = ValueAnimator.ofFloat(startSize, endSize).apply {
            duration = 1000 // Duración de la animación en milisegundos
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue)
            }
        }

        val decreaseAnimator = ValueAnimator.ofFloat(endSize, startSize).apply {
            duration = 1000 // Duración de la animación en milisegundos
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue)
            }
        }

        val animatorSet = AnimatorSet().apply {
            playSequentially(increaseAnimator, decreaseAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Reiniciamos el conjunto de animadores para que el bucle continúe
                    start()
                }
            })
        }
        animatorSet.start()
    }

    private fun replaceActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun mostrarDatos(
        nombreCompleto: String?,
        semestre: String?,
        seccion: String?,
        codigoEstudiante: String?,
        carrera: String?,
        codigoAcceso: String?,
        userId: String?,
        correo: String?
    ) {
        if (nombreCompleto != null && semestre != null && seccion != null && codigoEstudiante != null && carrera != null && codigoAcceso != null && userId != null && correo != null) {
            // Si los datos están disponibles, mostrarlos en las vistas correspondientes
            val textViewnombre = findViewById<TextView>(R.id.Nombre)
            textViewnombre.text = nombreCompleto
            val textViewcorreo = findViewById<TextView>(R.id.Gmail)
            textViewcorreo.text = codigoAcceso

            val data =
                "$nombreCompleto\n$semestre\n$seccion\n$codigoEstudiante\n$carrera\n$codigoAcceso\n$userId\n$correo"
            val qrBitmap = generarQR(data)
            if (qrBitmap != null) {
                val ivCodigoQR = findViewById<ImageView>(R.id.ivCodigoQR)
                ivCodigoQR.setImageBitmap(qrBitmap)
            }
            obtenerPuntosDelEstudiante(codigoAcceso) { puntosTotales ->
                mostrarPuntos(puntosTotales)
                actualizarPuntosEstudiante(codigoAcceso, puntosTotales)
                obtenerEventosAsistidosParaTodos(codigoAcceso)
                eliminarDuplicados(codigoAcceso)
                saveCodigoAcceso(codigoAcceso,nombreCompleto)
            }
        } else {
            obtenerDatosDeFirestore()
        }
        // Se carga la imagen aquí
        // Fotos perfil
        imageViewProfile = findViewById(R.id.imageViewProfile)

        imageViewProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                openGallery()
            }
        }

        // Cargar la imagen guardada en el inicio
        loadSavedImage()
        // Aquí termina sobre foto de perfil

        // Donde se muestra los nombres en el layout principal
        val userName = intent.getStringExtra("userName")

        firebaseAuth = Firebase.auth

    }

    private fun generarQR(data: String): Bitmap? {
        return try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 400, 400)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun obtenerDatosDeFirestore() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            db.collection("estudiantes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val nombreCompleto = document.getString("nombreCompleto")
                        val semestre = document.getString("semestre")
                        val seccion = document.getString("seccion")
                        val codigoEstudiante = document.getString("codigoEstudiante")
                        val carrera = document.getString("carrera")
                        val codigoAcceso = document.getString("codigoAcceso")
                        val correo = document.getString("correo")

                        mostrarDatos(
                            nombreCompleto,
                            semestre,
                            seccion,
                            codigoEstudiante,
                            carrera,
                            codigoAcceso,
                            userId,
                            correo
                        )
                    }
                }.addOnFailureListener { exception ->
                    // Manejar el error
                    Toast.makeText(
                        this,
                        "Error al obtener los datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Abre la galería de imágenes para que el usuario seleccione una imagen.
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    // Guarda la URI de la imagen seleccionada en SharedPreferences.
    //@param imageUri La URI de la imagen seleccionada.
    private fun saveImageUri(imageUri: Uri) {
        try {
            val editor = sharedPreferences.edit()
            editor.putString("profile_image_uri", imageUri.toString())
            editor.apply()
            Log.d("MainActivity", "URI guardada: $imageUri")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivity", "Error al guardar la URI de la imagen", e)
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Carga la URI de la imagen guardada en SharedPreferences y establece la imagen en la vista.
    private fun loadSavedImage() {
        try {
            val imageUriString = sharedPreferences.getString("profile_image_uri", null)
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                setImage(imageUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivity", "Error al cargar la URI de la imagen", e)
            Toast.makeText(this, "Error al cargar la imagen guardada", Toast.LENGTH_SHORT).show()
        }
    }

    // Establece la imagen en la vista de perfil utilizando la URI proporcionada.
    //@param imageUri La URI de la imagen que se va a establecer.
    private fun setImage(imageUri: Uri) {
        try {
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile)
            Log.d("MainActivity", "Imagen establecida desde URI: $imageUri")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivity", "Error al cargar la imagen desde la URI", e)
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Muestra un diálogo para que el usuario decida si desea guardar la imagen seleccionada o elegir otra.
    //@param imageUri La URI de la imagen seleccionada.
    private fun showSaveOrSelectDialog(imageUri: Uri) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("¿Quieres guardar esta imagen o seleccionar otra?")
            .setCancelable(false)
            .setPositiveButton("Guardar") { dialog, _ ->
                saveImageUri(imageUri)
                setImage(imageUri)
                dialog.dismiss()
            }
            .setNegativeButton("Seleccionar otra") { dialog, _ ->
                openGallery()
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Seleccionar Imagen")
        alert.show()
    }

    // Solicita permiso para acceder al almacenamiento del dispositivo y abre la galería si se concede el permiso.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanza una actividad para seleccionar una imagen y maneja el resultado.
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                currentImageUri = imageUri
                showSaveOrSelectDialog(imageUri)
            }
        }
    } // Últimas líneas de código de foto perfil

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        return
    }
    private fun eliminarDuplicados(codigoAcceso: String) {
        val eventosAsistidosRef = db.collection("eventosAsistidos")

        eventosAsistidosRef
            .whereEqualTo("codigoAcceso", codigoAcceso)
            .get()
            .addOnSuccessListener { snapshot ->
                val uniqueEvents = mutableMapOf<Pair<String, String>, String>()
                val duplicates = mutableListOf<String>()

                for (doc in snapshot.documents) {
                    val data = doc.data
                    val eventCodigoAcceso = data?.get("codigoAcceso") as? String
                    val eventNombre = data?.get("nombre") as? String

                    if (eventCodigoAcceso != null && eventNombre != null) {
                        val key = Pair(eventCodigoAcceso, eventNombre)
                        if (uniqueEvents.containsKey(key)) {
                            duplicates.add(doc.id)
                        } else {
                            uniqueEvents[key] = doc.id
                        }
                    }
                }

                for (duplicateId in duplicates) {
                    eventosAsistidosRef.document(duplicateId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("zzz", "Documento duplicado eliminado: $duplicateId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("zzz", "Error al eliminar el documento duplicado: $duplicateId", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("zzz", "Error al obtener los documentos de eventosAsistidos", e)
            }
    }


    private fun verificarEvento(codigoAcceso: String, nombreEvento: String) {
        // Verifica que el código de acceso tenga al menos un carácter numérico
        val tieneCaracterNumerico = codigoAcceso.any { it.isDigit() }

        if (!tieneCaracterNumerico) {
            Log.e("zzz", "El código de acceso debe contener al menos un carácter numérico")
            return  // Sale de la función si el código de acceso no cumple con el requisito
        }

        val eventosAsistidosRef = db.collection("eventosAsistidos")
        eventosAsistidosRef
            .whereEqualTo("codigoAcceso", codigoAcceso)
            .whereEqualTo("nombre", nombreEvento)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // El evento no está registrado, se crea el documento
                    val eventoAsistidoData = hashMapOf(
                        "nombre" to nombreEvento,
                        "codigoAcceso" to codigoAcceso
                        // Agregar otros campos relevantes del evento si es necesario
                    )

                    // Guardar el documento del evento asistido
                    eventosAsistidosRef
                        .add(eventoAsistidoData)
                        .addOnSuccessListener {
                            Log.d("zzz", "Documento creado en eventosAsistidos para el código de acceso $codigoAcceso y evento $nombreEvento")
                        }
                        .addOnFailureListener { e ->
                            Log.e("zzz", "Error al crear el documento en eventosAsistidos", e)
                        }
                } else {
                    // El evento ya está registrado para este código de acceso y nombre, no se realiza ninguna acción adicional
                    Log.d("zzz", "El evento $nombreEvento ya está registrado en eventosAsistidos para el código de acceso $codigoAcceso")
                }
            }
            .addOnFailureListener { e ->
                Log.e("zzz", "Error al verificar el evento en la colección de eventosAsistidos", e)
            }
    }


    private fun obtenerEventosAsistidosParaTodos(codigoAcceso: String) {
        db.collection("asistencias")
            .whereEqualTo("codigoAcceso", codigoAcceso)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val nombreEvento = document.getString("nombre")
                    nombreEvento?.let {
                        verificarEvento(codigoAcceso, nombreEvento)
                    }
                }
                eliminarDuplicados(codigoAcceso)
            }
            .addOnFailureListener { e ->
                Log.e("zzz", "Error al obtener las asistencias para el código de acceso $codigoAcceso", e)
            }
    }






    private fun mostrarPuntos(puntos: Int) {
        val textViewPuntos = findViewById<TextView>(R.id.DatoPuntos)
        textViewPuntos.text = "Puntos: $puntos"
    }
    private fun obtenerPuntosDelEstudiante(codigoAcceso: String, callback: (Int) -> Unit) {
        val estudianteRef = db.collection("estudiantes").document(codigoAcceso)
        estudianteRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val codigoAcceso = document.getString("codigoAcceso")

                codigoAcceso?.let {
                    db.collection("asistencias")
                        .whereEqualTo("codigoAcceso", codigoAcceso)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents != null && !documents.isEmpty) {
                                val fechasUnicas = HashSet<String>()
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                                for (document in documents) {
                                    val fechaAsistencia = document.getTimestamp("ingreso")
                                    val nombreEvento = document.getString("nombre")

                                    fechaAsistencia?.let {
                                        val fechaSinHora = dateFormat.format(it.toDate())
                                        fechasUnicas.add(fechaSinHora)
                                    }

                                    nombreEvento?.let {
                                        verificarEvento(nombreEvento,codigoAcceso)
                                    }
                                }

                                val puntosTotales = fechasUnicas.size
                                callback(puntosTotales)
                            } else {
                                Log.e("zz", "No se encontraron asistencias para el código de acceso $codigoAcceso")
                                callback(0)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("zzz", "Error al obtener las asistencias", e)
                            callback(0)
                        }
                } ?: run {
                    Log.e("zzz", "El código de acceso es nulo")
                    callback(0)
                }
            } else {
                Log.e("zzz", "No se encontró el documento del estudiante con ID $codigoAcceso")
                callback(0)
            }
        }
            .addOnFailureListener { e ->
                Log.e("zzz", "Error al obtener el documento del estudiante", e)
                callback(0)
            }
    }
    private fun actualizarPuntosEstudiante(codigoAcceso: String, nuevosPuntos: Int) {
        val estudianteRef = db.collection("estudiantes").document(codigoAcceso)

        estudianteRef
            .update("puntos_asistencia", nuevosPuntos)
            .addOnSuccessListener {
                Log.d("zzz", "Puntos del estudiante actualizados correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("zzz", "Error al actualizar los puntos del estudiante", e)
            }
    }
}