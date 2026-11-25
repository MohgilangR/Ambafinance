package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType

enum class CurrencyStyle(
    val displayName: String,
    val samplePreview: String
) {
    IDR_STANDARD("Rupiah Standar", "Rp 1.500.000"),
    IDR_COMPACT("Rupiah Ringkas", "Rp1.500.000"),
    IDR_WITH_CODE("Kode IDR", "IDR 1.500.000"),
    IDR_DECIMAL("Rupiah Desimal", "Rp 1.500.000,00");

    companion object {
        fun fromKey(key: String): CurrencyStyle =
            entries.firstOrNull { it.name == key } ?: IDR_STANDARD
    }
}

data class FinancialSummary(
    val initialBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L
) {
    val finalBalance: Long
        get() = initialBalance + totalIncome - totalExpense
}

enum class HistoryTypeFilter(val label: String) {
    ALL("Semua"),
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran")
}

enum class HistoryPeriodFilter(val label: String) {
    ALL("Semua"),
    TODAY("Hari Ini"),
    LAST_7_DAYS("7 Hari"),
    THIS_MONTH("Bulan Ini"),
    CUSTOM("Pilih Tanggal")
}

data class TransactionGroup(
    val dateHeader: String,
    val dateMillis: Long,
    val items: List<TransactionEntity>
)

data class CategoryItem(
    val name: String,
    val icon: ImageVector
)

object TransactionCategories {
    val expenseCategories = listOf(
        CategoryItem("Makan", Icons.Rounded.Fastfood),
        CategoryItem("Minuman", Icons.Rounded.LocalCafe),
        CategoryItem("Transportasi", Icons.Rounded.DirectionsCar),
        CategoryItem("Belanja", Icons.Rounded.ShoppingBag),
        CategoryItem("Jajan", Icons.Rounded.Storefront),
        CategoryItem("Tagihan", Icons.Rounded.ReceiptLong),
        CategoryItem("Kesehatan", Icons.Rounded.Favorite),
        CategoryItem("Pendidikan", Icons.Rounded.School),
        CategoryItem("Lainnya", Icons.Rounded.MoreHoriz)
    )

    val incomeCategories = listOf(
        CategoryItem("Gaji", Icons.Rounded.AccountBalanceWallet),
        CategoryItem("Freelance", Icons.Rounded.LaptopMac),
        CategoryItem("Bonus", Icons.Rounded.Bolt),
        CategoryItem("Investasi", Icons.Rounded.TrendingUp),
        CategoryItem("Penjualan", Icons.Rounded.AttachMoney),
        CategoryItem("Hadiah", Icons.Rounded.CardGiftcard),
        CategoryItem("Lainnya", Icons.Rounded.MoreHoriz)
    )

    val quickExpenseSuggestions = listOf(
        "Makan",
        "Beli minuman",
        "Transportasi",
        "Beli barang",
        "Jajan"
    )

    val quickIncomeSuggestions = listOf(
        "Gaji Bulanan",
        "Bonus",
        "Freelance",
        "Uang Tambahan"
    )

    fun iconFor(category: String, type: TransactionType): ImageVector {
        val list = if (type == TransactionType.INCOME) incomeCategories else expenseCategories
        return list.firstOrNull { it.name.equals(category, ignoreCase = true) }?.icon
            ?: if (type == TransactionType.INCOME) Icons.Rounded.TrendingUp else Icons.Rounded.ReceiptLong
    }
}
