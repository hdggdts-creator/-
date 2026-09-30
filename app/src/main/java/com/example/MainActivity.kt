package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        QuizApp(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizApp(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = {
            if (targetState == Screen.GAME) {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
            } else if (targetState == Screen.RESULTS) {
                scaleIn(initialScale = 0.9f) + fadeIn() togetherWith fadeOut()
            } else {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            }
        },
        label = "ScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            Screen.HOME -> {
                HomeScreen(
                    onSelectEpisode = { episode, twoPlayerMode ->
                        viewModel.startEpisode(episode, twoPlayerMode)
                    }
                )
            }
            Screen.GAME -> {
                GameScreen(viewModel = viewModel)
            }
            Screen.RESULTS -> {
                ResultsScreen(viewModel = viewModel)
            }
        }
    }
}
