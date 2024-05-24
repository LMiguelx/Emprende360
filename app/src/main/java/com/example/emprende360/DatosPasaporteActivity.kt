package com.example.emprende360

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
import com.google.firebase.ktx.Firebase

class DatosPasaporteActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    //Foto de Perfil
    private lateinit var imageViewProfile: ImageView
    private val sharedPreferences by lazy { getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    private var currentImageUri: Uri? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_pasaporte)
        // Inicializar FirebaseAuth
        firebaseAuth = Firebase.auth

        val sellosregis: Button = findViewById(R.id.SellosRegistrados)
        val ivCodigoQR: ImageView = findViewById(R.id.ivCodigoQR)


        // Recuperar la cadena base64 del código QR desde SharedPreferences
        val sharedPreferences = getSharedPreferences("QRPrefs", Context.MODE_PRIVATE)
        val encodedString = sharedPreferences.getString("qr_code", null)

        // Si hay una cadena base64 guardada, decodificarla y mostrar el código QR
        if (encodedString != null) {
            val decodedString = Base64.decode(encodedString, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            ivCodigoQR.setImageBitmap(decodedByte)
        }

        // Configurar listener para el botón SellosRegistrados
        sellosregis.setOnClickListener {
            startActivity(Intent(this, SellosRegistradosActivity::class.java))
        }

        //Se carga la imagen aqui
        //fotos perfil

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
        //Aqui se termina sobre foto de perfil

        //Donde se muestra los nombres en el layout principal
        val userName = intent.getStringExtra("userName")
        val textViewHola = findViewById<TextView>(R.id.hola)
        textViewHola.text = "Hola, $userName"

        val actulizarqr: Button = findViewById(R.id.ActilizarQR)
        actulizarqr.setOnClickListener {
            startActivity(Intent(this, GenerarQrActivity::class.java))

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
