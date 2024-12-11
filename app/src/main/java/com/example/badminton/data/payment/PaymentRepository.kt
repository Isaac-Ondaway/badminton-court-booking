package com.example.badminton.data.payment

class PaymentRepository(private val paymentDao: PaymentDao) {

    suspend fun insertPayment(payment: Payment) = paymentDao.insertPayment(payment)

    suspend fun getPaymentsForBooking(bookingId: Int) = paymentDao.getPaymentsForBooking(bookingId)

}