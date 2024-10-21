package com.example.minhaprimeiraapi.database

import android.content.Context
import androidx.room.Room
import java.lang.Exception

object DatabaseBuilder {

    private var instance: AppDatabase? = null

    /**
     * metodo responsável por certificar que teremos uma estrutura
     * singleton para o database
     */
    fun getInstance(context: Context? = null): AppDatabase {
        return instance ?: synchronized(this) {
            if (context == null) {
                throw Exception("DatabaseBuilder.getInstance(context) deve ser inicializado antes de ser utilizado")
            }
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .addMigrations(DatabaseMigrations.MIGRATION_1_TO_2)
                .build()
            instance = newInstance
            newInstance
        }
    }
}