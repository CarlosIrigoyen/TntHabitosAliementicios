package com.example.habitosalimenticios.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "encuestado",
    indices = [Index(value = ["firebaseUid"], unique = true)]
)
data class Encuestado(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val firebaseUid: String,
    val nombre: String,
    val apellido: String,
    val calle: String,
    val numero: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
