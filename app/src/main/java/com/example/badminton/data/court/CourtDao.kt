package com.example.badminton.data.court

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtDao {
    @Insert
    suspend fun insert(court: Court): Long

    @Query("SELECT * FROM courts")
    fun getAllCourts(): Flow<List<Court>>

    @Query("SELECT * FROM courts WHERE courtId = :courtId")
    fun getCourtById(courtId: Int): Flow<Court?>


}
