package com.example

import android.app.Application
import com.example.data.local.MonochromeDatabase
import com.example.data.repository.FinanceRepository

class MonochromeApp : Application() {
    val database: MonochromeDatabase by lazy {
        MonochromeDatabase.getInstance(this)
    }

    val repository: FinanceRepository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            settingsDao = database.accountSettingsDao()
        )
    }
}
