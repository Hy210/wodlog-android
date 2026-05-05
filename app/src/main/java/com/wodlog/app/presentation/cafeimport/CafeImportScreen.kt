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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wodlog.app.domain.model.CafePostCandidate
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun CafeImportScreen(
    cafeSource: CafeSource?,
    cafeSourceId: Long,
    onBackClick: () -> Unit,
    onOpenCafeSourceSettings: () -> Unit = {},
    onCreateManualWod: () -> Unit = {},
    onImportedWodTextReady: (ImportedWodText) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (cafeSource == null) {
        CafeImportMissingSource(
            cafeSourceId = cafeSourceId,
            onBackClick = onBackClick,
            onOpenCafeSourceSettings = onOpenCafeSourceSettings,
            onCreateManualWod = onCreateManualWod,
            modifier = modifier
        )
        return
    }

    var webView by remember { mutableStateOf<WebView?>(null) }
    val candidateExtractor = remember { CafePostCandidateExtractor() }
    val importedWodTextExtractor = remember { ImportedWodTextExtractor() }
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
            .verticalScroll(rememberScrollState())
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
            isExtractingCandidates = state.isExtractingCandidates,
            isExtractingImportedText = state.isExtractingImportedText,
            onBackClick = handleBack,
            onReloadClick = {
                state = state.copy(errorMessage = null, importedTextMessage = null)
                webView?.reload()
            },
            onFindCandidatesClick = {
                val currentWebView = webView
                if (currentWebView == null) {
                    state = state.copy(
                        candidateMessage = "현재 화면을 확인하지 못했습니다. 새로고침 후 다시 시도해 주세요.",
                        isCandidateListVisible = true
                    )
                    return@CafeImportActions
                }

                val currentPageUrl = state.currentUrl.ifBlank { state.initialUrl }
                state = state.copy(
                    isExtractingCandidates = true,
                    candidateMessage = null,
                    errorMessage = null
                )
                currentWebView.evaluateJavascript(CafePostCandidateExtractionScript) { result ->
                    val candidates = candidateExtractor.extract(
                        evaluateJavascriptResult = result.orEmpty(),
                        currentPageUrl = currentPageUrl,
                        titleKeywords = cafeSource.titleKeywords
                    )
                    state = state.copy(
                        isExtractingCandidates = false,
                        candidates = candidates,
                        isCandidateListVisible = true,
                        candidateMessage = if (candidates.isEmpty()) {
                            "현재 화면에서 WOD 후보를 찾지 못했습니다."
                        } else {
                            null
                        }
                    )
                }
            },
            onImportTextClick = {
                val currentWebView = webView
                if (currentWebView == null) {
                    state = state.copy(
                        importedTextMessage = "본문을 가져오지 못했습니다. 게시글 화면에서 다시 시도해 주세요."
                    )
                    return@CafeImportActions
                }

                val currentPageUrl = state.currentUrl.ifBlank { state.initialUrl }
                state = state.copy(
                    isExtractingImportedText = true,
                    importedTextMessage = null,
                    errorMessage = null
                )
                currentWebView.evaluateJavascript(ImportedWodTextExtractionScript) { result ->
                    val javascriptResult = result.orEmpty()
                    val importedWodText = importedWodTextExtractor.extract(
                        evaluateJavascriptResult = javascriptResult,
                        fallbackSourceUrl = currentPageUrl
                    )
                    if (importedWodText == null) {
                        val message = if (javascriptResult.isBlank() || javascriptResult.trim() == "null") {
                            "본문을 가져오지 못했습니다. 게시글 화면에서 다시 시도해 주세요."
                        } else {
                            "가져올 본문을 찾지 못했습니다. 게시글 화면에서 다시 시도해 주세요. 가져오기가 어렵다면 WOD 추가에서 직접 입력할 수 있습니다."
                        }
                        state = state.copy(
                            isExtractingImportedText = false,
                            importedTextMessage = message
                        )
                    } else {
                        state = state.copy(isExtractingImportedText = false)
                        onImportedWodTextReady(importedWodText)
                    }
                }
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
        state.importedTextMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-cafe-import-text-error")
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(820.dp)
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

    if (state.isCandidateListVisible) {
        CafePostCandidateDialog(
            candidates = state.candidates,
            message = state.candidateMessage,
            onSelect = { candidate ->
                state = state.copy(isCandidateListVisible = false)
                webView?.loadUrl(candidate.url)
            },
            onDismiss = {
                state = state.copy(isCandidateListVisible = false)
            }
        )
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
    isExtractingCandidates: Boolean,
    isExtractingImportedText: Boolean,
    onBackClick: () -> Unit,
    onReloadClick: () -> Unit,
    onFindCandidatesClick: () -> Unit,
    onImportTextClick: () -> Unit
) {
    val isBusy = isLoading || isExtractingCandidates || isExtractingImportedText
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                enabled = !isBusy,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-webview-reload")
            )
        }
        WodLogPrimaryButton(
            text = "현재 목록에서 WOD 찾기",
            onClick = onFindCandidatesClick,
            enabled = !isBusy,
            loading = isExtractingCandidates,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-find-wod-candidates")
        )
        WodLogPrimaryButton(
            text = "본문 가져오기",
            onClick = onImportTextClick,
            enabled = !isBusy,
            loading = isExtractingImportedText,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-import-wod-text")
        )
        if (isExtractingImportedText) {
            Text(
                text = "본문을 확인하는 중입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("text-import-wod-text-loading")
            )
        }
    }
}

@Composable
private fun CafePostCandidateDialog(
    candidates: List<CafePostCandidate>,
    message: String?,
    onSelect: (CafePostCandidate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.testTag("dialog-cafe-post-candidates"),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "WOD 후보",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (candidates.isEmpty()) {
                    Text(
                        text = message ?: "현재 화면에서 WOD 후보를 찾지 못했습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("text-cafe-post-candidate-empty")
                    )
                    Text(
                        text = "찾지 못했다면 WOD 추가에서 직접 입력할 수 있습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("text-cafe-post-candidate-fallback")
                    )
                } else {
                    candidates.forEach { candidate ->
                        WodLogCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cafe-post-candidate-${candidate.url.hashCode()}"),
                            title = candidate.title,
                            subtitle = candidate.dateText
                        ) {
                            Text(
                                text = candidate.url,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            candidate.matchedKeyword?.let { keyword ->
                                Text(
                                    text = "매칭 키워드: $keyword",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            WodLogPrimaryButton(
                                text = "이 글 열기",
                                onClick = { onSelect(candidate) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("action-open-cafe-post-${candidate.url.hashCode()}")
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("action-dismiss-cafe-post-candidates")
            ) {
                Text("닫기")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
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
    onOpenCafeSourceSettings: () -> Unit,
    onCreateManualWod: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (cafeSourceId == 0L) {
        "등록된 카페 소스가 없습니다. 설정에서 게시판 URL을 먼저 등록해 주세요."
    } else {
        "카페 소스를 찾지 못했습니다. 설정을 확인해 주세요."
    }
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
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-cafe-import-missing-source")
        )
        Text(
            text = "가져오기가 어렵다면 WOD 추가에서 직접 입력할 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-cafe-import-manual-fallback")
        )
        Text(
            text = "CafeSource id: $cafeSourceId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        WodLogPrimaryButton(
            text = "설정에서 URL 등록",
            onClick = onOpenCafeSourceSettings,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-open-cafe-source-settings")
        )
        WodLogSecondaryButton(
            text = "WOD 추가로 직접 입력",
            onClick = onCreateManualWod,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-create-manual-wod-from-cafe-import")
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
