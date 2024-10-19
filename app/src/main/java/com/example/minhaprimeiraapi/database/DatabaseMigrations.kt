package com.example.minhaprimeiraapi.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_TO_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Criar uma nova tabela com a nova estrutura
            database.execSQL("""
            CREATE TABLE new_location_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                createdAt INTEGER NOT NULL DEFAULT (strftime('%s','now')) 
            )
        """.trimIndent())

            // Copiar os dados da tabela antiga para a nova tabela
            database.execSQL("""
            INSERT INTO new_location_table (id, latitude, longitude)
            SELECT id, latitude, longitude FROM user_location_table
        """.trimIndent())

            // Remover a tabela antiga
            database.execSQL("DROP TABLE user_location_table")

            // Renomear a nova tabela para o nome da tabela original
            database.execSQL("ALTER TABLE new_location_table RENAME TO user_location_table")
        }
    }
}