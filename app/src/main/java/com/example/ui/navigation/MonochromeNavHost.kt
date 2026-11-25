package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.ui.components.IosBottomNavBar
import com.example.ui.components.MonochromeNavigationRail
import com.example.ui.components.TransactionDetailSheet
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpenseFormScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.IncomeAndBalanceScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

@Composable
fun MonochromeFinanceApp(
    viewModel: FinanceViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val dashboardState by viewModel.dashboardUiState.collectAsStateWithLifecycle()
    val historyState by viewModel.historyUiState.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val isMainTabRoute = currentRoute in setOf(NavRoutes.HOME, NavRoutes.HISTORY, NavRoutes.SETTINGS)

    val navigateToMainTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MonochromeBackground)
    ) {
        val useNavigationRail = maxWidth >= 720.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail && isMainTabRoute) {
                MonochromeNavigationRail(
                    currentRoute = currentRoute,
                    onNavigate = navigateToMainTab
                )
            }

            Scaffold(
                containerColor = MonochromeBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = MonochromeSurfaceSecondary,
                            contentColor = MonochromeTextPrimary,
                            actionColor = MonochromeAccentBright
                        )
                    }
                },
                bottomBar = {
                    if (!useNavigationRail && isMainTabRoute) {
                        IosBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = navigateToMainTab
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.HOME,
                        enterTransition = {
                            fadeIn(animationSpec = tween(220))
                        },
                        exitTransition = {
                            fadeOut(animationSpec = tween(180))
                        }
                    ) {
                        composable(NavRoutes.HOME) {
                            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                                DashboardScreen(
                                    uiState = dashboardState,
                                    onToggleBalanceVisibility = { viewModel.toggleBalanceVisibility() },
                                    onUpdateInitialBalance = { newInitial ->
                                        viewModel.updateInitialBalance(newInitial) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Saldo awal diperbarui")
                                            }
                                        }
                                    },
                                    onNavigateToAddIncome = {
                                        navController.navigate(NavRoutes.ADD_INCOME)
                                    },
                                    onNavigateToAddExpense = {
                                        navController.navigate(NavRoutes.ADD_EXPENSE)
                                    },
                                    onNavigateToHistory = {
                                        navigateToMainTab(NavRoutes.HISTORY)
                                    },
                                    onTransactionClick = { tx ->
                                        selectedTransaction = tx
                                    }
                                )
                            }
                        }

                        composable(NavRoutes.HISTORY) {
                            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                                HistoryScreen(
                                    uiState = historyState,
                                    onSearchQueryChange = viewModel::setSearchQuery,
                                    onTypeFilterChange = viewModel::setTypeFilter,
                                    onPeriodFilterChange = viewModel::setPeriodFilter,
                                    onCustomDateRangeSelected = viewModel::setCustomDateRange,
                                    onResetFilters = viewModel::resetHistoryFilters,
                                    onTransactionClick = { tx ->
                                        selectedTransaction = tx
                                    }
                                )
                            }
                        }

                        composable(NavRoutes.SETTINGS) {
                            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                                SettingsScreen(
                                    uiState = settingsState,
                                    onUpdateInitialBalance = { newInitial ->
                                        viewModel.updateInitialBalance(newInitial) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Saldo awal diperbarui")
                                            }
                                        }
                                    },
                                    onUpdateCurrencyStyle = viewModel::updateCurrencyStyle,
                                    onSetBalanceHidden = viewModel::setBalanceHidden,
                                    onSetCompactList = viewModel::setCompactList,
                                    onSetShowTimeInList = viewModel::setShowTimeInList,
                                    onClearAllData = {
                                        viewModel.clearAllData {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Semua data transaksi telah dihapus")
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        composable(
                            route = NavRoutes.ADD_INCOME,
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    tween(280)
                                ) + fadeIn(tween(220))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    tween(250)
                                ) + fadeOut(tween(180))
                            }
                        ) {
                            IncomeAndBalanceScreen(
                                summary = dashboardState.summary,
                                currencyStyle = dashboardState.currencyStyle,
                                onSaveIncome = { title, amount, category, note, timestamp ->
                                    viewModel.saveTransaction(
                                        title = title,
                                        amount = amount,
                                        type = TransactionType.INCOME,
                                        category = category,
                                        note = note,
                                        timestamp = timestamp
                                    ) {
                                        navController.popBackStack()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Pemasukan berhasil disimpan")
                                        }
                                    }
                                },
                                onSaveInitialBalance = { newBalance ->
                                    viewModel.updateInitialBalance(newBalance) {
                                        navController.popBackStack()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Saldo awal disimpan")
                                        }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = NavRoutes.ADD_EXPENSE,
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    tween(280)
                                ) + fadeIn(tween(220))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    tween(250)
                                ) + fadeOut(tween(180))
                            }
                        ) {
                            ExpenseFormScreen(
                                availableBalance = dashboardState.summary.finalBalance,
                                currencyStyle = dashboardState.currencyStyle,
                                onSaveExpense = { title, amount, category, note, timestamp ->
                                    viewModel.saveTransaction(
                                        title = title,
                                        amount = amount,
                                        type = TransactionType.EXPENSE,
                                        category = category,
                                        note = note,
                                        timestamp = timestamp
                                    ) {
                                        navController.popBackStack()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Pengeluaran berhasil dicatat")
                                        }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = NavRoutes.EDIT_TRANSACTION,
                            arguments = listOf(
                                navArgument("transactionId") { type = NavType.LongType }
                            ),
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Start,
                                    tween(260)
                                ) + fadeIn(tween(200))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.End,
                                    tween(240)
                                ) + fadeOut(tween(180))
                            }
                        ) { entry ->
                            val transactionId = entry.arguments?.getLong("transactionId") ?: 0L
                            val transaction by viewModel.getTransactionById(transactionId)
                                .collectAsStateWithLifecycle(initialValue = null)

                            val currentTx = transaction
                            if (currentTx != null) {
                                if (currentTx.type == TransactionType.INCOME) {
                                    IncomeAndBalanceScreen(
                                        summary = dashboardState.summary,
                                        currencyStyle = dashboardState.currencyStyle,
                                        editingTransaction = currentTx,
                                        onSaveIncome = { title, amount, category, note, timestamp ->
                                            viewModel.saveTransaction(
                                                existingId = currentTx.id,
                                                title = title,
                                                amount = amount,
                                                type = TransactionType.INCOME,
                                                category = category,
                                                note = note,
                                                timestamp = timestamp,
                                                createdAt = currentTx.createdAt
                                            ) {
                                                navController.popBackStack()
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Transaksi diperbarui")
                                                }
                                            }
                                        },
                                        onSaveInitialBalance = {},
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } else {
                                    ExpenseFormScreen(
                                        availableBalance = dashboardState.summary.finalBalance,
                                        currencyStyle = dashboardState.currencyStyle,
                                        editingTransaction = currentTx,
                                        onSaveExpense = { title, amount, category, note, timestamp ->
                                            viewModel.saveTransaction(
                                                existingId = currentTx.id,
                                                title = title,
                                                amount = amount,
                                                type = TransactionType.EXPENSE,
                                                category = category,
                                                note = note,
                                                timestamp = timestamp,
                                                createdAt = currentTx.createdAt
                                            ) {
                                                navController.popBackStack()
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Transaksi diperbarui")
                                                }
                                            }
                                        },
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedTransaction?.let { tx ->
        TransactionDetailSheet(
            transaction = tx,
            currencyStyle = dashboardState.currencyStyle,
            onEdit = {
                selectedTransaction = null
                navController.navigate(NavRoutes.editTransaction(tx.id))
            },
            onDelete = {
                selectedTransaction = null
                viewModel.deleteTransaction(tx.id) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Transaksi dihapus")
                    }
                }
            },
            onDismiss = { selectedTransaction = null }
        )
    }
}
