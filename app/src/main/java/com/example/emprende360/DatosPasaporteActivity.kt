package com.example.emprende360

import android.graphics.Color
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.emprende360.R
import com.example.emprende360.SellosRegistradosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class DatosPasaporteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
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

        mostrarDatos(nombreCompleto, semestre, seccion, codigoEstudiante, carrera, codigoAcceso)

        //funcion del boton de navegacion inferior-------------------------------------------
        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "Home", R.drawable.baseline_home_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Puntos", R.drawable.baseline_123_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "Pasaporte", R.drawable.baseline_perm_identity_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4, "Eventos", R.drawable.baseline_ballot_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(5, "Preguntas", R.drawable.baseline_assignment_24)
        )

        bottomNavigation.setOnClickMenuListener { item ->
            when (item.id) {
                1 -> {
                    replaceActivity(PrincipalActivity::class.java)
                    true
                }
                2 -> {
                    replaceActivity(PuntosActivity::class.java)
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
                    replaceActivity(CuestionarioActivity::class.java)
                    true
                }
                else -> false
            }
        }
        bottomNavigation.show(3)
    }
    private fun replaceActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun mostrarDatos(nombreCompleto: String?, semestre: String?, seccion: String?, codigoEstudiante: String?, carrera: String?, codigoAcceso: String?) {
        if (nombreCompleto != null && semestre != null && seccion != null && codigoEstudiante != null && carrera != null && codigoAcceso != null) {
            // Si los datos están disponibles, mostrarlos en las vistas correspondientes
            val textViewHola = findViewById<TextView>(R.id.hola)
            textViewHola.text = nombreCompleto


            val data = "$nombreCompleto\n$semestre\n$seccion\n$codigoEstudiante\n$carrera\n$codigoAcceso"
            val qrBitmap = generarQR(data)
            if (qrBitmap != null) {
                val ivCodigoQR = findViewById<ImageView>(R.id.ivCodigoQR)
                ivCodigoQR.setImageBitmap(qrBitmap)
            }
        } else {
            obtenerDatosDeFirestore()
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

        firebaseAuth = Firebase.auth

        //drawel donde se define la varibles y el llamado del drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationVie: NavigationView = findViewById(R.id.nav_view)
        navigationVie.setNavigationItemSelectedListener(this)


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
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
            val docRef = db.collection("estudiantes").document(userId)
            docRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Obtener datos del documento y mostrarlos
                    val nombreCompleto = document.getString("nombreCompleto")
                    val semestre = document.getString("semestre")
                    val seccion = document.getString("seccion")
                    val codigoEstudiante = document.getString("codigoEstudiante")
                    val carrera = document.getString("carrera")
                    val codigoAcceso = document.getString("codigoAcceso")

                    mostrarDatos(nombreCompleto, semestre, seccion, codigoEstudiante, carrera, codigoAcceso)
                }
            }.addOnFailureListener { exception ->
                // Manejar el error
                Toast.makeText(this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Abre la galería de imágenes para que el usuario seleccione una imagen.
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }


    //Guarda la URI de la imagen seleccionada en SharedPreferences.
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


     //Carga la URI de la imagen guardada en SharedPreferences y establece la imagen en la vista.
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

     //Establece la imagen en la vista de perfil utilizando la URI proporcionada.

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


    //Muestra un diálogo para que el usuario decida si desea guardar la imagen seleccionada o elegir otra.
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

    //Solicita permiso para acceder al almacenamiento del dispositivo y abre la galería si se concede el permiso.

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    //Lanza una actividad para seleccionar una imagen y maneja el resultado.
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_buscar -> {
                Toast.makeText(baseContext, "Buscar información", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_salir -> {
                signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        return
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }

    //Los drawers aqui desde la funcion -----------------------------------------------------------------------------

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> {
                // Iniciar PrincipalActivity cuando se hace clic en el primer ítem del menú
                startActivity(Intent(this, PrincipalActivity::class.java))
            }
            R.id.nav_item_two -> {
                // Iniciar PerfilActivity
                startActivity(Intent(this, DatosPasaporteActivity::class.java))
            }
            R.id.nav_item_three -> {
                // Iniciar PuntosActivity
                startActivity(Intent(this, PuntosActivity::class.java))
            }
            R.id.nav_item_four -> {
                // Iniciar EventosActivity
                startActivity(Intent(this, EventosActivity::class.java))
            }
            R.id.nav_item_five -> {
                // Iniciar CuestionarioActivity
                startActivity(Intent(this, CuestionarioActivity::class.java))
            }
        }

        // Cerrar el drawer después de manejar la selección
        drawerLayout.closeDrawer(GravityCompat.START) // Corrección aquí
        return true
    }

    //Los drawers finalizan aqui -----------------------------------------------------------------------------

    override fun onPostCreate(savedInstanceState: Bundle?,) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.syncState()
    }
}
