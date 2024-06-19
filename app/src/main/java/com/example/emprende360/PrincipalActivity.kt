package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import java.text.SimpleDateFormat
import java.util.Locale

class PrincipalActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val sharedPreferences by lazy {
        getSharedPreferences(
            "profile_prefs",
            Context.MODE_PRIVATE
        )
    }

    // Slider
    private lateinit var viewPager2: ViewPager2
    private val sliderHandler = Handler()


    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        // Inicialización del DrawerLayout y NavigationView
        drawer = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val verMasEventosCarview: TextView = findViewById(R.id.btn_ver_mas)
        val verMasEventosCarview2: TextView = findViewById(R.id.btn_ver_mas2)

        val headerView = navigationView.getHeaderView(0)
        val imgUserProfile:ImageView = headerView.findViewById(R.id.nav_header_imagenView)
        val txtUserEmail: TextView = headerView.findViewById(R.id.headertext)

        val email = sharedPreferences.getString("email", "")
        val photoUrl = sharedPreferences.getString("photoUrl", "")

        photoUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.google) // Imagen predeterminada mientras carga
                .error(R.drawable.google) // Imagen predeterminada si hay error
                .circleCrop()
                .into(imgUserProfile)
        }
        txtUserEmail.text = email

        verMasEventosCarview.setOnClickListener {
            val intent23 = Intent(this, EventosActivity::class.java)
            startActivity(intent23)
        }
        verMasEventosCarview2.setOnClickListener {
            val intent23 = Intent(this, CursosActivity::class.java)
            startActivity(intent23)
        }


        // Configuración de la barra de herramientas
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        establecerNombreUsuario()
        cargarImagenCursoMasBarato()

        // Función del botón de navegación inferior
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
        bottomNavigation.show(1)

        // Slider de imágenes
        viewPager2 = findViewById(R.id.viewPagerImageSlider)


        // Obtener imágenes desde Firestore
        db.collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                val sliderItems = result.mapNotNull { it.getString("imagen") }
                viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
            }

        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        })
        viewPager2.setPageTransformer(compositePageTransformer)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000) // 3000 ms = 3 seconds
            }
        })


    }
    private fun cargarImagenCursoMasBarato() {
        db.collection("cursos")
            .orderBy("precio", Query.Direction.ASCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val curso = documents.first()
                    val urlImagen = curso.getString("imagen")

                    // Cargar la imagen en el ImageView correspondiente
                    val imageView: ImageView = findViewById(R.id.image_cursos_cercanos)
                    Glide.with(this)
                        .load(urlImagen)
                        .placeholder(R.drawable.ciberseguridad)
                        .error(R.drawable.ciberseguridad)
                        .into(imageView)
                } else {
                    Log.d("PrincipalActivity", "No se encontraron cursos")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "PrincipalActivity",
                    "Error al cargar el curso con precio más bajo",
                    exception
                )
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Detener la actualización al cerrar la actividad
    }

    private fun establecerNombreUsuario() {
        val nombreUsuario = sharedPreferences.getString("nombreCompleto", "Usuario")
        findViewById<TextView>(R.id.nombre)?.text = "Hola $nombreUsuario"
    }



    private val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun replaceActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> {
                startActivity(Intent(this, PrincipalActivity::class.java))
            }
            R.id.nav_item_two -> {
                startActivity(Intent(this, DatosPasaporteActivity::class.java))
            }
            R.id.nav_item_three -> {
                startActivity(Intent(this, EventosAsistidosActivity::class.java))
            }
            R.id.nav_item_four -> {
                startActivity(Intent(this, EventosActivity::class.java))
            }
            R.id.nav_item_five -> {
                startActivity(Intent(this, CursosActivity::class.java))
            }
            R.id.nav_item_eight -> {
                signOut()
            }
        }

        // Cerrar el drawer después de manejar la selección
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.syncState()
    }
}
