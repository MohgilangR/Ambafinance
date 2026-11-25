package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.MonochromeFinanceApp
import com.example.ui.theme.MonochromeFinanceTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val app = application as MonochromeApp
        val viewModelFactory = FinanceViewModelFactory(app.repository)

        setContent {
            MonochromeFinanceTheme {
                val financeViewModel: FinanceViewModel = viewModel(factory = viewModelFactory)
                MonochromeFinanceApp(viewModel = financeViewModel)
            }
        }
    }
}
