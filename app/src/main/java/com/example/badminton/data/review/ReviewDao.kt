package com.example.badminton.data.review

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insertReview(review: Review): Long

    @Query("SELECT * FROM reviews WHERE userId = :userId")
    fun getReviewsByUserId(userId: Int): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE bookingId = :bookingId")
    fun getReviewsByBookingId(bookingId: Int): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE courtId = :courtId")
    fun getReviewsByCourtId(courtId: Int): Flow<List<Review>>

    @Delete
    suspend fun deleteReview(review: Review)

    @Update
    suspend fun updateReview(review: Review)
}
