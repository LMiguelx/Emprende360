package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorEventos
    private val listaEventos: MutableList<Map<String, Any>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recycler_view_eventos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorEventos(listaEventos)
        recyclerView.adapter = adaptador

        // Configuración del DrawerLayout
        drawer = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Configuración del BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(this, PrincipalActivity::class.java))
                    true
                }
                R.id.bottom_id -> {
                    startActivity(Intent(this, DatosPasaporteActivity::class.java))
                    true
                }
                R.id.bottom_puntos -> {
                    startActivity(Intent(this, PuntosActivity::class.java))
                    true
                }
                R.id.bottom_eventos -> {
                    // No hacer nada ya que estamos en la misma actividad
                    true
                }
                R.id.bottom_cuestionario -> {
                    startActivity(Intent(this, CuestionarioActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Obtener los datos de Firestore
        obtenerDatosFirestore()
    }

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
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun obtenerDatosFirestore() {
        val db = FirebaseFirestore.getInstance()
        val eventosRef = db.collection("eventos")

        eventosRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val datosEvento = document.data
                    datosEvento?.let { evento ->
                        listaEventos.add(evento)
                    }
                }
                adaptador.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener eventos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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
                startActivity(Intent(this, PuntosActivity::class.java))
            }
            R.id.nav_item_four -> {
                // No hacer nada ya que estamos en la misma actividad
            }
            R.id.nav_item_five -> {
                startActivity(Intent(this, CuestionarioActivity::class.java))
            }
        }

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
