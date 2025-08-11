package com.example.habitosalimenticios

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitosalimenticios.ui.LoginScreen
import com.example.habitosalimenticios.ui.encuestado.EncuestadoFormScreen
import com.example.habitosalimenticios.ui.encuestado.EncuestadoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    // opcional: inicializamos aquí
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inicializar auth
        auth = Firebase.auth

        setContent {
            val context = LocalContext.current
            // estado del usuario actual (null si no hay sesión)
            val firebaseUser = remember { mutableStateOf<FirebaseUser?>(Firebase.auth.currentUser) }

            // Listener de Auth que actualiza el state de firebaseUser
            DisposableEffect(Unit) {
                val listener = FirebaseAuth.AuthStateListener { a ->
                    firebaseUser.value = a.currentUser
                }
                Firebase.auth.addAuthStateListener(listener)
                onDispose {
                    Firebase.auth.removeAuthStateListener(listener)
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Si hay usuario logueado, comprobamos si existe el Encuestado en BD
                    if (firebaseUser.value != null) {
                        val uid = firebaseUser.value!!.uid

                        // Creamos el ViewModel (AndroidViewModel) con factory
                        val application = LocalContext.current.applicationContext as Application
                        val encuestadoVm: EncuestadoViewModel = viewModel(
                            key = "encuestado_vm_$uid",
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    @Suppress("UNCHECKED_CAST")
                                    return EncuestadoViewModel(application) as T
                                }
                            }
                        )

                        // Observamos el Flow<Encuestado?>; si es null -> mostrar formulario
                        val encuesta by encuestadoVm.observeEncuestadoFlow(uid).collectAsState(initial = null)

                        if (encuesta == null) {
                            // Mostrar formulario para completar datos del encuestado
                            EncuestadoFormScreen(
                                onSaved = {
                                    // El propio ViewModel en EncuestadoFormScreen guarda los datos.
                                    // Aquí solo mostramos un toast opcional; la observación del Flow
                                    // hará que la UI pase automáticamente a WelcomeScreen.
                                    Toast.makeText(context, "Guardando datos...", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            // Si ya existe, mostramos la pantalla de bienvenida
                            WelcomeScreen(
                                email = firebaseUser.value?.email ?: "Usuario",
                                onSignOut = {
                                    Firebase.auth.signOut()
                                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    } else {
                        // Si no hay usuario logueado, mostramos pantalla de login
                        LoginScreen(
                            onLoginSuccess = { userEmail ->
                                // No necesitamos navegar manualmente: el AuthStateListener actualizará firebaseUser
                                Toast.makeText(context, "Logueado como $userEmail", Toast.LENGTH_SHORT).show()
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(email: String, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido, $email 👋", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSignOut) {
            Text("Cerrar sesión")
        }
    }
}
