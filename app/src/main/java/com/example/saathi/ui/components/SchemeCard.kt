package com.example.saathi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saathi.domain.model.EligibilityStatus
import com.example.saathi.domain.model.Scheme
import com.example.saathi.ui.theme.CalmTeal
import com.example.saathi.ui.theme.Saffron

@Composable
fun SchemeCard(
    scheme: Scheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = scheme.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = Saffron.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${(scheme.relevance * 100).toInt()}% Match",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Saffron
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            EligibilityBadge(scheme.eligibilityStatus)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = scheme.benefits,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CalmTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap for eligibility and documents",
                    style = MaterialTheme.typography.labelMedium,
                    color = CalmTeal
                )
            }
        }
    }
}

@Composable
fun EligibilityBadge(status: EligibilityStatus) {
    val (text, color, icon) = when (status) {
        EligibilityStatus.LIKELY_ELIGIBLE -> Triple("Likely Eligible", CalmTeal, Icons.Default.CheckCircle)
        EligibilityStatus.MAY_BE_ELIGIBLE -> Triple("May be Eligible", Saffron, Icons.Default.Info)
        EligibilityStatus.NEEDS_MORE_INFORMATION -> Triple("Need More Info", Color.Gray, Icons.Default.Info)
        EligibilityStatus.NOT_ELIGIBLE -> Triple("Not Eligible", Color.Red, Icons.Default.Warning)
        EligibilityStatus.UNKNOWN -> Triple("Unknown", Color.Gray, Icons.Default.Info)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
