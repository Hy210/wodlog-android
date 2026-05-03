package com.wodlog.app.presentation.calendar

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
fun CalendarScreen(
    onCreateWodClick: () -> Unit = {},
    onOpenWodClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-calendar")
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "캘린더",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Phase 0 placeholder",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onCreateWodClick,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("action-calendar-create-wod")
        ) {
            Text("선택한 날짜에 WOD 작성")
        }
        Button(
            onClick = onOpenWodClick,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("action-calendar-open-wod")
        ) {
            Text("WOD 상세 보기")
        }
    }
}
