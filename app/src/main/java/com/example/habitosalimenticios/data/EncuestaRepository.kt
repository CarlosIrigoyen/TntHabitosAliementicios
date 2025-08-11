package com.example.habitosalimenticios.data

import kotlinx.coroutines.flow.Flow

class EncuestaRepository(private val db: AppDatabase) {
    private val dao = db.encuestadoDao()

    suspend fun upsertEncuestado(e: Encuestado) = dao.upsert(e)
    suspend fun getEncuestadoOnce(uid: String): Encuestado? = dao.getByFirebaseUidOnce(uid)
    fun observeEncuestado(uid: String): Flow<Encuestado?> = dao.getByFirebaseUidFlow(uid)
}
