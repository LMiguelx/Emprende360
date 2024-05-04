package com.example.emprende360

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        firebaseAuth = Firebase.auth
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.menu_buscar ->{
                Toast.makeText(baseContext, "Buscar información", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_salir ->{
                signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        return
    }

    private fun signOut(){
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
    }
}