package com.wodlog.app.presentation.woddetail

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
fun WodDetailScreen(
    onEditResultClick: () -> Unit = {},
    onPromptClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-wod-detail")
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WOD Detail",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Phase 3 placeholder",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onEditResultClick,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("action-edit-result")
        ) {
            Text("결과 입력")
        }
        Button(
            onClick = onPromptClick,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("action-open-prompt")
        ) {
            Text("질문지")
        }
        Button(
            onClick = onReportClick,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("action-open-report")
        ) {
            Text("GPT 답변")
        }
    }
}
