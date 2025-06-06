package com.example.mangiaebasta.modal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_table WHERE mid = :mid")
    suspend fun getMenuByIdfromDb(mid: Int): MenuEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenu(menu: MenuEntity)
}