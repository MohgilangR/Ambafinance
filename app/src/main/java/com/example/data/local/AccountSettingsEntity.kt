package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_settings")
data class AccountSettingsEntity(
    @PrimaryKey
    val id: Int = SINGLETON_ID,
    val initialBalance: Long = 0L,
    val currencyStyle: String = "IDR_STANDARD",
    val isBalanceHidden: Boolean = false,
    val isCompactList: Boolean = false,
    val showTimeInList: Boolean = true
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
