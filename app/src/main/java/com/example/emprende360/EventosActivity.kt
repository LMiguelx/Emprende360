package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth


class EventosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)


        firebaseAuth = com.google.firebase.ktx.Firebase.auth

        //drawel donde se define la varibles y el llamado del drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationVie: NavigationView = findViewById(R.id.nav_view)
        navigationVie.setNavigationItemSelectedListener(this)


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        //funcion del boton de navegacion inferior-------------------------------------------
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
        //fin de boton de navegacion inferior     -------------------------------------------

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
            //R.id.nav_item_six -> {
            // Iniciar PerfilActivity
            //  startActivity(Intent(this, PerfilActivity::class.java))
            //}
            //R.id.nav_item_seven -> {
            // Iniciar PuntosActivity
            //  startActivity(Intent(this, PuntosActivity::class.java))
            //}
            //R.id.nav_item_eight -> {
            // Iniciar SeguridadActivity
            //startActivity(Intent(this, SeguridadActivity::class.java))
            //}
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