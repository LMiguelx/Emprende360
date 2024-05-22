package com.example.emprende360


import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth = Firebase.auth

        // Asumiendo que estás usando Kotlin
        val ingresoPasaporte: Button = findViewById(R.id.ingresoPasaporte)
        val ingresoqrgenerator: Button = findViewById(R.id.ingresoqrgenerator)

        ingresoPasaporte.setOnClickListener {
            // Acción para ingresoPasaporte
            // Por ejemplo, puedes abrir una nueva actividad o realizar alguna otra acción
            val intent = Intent(this, PortadaPasaPorteActivity::class.java)
            startActivity(intent)
        }

        ingresoqrgenerator.setOnClickListener {
            // Acción para ingresoqrgenerator
            // Por ejemplo, puedes abrir una nueva actividad o realizar alguna otra acción
            val intent = Intent(this, GenerearQrActivity::class.java)
            startActivity(intent)
        }
        //Gin de Generar QR



        //drawel donde se define la varibles y el llamado del drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationVie: NavigationView = findViewById(R.id.nav_view)
        navigationVie.setNavigationItemSelectedListener(this)

        //Boton de ver mas de principal los eventos
        val btnVerMas: TextView = findViewById(R.id.btndireccional)
        val btnVerMas2: TextView = findViewById(R.id.btndireccional1)

        //Donde se muestra los nombres en el layout principal
        val userName = intent.getStringExtra("userName")
        val textViewHola = findViewById<TextView>(R.id.hola)
        textViewHola.text = "Hola, $userName"
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        btnVerMas.setOnClickListener {
            // Lógica para iniciar la actividad CursosActivity
            startActivity(Intent(this, EventosActivity::class.java))
        }
        btnVerMas2.setOnClickListener {
            // Lógica para iniciar la actividad CursosActivity
            startActivity(Intent(this, EventosActivity::class.java))
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
        return
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this, SelectionActivity::class.java)
        startActivity(i)
    }

    //Los drawers aqui desde la funcion -----------------------------------------------------------------------------

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> {
                // Iniciar EventosActivity cuando se hace clic en el primer ítem del menú
                startActivity(Intent(this, PrincipalActivity::class.java))
            }
            R.id.nav_item_two -> {
                // Iniciar PerfilActivity
                startActivity(Intent(this, PerfilActivity::class.java))
            }
            R.id.nav_item_three -> {
                // Iniciar PuntosActivity
                startActivity(Intent(this, PuntosActivity::class.java))
            }
            R.id.nav_item_four -> {
                // Iniciar EventosProximosActivity
                startActivity(Intent(this, EventosActivity::class.java))
            }
            R.id.nav_item_five -> {
                // Iniciar CuestionarioActivity
                startActivity(Intent(this, CuestionarioActivity::class.java))
            }
            R.id.nav_item_six -> {
                // Iniciar ConfiguracionGeneralActivity
                startActivity(Intent(this, PerfilActivity::class.java))
            }
            R.id.nav_item_seven -> {
                // Iniciar CuentaActivity
                startActivity(Intent(this, PuntosActivity::class.java))
            }
            //R.id.nav_item_eight -> {
            // Iniciar SeguridadActivity
            //startActivity(Intent(this, SeguridadActivity::class.java))
            //}
        }

        // Cerrar el drawer después de manejar la selección
        drawer.closeDrawer(GravityCompat.START) // Corrección aquí
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
