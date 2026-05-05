package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.ImportedWodText

object ImportedWodPreviewHolder {
    var current: ImportedWodText? = null
    var editorPrefill: ImportedWodText? = null

    fun consumeEditorPrefill(): ImportedWodText? {
        return editorPrefill.also {
            editorPrefill = null
        }
    }
}
