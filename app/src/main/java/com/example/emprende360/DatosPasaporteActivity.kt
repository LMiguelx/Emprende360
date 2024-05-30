package com.example.emprende360

import android.graphics.Color
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.emprende360.R
import com.example.emprende360.SellosRegistradosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class DatosPasaporteActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var nombreUsuario: String // Aquí debes inicializar esta variable con el nombre del usuario

    //Foto de Perfil
    private lateinit var imageViewProfile: ImageView
    private val sharedPreferences by lazy { getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    private var currentImageUri: Uri? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_pasaporte)

        // Inicializar FirebaseAuth
        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val nombreCompleto = intent.getStringExtra("nombreCompleto")
        val semestre = intent.getStringExtra("semestre")
        val seccion = intent.getStringExtra("seccion")
        val codigoEstudiante = intent.getStringExtra("codigoEstudiante")
        val carrera = intent.getStringExtra("carrera")
        val codigoAcceso = intent.getStringExtra("codigoAcceso")

        // Mostrar el nombre en el TextView hola
        val textViewHola = findViewById<TextView>(R.id.hola)
        textViewHola.text = "$nombreCompleto"

        // Generar el QR con los datos del usuario y mostrarlo en la ImageView ivCodigoQR
        val data = "$nombreCompleto\n$semestre\n$seccion\n$codigoEstudiante\n$carrera\n$codigoAcceso"
        val qrBitmap = generarQR(data)
        if (qrBitmap != null) {
            val ivCodigoQR = findViewById<ImageView>(R.id.ivCodigoQR)
            ivCodigoQR.setImageBitmap(qrBitmap)
        }

        // Configurar listener para el botón SellosRegistrados
        val sellosRegistradosButton: Button = findViewById(R.id.SellosRegistrados)
        sellosRegistradosButton.setOnClickListener {
            startActivity(Intent(this, SellosRegistradosActivity::class.java))
        }

        // Se carga la imagen aquí
        // Fotos perfil
        imageViewProfile = findViewById(R.id.imageViewProfile)

        imageViewProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        val actulizarqr: TextView= findViewById(R.id.ActualizarQR)
        actulizarqr.setOnClickListener {
            startActivity(Intent(this, EventosActivity::class.java))
        }
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

    /**
     * Abre la galería de imágenes para que el usuario seleccione una imagen.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    /**
     * Guarda la URI de la imagen seleccionada en SharedPreferences.
     *
     * @param imageUri La URI de la imagen seleccionada.
     */
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

    /**
     * Carga la URI de la imagen guardada en SharedPreferences y establece la imagen en la vista.
     */
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

    /**
     * Establece la imagen en la vista de perfil utilizando la URI proporcionada.
     *
     * @param imageUri La URI de la imagen que se va a establecer.
     */
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

    /**
     * Muestra un diálogo para que el usuario decida si desea guardar la imagen seleccionada o elegir otra.
     *
     * @param imageUri La URI de la imagen seleccionada.
     */
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

    /**
     * Solicita permiso para acceder al almacenamiento del dispositivo y abre la galería si se concede el permiso.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lanza una actividad para seleccionar una imagen y maneja el resultado.
     */
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
    } //Ultimas linea de codigo de foto perfil
}
