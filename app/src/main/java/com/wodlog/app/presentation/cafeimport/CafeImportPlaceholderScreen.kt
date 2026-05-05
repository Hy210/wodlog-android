package com.wodlog.app.presentation.cafeimport

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wodlog.app.domain.model.CafeSource

@Composable
fun CafeImportPlaceholderScreen(
    cafeSource: CafeSource?,
    cafeSourceId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CafeImportScreen(
        cafeSource = cafeSource,
        cafeSourceId = cafeSourceId,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
