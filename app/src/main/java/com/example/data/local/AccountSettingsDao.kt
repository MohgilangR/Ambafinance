package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountSettingsDao {
    @Query("SELECT * FROM account_settings WHERE id = 1")
    fun observeSettings(): Flow<AccountSettingsEntity?>

    @Query("SELECT * FROM account_settings WHERE id = 1")
    suspend fun getSettingsOnce(): AccountSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: AccountSettingsEntity)
}
