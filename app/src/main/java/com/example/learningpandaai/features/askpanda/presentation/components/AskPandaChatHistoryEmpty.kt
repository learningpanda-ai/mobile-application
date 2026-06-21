package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.R
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.PureWhite

@Composable
fun AskPandaChatHistoryEmpty(
    emptyTitle: String,
    emptySubtitle: String,
    newSessionLabel: String,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_panda_logo),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = emptyTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = emptySubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onNewSession,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = newSessionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = PureWhite,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
