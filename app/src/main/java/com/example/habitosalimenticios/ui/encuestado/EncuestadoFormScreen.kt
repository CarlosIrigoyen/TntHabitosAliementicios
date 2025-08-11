package com.example.habitosalimenticios.ui.encuestado

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun EncuestadoFormScreen(
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelProvider.AndroidViewModelFactory(application)
    // viewModel() proviene de androidx.lifecycle.viewmodel.compose.viewModel
    val vm: EncuestadoViewModel = viewModel(factory = factory)

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }

    // Recoger Flow de forma segura en Compose
    if (uid != null) {
        val encuestadoFlow: Flow<com.example.habitosalimenticios.data.Encuestado?> =
            remember(uid) { vm.observeEncuestadoFlow(uid) }

        val encuestadoState by encuestadoFlow.collectAsState(initial = null)
        LaunchedEffect(encuestadoState) {
            encuestadoState?.let {
                nombre = it.nombre
                apellido = it.apellido
                calle = it.calle
                numero = it.numero ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Datos del encuestado", modifier = Modifier.padding(bottom = 8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = calle,
            onValueChange = { calle = it },
            label = { Text("Calle") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Número") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (uid == null) {
                    Toast.makeText(context, "No hay usuario logueado. Volvé a iniciar sesión.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (nombre.isBlank() || apellido.isBlank() || calle.isBlank()) {
                    Toast.makeText(context, "Completar nombre, apellido y calle", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                vm.saveEncuestado(uid, nombre, apellido, calle, numero.ifBlank { null }) {
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    onSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Guardar y empezar encuesta")
        }
    }
}

