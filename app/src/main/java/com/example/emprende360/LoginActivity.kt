package com.example.emprende360


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

        // Instanciar elementos xD
        val txtEmail: EditText = findViewById(R.id.edtEmail)
        val txtPass: EditText = findViewById(R.id.edtPassword)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val btnGoogleSignIn: Button = findViewById(R.id.btnGoogleSignIn)
        val btnCrearCuenta: TextView = findViewById(R.id.btnCrearCuenta)
        val btnRecordar: TextView = findViewById(R.id.btnOlvidar)

        // Boton Iniciar Sesion
        btnIngresar.setOnClickListener {
            signIn(txtEmail.text.toString(), txtPass.text.toString())
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
            Toast.makeText(this, "Google Sign-In may not prompt for password on trusted devices.", Toast.LENGTH_LONG).show()
        }

        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, CrearCuentaActivity::class.java))
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
    // Logear gogle
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
                    Toast.makeText(this, "Autentificación exitosa con Google", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PrincipalActivity::class.java))
                    Log.d("TAG", "Datos de la tarea: ${task.getResult()}")
                    Log.d("User", "ID del usuario: ${task.getResult().user?.uid}") // me da datos del id
                    Log.d("nombre", "ID del usuario: ${task.getResult().user?.displayName}") // me da datos  de nombre del correo
                    Log.d("numero", "ID del usuario: ${task.getResult().user?.phoneNumber}") // me da datos de numero ( aunque es nulo )
                    Log.d("email", "ID del usuario: ${task.getResult().user?.email}") // me da datos del nombre del correo
                    Log.d("email2", "ID del usuario: ${task.getResult().user?.photoUrl}") // me da datos de la photoUrl
                } else {
                    Toast.makeText(this, "Error de autenticación con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
