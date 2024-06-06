package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

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
        // Obtener los datos de Firestore
        obtenerDatosFirestore()

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
            CurvedBottomNavigation.Model(5, "Cursos", R.drawable.baseline_assignment_24)
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
                    replaceActivity(CursosActivity::class.java)
                    true
                }
                else -> false
            }
        }
        bottomNavigation.show(4)
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
    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
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
