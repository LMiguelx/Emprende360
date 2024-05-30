package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth = Firebase.auth

        // Inicialización del DrawerLayout y NavigationView
        drawer = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

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

        // Configuración de botones de ver más
        val btnVerMas: TextView = findViewById(R.id.btndireccional)
        val btnVerMas2: TextView = findViewById(R.id.btndireccional1)
        btnVerMas.setOnClickListener {
            startActivity(Intent(this, EventosActivity::class.java))
        }
        btnVerMas2.setOnClickListener {
            startActivity(Intent(this, EventosActivity::class.java))
        }

        // Mostrar el nombre del usuario
        val userName = intent.getStringExtra("userName")
        val textViewHola = findViewById<TextView>(R.id.hola)
        textViewHola.text = "Hola, $userName"

        // Configuración de la navegación inferior
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
                    startActivity(Intent(this, EventosActivity::class.java))
                    true
                }
                R.id.bottom_cuestionario -> {
                    startActivity(Intent(this, CuestionarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
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
                signOut()
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

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this, SelectionActivity::class.java)
        startActivity(i)
        finish() // Cierra la actividad actual para evitar regresar al presionar atrás
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
                startActivity(Intent(this, EventosActivity::class.java))
            }
            R.id.nav_item_five -> {
                startActivity(Intent(this, CuestionarioActivity::class.java))
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

