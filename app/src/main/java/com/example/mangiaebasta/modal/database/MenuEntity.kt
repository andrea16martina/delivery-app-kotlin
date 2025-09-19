package com.example.mangiaebasta.modal.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_table")
data class MenuEntity(
    @PrimaryKey val mid: Int,
    val imageBase64: String,
    val imageVersion: Int
)