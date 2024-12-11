package com.example.badminton.data.review

import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {

    suspend fun insertReview(review: Review): Long {
        return reviewDao.insertReview(review)
    }

    fun getReviewsByUserId(userId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsByUserId(userId)
    }

    fun getReviewsByBookingId(bookingId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsByBookingId(bookingId)
    }

    fun getReviewsByCourtId(courtId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsByCourtId(courtId)
    }

    suspend fun deleteReview(review: Review) {
        reviewDao.deleteReview(review)
    }

    suspend fun updateReview(review: Review) {
        reviewDao.updateReview(review)
    }
}
