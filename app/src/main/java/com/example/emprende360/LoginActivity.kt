package com.example.emprende360

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    private val RC_SIGN_IN = 1001   // Código de solicitud de inicio de sesión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = Firebase.auth

        // Configurar botones y campos de texto
        val txtEmail: EditText = findViewById(R.id.edtEmail)
        val txtPass: EditText = findViewById(R.id.edtPassword)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val btnGoogleSignIn: Button = findViewById(R.id.btnGoogleSignIn)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val btnRecordar: TextView = findViewById(R.id.btnOlvidar)

        // Inicializar cliente de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Botón para registrar nueva cuenta
        btnRegister.setOnClickListener {
            val intent2 = Intent(this, CrearCuentaActivity::class.java)
            startActivity(intent2)
        }

        // Botón para iniciar sesión con email y contraseña
        btnIngresar.setOnClickListener {
            signIn(txtEmail.text.toString(), txtPass.text.toString())
        }

        // Botón para iniciar sesión con Google
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
            Toast.makeText(this, "Google Sign-In may not prompt for password on trusted devices.", Toast.LENGTH_LONG).show()
        }

        // Botón para recuperar contraseña
        btnRecordar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraseñaActivity::class.java))
        }
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid
                    userId?.let {
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userId", it)
                        editor.apply()

                        // Iniciar la actividad del formulario
                        startActivity(Intent(this, FormularioActivity::class.java))
                    }
                } else {
                    // Manejar errores de autenticación
                }
            }
    }

    private fun signInWithGoogle() {
        // Iniciar sesión con Google
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
                    val googleId = user?.uid  // Obtener el ID de Google del usuario

                    // Guardar el ID de Google en SharedPreferences o enviarlo a la actividad del formulario
                    // Aquí puedes guardar el ID de Google en SharedPreferences para usarlo más tarde
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("googleId", googleId)
                    editor.apply()

                    // Iniciar la actividad del formulario
                    startActivity(Intent(this, FormularioActivity::class.java))
                } else {
                    // Manejar errores de autenticación
                }
            }
    }
}
