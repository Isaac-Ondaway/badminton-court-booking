package com.example.badminton.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.booking.BookingDao
import com.example.badminton.data.booking.BookingCourt
import com.example.badminton.data.booking.BookingCourtDao
import com.example.badminton.data.court.Court
import com.example.badminton.data.court.CourtDao
import com.example.badminton.data.review.Review
import com.example.badminton.data.review.ReviewDao
import com.example.badminton.data.user.User
import com.example.badminton.data.user.UserDao
import java.util.Date

@Database(
    entities = [User::class, Court::class, Booking::class, BookingCourt::class, Review::class],
    version = 9, // Increment the database version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun bookingDao(): BookingDao
    abstract fun bookingCourtDao(): BookingCourtDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `reviews` (
                `reviewId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `userId` INTEGER NOT NULL,
                `bookingId` INTEGER,
                `courtId` INTEGER,
                `rating` INTEGER NOT NULL,
                `comment` TEXT NOT NULL,
                `reviewDate` INTEGER NOT NULL,
                FOREIGN KEY(`userId`) REFERENCES `users`(`userID`) ON DELETE CASCADE,
                FOREIGN KEY(`bookingId`) REFERENCES `bookings`(`bookingId`) ON DELETE CASCADE,
                FOREIGN KEY(`courtId`) REFERENCES `courts`(`courtId`) ON DELETE CASCADE
            )
        """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_bookingId` ON `reviews` (`bookingId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_courtId` ON `reviews` (`courtId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_userId` ON `reviews` (`userId`)")
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_8_9)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}



