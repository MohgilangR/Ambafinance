package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TransactionEntity::class, AccountSettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TransactionConverters::class)
abstract class MonochromeDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountSettingsDao(): AccountSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: MonochromeDatabase? = null

        fun getInstance(context: Context): MonochromeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonochromeDatabase::class.java,
                    "monochrome_finance.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
