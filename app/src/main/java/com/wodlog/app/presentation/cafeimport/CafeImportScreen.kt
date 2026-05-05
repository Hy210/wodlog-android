package com.wodlog.app.presentation.cafeimport

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton

@Composable
fun CafeImportScreen(
    cafeSource: CafeSource?,
    cafeSourceId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (cafeSource == null) {
        CafeImportMissingSource(
            cafeSourceId = cafeSourceId,
            onBackClick = onBackClick,
            modifier = modifier
        )
        return
    }

    var webView by remember { mutableStateOf<WebView?>(null) }
    var state by remember(cafeSource.id) {
        mutableStateOf(
            CafeImportUiState(
                cafeSource = cafeSource,
                initialUrl = cafeSource.boardUrl,
                currentUrl = cafeSource.boardUrl
            )
        )
    }

    val handleBack = {
        val currentWebView = webView
        if (currentWebView?.canGoBack() == true) {
            currentWebView.goBack()
            state = state.copy(canGoBack = currentWebView.canGoBack())
        } else {
            onBackClick()
        }
    }

    BackHandler(onBack = handleBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-cafe-import-webview")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CafeImportHeader(
            state = state,
            onCloseClick = onBackClick
        )
        CafeImportActions(
            isLoading = state.isLoading,
            onBackClick = handleBack,
            onReloadClick = {
                state = state.copy(errorMessage = null)
                webView?.reload()
            }
        )
        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-cafe-import-error")
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("cafe-import-webview-container")
        ) {
            CafeWebView(
                initialUrl = state.initialUrl,
                onWebViewCreated = { createdWebView ->
                    webView = createdWebView
                },
                onStateChange = { reducer ->
                    state = reducer(state)
                },
                modifier = Modifier.fillMaxSize()
            )
            if (state.isLoading) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .testTag("cafe-import-loading"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "페이지를 불러오는 중입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.destroy()
            webView = null
        }
    }
}

@Composable
private fun CafeImportHeader(
    state: CafeImportUiState,
    onCloseClick: () -> Unit
) {
    WodLogCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cafe-import-header"),
        title = "WOD 불러오기",
        subtitle = state.cafeSource?.boxName
    ) {
        Text(
            text = "네이버 로그인은 WebView 안에서 직접 진행해 주세요. 앱은 ID/PW를 저장하지 않습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-cafe-import-privacy")
        )
        Text(
            text = state.currentUrl.ifBlank { state.initialUrl },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("text-cafe-import-current-url")
        )
        WodLogSecondaryButton(
            text = "닫기",
            onClick = onCloseClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-close-cafe-import")
        )
    }
}

@Composable
private fun CafeImportActions(
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onReloadClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WodLogSecondaryButton(
            text = "뒤로",
            onClick = onBackClick,
            modifier = Modifier
                .weight(1f)
                .testTag("action-webview-back")
        )
        WodLogPrimaryButton(
            text = "새로고침",
            onClick = onReloadClick,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .testTag("action-webview-reload")
        )
    }
}

@Composable
private fun CafeWebView(
    initialUrl: String,
    onWebViewCreated: (WebView) -> Unit,
    onStateChange: ((CafeImportUiState) -> CafeImportUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxHeight()
            .heightIn(min = 320.dp)
            .testTag("cafe-import-webview"),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean = false

                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onStateChange {
                            it.copy(
                                currentUrl = url.orEmpty(),
                                isLoading = true,
                                canGoBack = view.canGoBack(),
                                errorMessage = null
                            )
                        }
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        onStateChange {
                            it.copy(
                                currentUrl = url.orEmpty(),
                                isLoading = false,
                                canGoBack = view.canGoBack()
                            )
                        }
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request.isForMainFrame) {
                            onStateChange {
                                it.copy(
                                    isLoading = false,
                                    canGoBack = view.canGoBack(),
                                    errorMessage = "페이지를 불러오지 못했습니다. 새로고침하거나 URL을 확인해 주세요."
                                )
                            }
                        }
                    }
                }
                onWebViewCreated(this)
                loadUrl(initialUrl)
            }
        }
    )
}

@Composable
private fun CafeImportMissingSource(
    cafeSourceId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-cafe-import-missing-source")
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD 불러오기",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "선택한 카페 소스를 찾지 못했습니다. 설정에서 URL을 확인해 주세요.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-cafe-import-missing-source")
        )
        Text(
            text = "CafeSource id: $cafeSourceId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        WodLogSecondaryButton(
            text = "뒤로가기",
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-back-from-cafe-import-missing-source")
        )
    }
}
