package com.example.emprende360

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001   // codigo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar firebase
        firebaseAuth = Firebase.auth

        // Instanciar elementos
        val txtEmail: EditText = findViewById(R.id.edtEmail)
        val txtPass: EditText = findViewById(R.id.edtPassword)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val btnGoogleSignIn: Button = findViewById(R.id.btnGoogleSignIn)
        val btnRecordar: TextView = findViewById(R.id.btnOlvidar)
        val btnReturnBack = findViewById<ImageButton>(R.id.retunback1)

        btnReturnBack.setOnClickListener {
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
        }

        // Boton Iniciar Sesion
        btnIngresar.setOnClickListener {
            signIn(txtEmail.text.toString(), txtPass.text.toString())
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
            Toast.makeText(this, "Google Sign-In may not prompt for password on trusted devices.", Toast.LENGTH_LONG).show()
        }

        btnRecordar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraseñaActivity::class.java))
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val verifica = user?.isEmailVerified
                    if (verifica == true) {
                        Toast.makeText(baseContext, "Autentificación exitosa", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PrincipalActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, "No ha verificado su correo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext, "Error en el Email o Contraseña", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Logear con Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account!!)
            } else {
                Toast.makeText(this, "Error en inicio de sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val name = task.result?.user?.displayName

                    // Enviar el nombre del usuario a la FormularioActivity
                    val intent = Intent(this, FormularioActivity::class.java)
                    intent.putExtra("userName", name)
                    startActivity(intent)

                    Toast.makeText(this, "Autenticación exitosa con Google", Toast.LENGTH_SHORT).show()
                    val textViewHola = findViewById<TextView>(R.id.hola)
                    if (textViewHola != null) {
                        textViewHola.text = "hola $name"
                    } else {
                        Log.e("LoginActivity", "El TextView 'hola' no se pudo encontrar.")
                    }

                    Log.d("TAG", "Datos de la tarea: ${task.result}")
                    Log.d("nombre", "Nombre del usuario: $name")
                    Log.d("email", "Email del usuario: ${task.result?.user?.email}")
                    Log.d("email2", "PhotoUrl del usuario: ${task.result?.user?.photoUrl}")
                } else {
                    Log.e("FirebaseAuth", "Error de autenticación con Google: ${task.exception?.message}")
                    Toast.makeText(this, "Error de autenticación con Google", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseAuth", "Error de autenticación con Google: ${exception.message}")
                Toast.makeText(this, "Error de autenticación con Google: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
