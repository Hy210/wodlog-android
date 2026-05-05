package com.wodlog.app.presentation.cafeimport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogSecondaryButton

@Composable
fun CafeImportPlaceholderScreen(
    cafeSource: CafeSource?,
    cafeSourceId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-cafe-import-placeholder")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD 불러오기",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "다음 단계에서 네이버카페 화면을 연결합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        WodLogCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cafe-import-selected-source"),
            title = cafeSource?.boxName ?: "선택한 카페 소스",
            subtitle = cafeSource?.boardUrl ?: "CafeSource id: $cafeSourceId"
        ) {
            Text(
                text = "이번 단계에서는 가져오기 진입점만 확인합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            cafeSource?.let {
                Text(
                    text = "제목 키워드: ${it.titleKeywords.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        WodLogSecondaryButton(
            text = "뒤로가기",
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-back-from-cafe-import-placeholder")
        )
    }
}
