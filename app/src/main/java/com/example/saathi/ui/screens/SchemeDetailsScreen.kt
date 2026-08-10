package com.example.saathi.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.saathi.domain.model.Scheme
import com.example.saathi.ui.components.EligibilityBadge
import com.example.saathi.ui.theme.CalmTeal
import com.example.saathi.ui.theme.Saffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeDetailsScreen(
    schemeId: String,
    viewModel: SchemeDetailsViewModel,
    onBack: () -> Unit
) {
    val scheme by viewModel.scheme.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(schemeId) {
        viewModel.loadScheme(schemeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scheme?.name ?: "Scheme Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        scheme?.let { s ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = s.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                EligibilityBadge(s.eligibilityStatus)
                
                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle("Why SAATHI recommends this?")
                Text(s.whyThis, style = MaterialTheme.typography.bodyLarge)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                SectionTitle("Benefits")
                Text(s.benefits, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Eligibility Criteria")
                Text(s.eligibilityCriteria, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Required Documents")
                s.documents.forEach { doc ->
                    Text("• $doc", style = MaterialTheme.typography.bodyMedium)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Application Steps")
                s.steps.forEachIndexed { index, step ->
                    Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle("Official Source")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = CalmTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(s.sourceOrganization, style = MaterialTheme.typography.bodyMedium)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { 
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(s.sourceUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CalmTeal)
                ) {
                    Text("Visit Official Website")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Last verified: ${s.lastVerified}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Saffron,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
