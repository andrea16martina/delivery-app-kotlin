package com.example.mangiaebasta.modal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_table WHERE oid = :oid")
    fun getOrderbyIdfromDb(oid: Int): OrderEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: OrderEntity)
}