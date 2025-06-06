package com.example.mangiaebasta.modal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mangiaebasta.modal.Location

@Entity(tableName = "order_table")
class OrderEntity (
    @PrimaryKey val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    val currentPosition: Location,
    val deliveryTimestamp: String? = null,
    val expectedDeliveryTimestamp : String? = null
)