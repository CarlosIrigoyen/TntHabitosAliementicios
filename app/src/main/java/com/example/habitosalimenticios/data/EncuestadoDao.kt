package com.example.habitosalimenticios.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EncuestadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(encuestado: Encuestado)

    @Query("SELECT * FROM encuestado WHERE firebaseUid = :uid LIMIT 1")
    suspend fun getByFirebaseUidOnce(uid: String): Encuestado?

    @Query("SELECT * FROM encuestado WHERE firebaseUid = :uid LIMIT 1")
    fun getByFirebaseUidFlow(uid: String): Flow<Encuestado?>
}
