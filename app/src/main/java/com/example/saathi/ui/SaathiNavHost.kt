package com.example.saathi.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saathi.data.local.QdrantMemoryService
import com.example.saathi.data.repository.*
import com.example.saathi.domain.AssistantBrain
import com.example.saathi.network.GroqService
import com.example.saathi.network.NetworkModule
import com.example.saathi.ui.screens.*
import com.example.saathi.voice.*

@Composable
fun SaathiNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Manual DI
    val androidTts = remember { AndroidTtsService(context) }
    val rimeTts = remember { RimeTtsService(context) }
    val ttsService = remember { HybridTtsService(rimeTts, androidTts) }
    
    val voiceService = remember { SaathiVoiceService(context, ttsService, scope) }
    val groqService = remember { GroqService() }
    val qdrantMemory = remember { QdrantMemoryService() }
    
    val mockSchemeRepository = remember { MockSchemeRepository() }
    val remoteSchemeRepository = remember { RemoteSchemeRepository(NetworkModule.api) }
    val schemeRepository = remember { HybridSchemeRepository(remoteSchemeRepository, mockSchemeRepository) }
    
    val userRepository = remember { MockUserRepository() }
    val historyRepository = remember { MockHistoryRepository() }
    
    val assistantBrain = remember { AssistantBrain(schemeRepository, userRepository, groqService, qdrantMemory) }

    val homeViewModel = remember { HomeViewModel(voiceService, assistantBrain, historyRepository, ttsService) }
    val conversationViewModel = remember { ConversationViewModel(historyRepository, assistantBrain) }
    val schemeResultsViewModel = remember { SchemeResultsViewModel(schemeRepository, assistantBrain) }
    val schemeDetailsViewModel = remember { SchemeDetailsViewModel(schemeRepository) }
    val profileViewModel = remember { ProfileViewModel(userRepository) }

    DisposableEffect(Unit) {
        onDispose {
            ttsService.shutdown()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToConversation = { navController.navigate(Screen.Conversation.route) }
            )
        }
        
        composable(Screen.Conversation.route) {
            ConversationScreen(
                viewModel = conversationViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToResults = { navController.navigate(Screen.SchemeResults.route) }
            )
        }
        
        composable(Screen.SchemeResults.route) {
            LaunchedEffect(Unit) {
                schemeResultsViewModel.loadSchemes("farming") 
            }
            SchemeResultsScreen(
                viewModel = schemeResultsViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> 
                    navController.navigate(Screen.SchemeDetails.createRoute(id)) 
                }
            )
        }
        
        composable(Screen.SchemeDetails.route) { backStackEntry ->
            val schemeId = backStackEntry.arguments?.getString("schemeId") ?: ""
            SchemeDetailsScreen(
                schemeId = schemeId,
                viewModel = schemeDetailsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = conversationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) }
            )
        }
        
        composable(Screen.Privacy.route) {
            PrivacyScreen(
                profileViewModel = profileViewModel,
                conversationViewModel = conversationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
