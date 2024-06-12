package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class EventosAsistidosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorEventosAsistidos
    private val listaEventosAsistidos: MutableList<Map<String, Any>> = mutableListOf()
    private var eventosCargados: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_evento_asistido)

        firebaseAuth = Firebase.auth

        // Configuración del DrawerLayout y el NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        // Configuración del RecyclerView y su adaptador
        recyclerView = findViewById(R.id.recycler_view_puntos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorEventosAsistidos(this, listaEventosAsistidos)
        recyclerView.adapter = adaptador
        actualizarEventosAsistidosConDatosDeEventos()


        // Configuración del botón de navegación inferior
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
        bottomNavigation.show(2)

        // Obtener eventos asociados por código de acceso solo si no se han cargado previamente
        if (!eventosCargados) {
            obtenerEventosAsociados()
        }
    }

    private fun actualizarLista(nuevaLista: List<Map<String, Any>>) {
        adaptador.updateData(nuevaLista)
    }

    private fun obtenerEventosAsociados() {
        val db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("estudiantes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { estudiantesSnapshot ->
                    if (!estudiantesSnapshot.isEmpty) {
                        val codigoAccesoEstudiante = estudiantesSnapshot.documents[0].id

                        db.collection("eventosAsistidos")
                            .whereEqualTo("codigoAcceso", codigoAccesoEstudiante)
                            .get()
                            .addOnSuccessListener { eventosSnapshot ->
                                val eventosAsistidos = mutableListOf<Map<String, Any>>()

                                for (eventoDoc in eventosSnapshot.documents) {
                                    val evento = eventoDoc.data
                                    if (evento != null) {
                                        eventosAsistidos.add(evento)
                                    }
                                }

                                listaEventosAsistidos.clear()
                                listaEventosAsistidos.addAll(eventosAsistidos)

                                actualizarLista(eventosAsistidos)
                            }
                            .addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }
    private fun actualizarEventosAsistidosConDatosDeEventos() {
        val db = FirebaseFirestore.getInstance()
        db.collection("eventosAsistidos")
            .get()
            .addOnSuccessListener { eventosAsistidosSnapshot ->
                // Iterar sobre cada documento de la colección "eventos asistidos"
                for (eventoAsistidoDoc in eventosAsistidosSnapshot.documents) {
                    val nombreEventoAsistido = eventoAsistidoDoc.getString("nombre")

                    if (nombreEventoAsistido != null) {
                        // Buscar documentos en la colección "eventos" que coincidan con el nombre del evento asistido
                        db.collection("eventos")
                            .whereEqualTo("nombre", nombreEventoAsistido)
                            .get()
                            .addOnSuccessListener { eventosSnapshot ->
                                // Si se encontraron documentos en la colección "eventos" con el mismo nombre,
                                // actualizar el documento de "eventos asistidos" con los datos correspondientes
                                if (!eventosSnapshot.isEmpty) {
                                    val datosEvento = eventosSnapshot.documents[0].data
                                    if (datosEvento != null) {
                                        // Actualizar el documento de "eventos asistidos" con los datos del evento encontrado
                                        eventoAsistidoDoc.reference.update(datosEvento)
                                            .addOnSuccessListener {
                                                Log.d("ActualizarEventosAsistidos", "Datos actualizados para el evento asistido: $nombreEventoAsistido")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("ActualizarEventosAsistidos", "Error al actualizar datos para el evento asistido: $nombreEventoAsistido", e)
                                            }
                                    }
                                } else {
                                    Log.d("ActualizarEventosAsistidos", "No se encontraron eventos en la colección 'eventos' para el nombre: $nombreEventoAsistido")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ActualizarEventosAsistidos", "Error al buscar eventos en la colección 'eventos' para el nombre: $nombreEventoAsistido", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ActualizarEventosAsistidos", "Error al obtener eventos asistidos", e)
            }
    }



    private fun replaceActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
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

