package com.example.saathi.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.saathi.ui.components.MicrophoneButton
import com.example.saathi.ui.components.VoiceOrb
import com.example.saathi.ui.components.VoiceState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToConversation: () -> Unit
) {
    val voiceState by viewModel.voiceState.collectAsState()
    val greeting by viewModel.greeting.collectAsState()
    val recordingDuration by viewModel.recordingDuration.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.navigateToResults.collect { query ->
            // In a real app, this would trigger navigation
            // For now we rely on the manual button or voice flow finishing
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onMicClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SAATHI", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = greeting,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "greetingAnim"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val statusText = when (voiceState) {
                VoiceState.IDLE -> "Bol kar poochiye..."
                VoiceState.LISTENING -> "Sun raha hoon..."
                VoiceState.THINKING -> "Samajh raha hoon..."
                VoiceState.SPEAKING -> "SAATHI bol raha hai..."
                VoiceState.ERROR -> "Kuch galat ho gaya. Phir se koshish karein."
            }

            AnimatedVisibility(visible = true) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    if (voiceState == VoiceState.LISTENING) {
                        Text(
                            text = recordingDuration,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            VoiceOrb(state = voiceState)

            Spacer(modifier = Modifier.height(48.dp))

            MicrophoneButton(
                isListening = voiceState == VoiceState.LISTENING,
                onClick = { 
                    if (voiceState == VoiceState.IDLE) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        viewModel.onMicClick()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AnimatedVisibility(visible = voiceState == VoiceState.IDLE) {
                TextButton(onClick = onNavigateToConversation) {
                    Text("Pichli baatcheet dekhein")
                }
            }
            
            AnimatedVisibility(visible = voiceState == VoiceState.SPEAKING) {
                Button(
                    onClick = { 
                        viewModel.onMicClick() 
                        onNavigateToConversation() 
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Baatcheet dekhein")
                }
            }
        }
    }
}
