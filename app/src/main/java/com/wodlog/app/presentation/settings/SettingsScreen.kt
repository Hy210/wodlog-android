package com.wodlog.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onProfileClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-settings")
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Phase 0 placeholder",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onProfileClick,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("action-open-profile")
        ) {
            Text("프로필")
        }
        Button(
            onClick = onLifestyleClick,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("action-open-lifestyle")
        ) {
            Text("생활습관")
        }
    }
}
