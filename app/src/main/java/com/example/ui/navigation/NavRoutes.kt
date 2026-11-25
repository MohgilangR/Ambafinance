package com.example.ui.navigation

object NavRoutes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ADD_INCOME = "add_income"
    const val ADD_EXPENSE = "add_expense"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"

    fun editTransaction(transactionId: Long): String = "edit_transaction/$transactionId"
}
