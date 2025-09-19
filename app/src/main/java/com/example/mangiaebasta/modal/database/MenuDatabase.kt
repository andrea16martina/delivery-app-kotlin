package com.example.mangiaebasta.modal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MenuEntity::class, OrderEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: MenuDatabase? = null

        fun getDatabase(context: Context): MenuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenuDatabase::class.java,
                    "menu_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}