package com.example.minhaprimeiraapi.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.minhaprimeiraapi.database.model.UserLocation

@Dao
interface UserLocationDao {

    @Insert
    suspend fun insert(userLocation: UserLocation)

    @Query("SELECT * FROM user_location_table")
    suspend fun getAllUserLocations(): List<UserLocation>

    @Query("SELECT * FROM user_location_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastLocation(): UserLocation?
}