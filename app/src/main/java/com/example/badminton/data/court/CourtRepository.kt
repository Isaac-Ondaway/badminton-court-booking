package com.example.badminton.data.court

import kotlinx.coroutines.flow.Flow

class CourtRepository(private val courtDao: CourtDao) {

    // Insert a new court
    suspend fun insertCourt(court: Court): Long {
        return courtDao.insert(court)
    }

    // Get all courts
    fun getAllCourts(): Flow<List<Court>> {
        return courtDao.getAllCourts()
    }

    // Get court by ID
    fun getCourtById(courtId: Int): Flow<Court?> {
        return courtDao.getCourtById(courtId)
    }
}
