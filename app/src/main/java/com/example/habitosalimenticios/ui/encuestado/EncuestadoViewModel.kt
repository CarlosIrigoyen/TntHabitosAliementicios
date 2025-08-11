package com.example.habitosalimenticios.ui.encuestado

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitosalimenticios.data.AppDatabase
import com.example.habitosalimenticios.data.Encuestado
import com.example.habitosalimenticios.data.EncuestaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EncuestadoViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = EncuestaRepository(AppDatabase.getInstance(application))

    fun observeEncuestadoFlow(uid: String): Flow<Encuestado?> = repo.observeEncuestado(uid)

    fun saveEncuestado(uid: String, nombre: String, apellido: String, calle: String, numero: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val e = Encuestado(
                firebaseUid = uid,
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                calle = calle.trim(),
                numero = numero?.trim()
            )
            repo.upsertEncuestado(e)
            onDone()
        }
    }
}
