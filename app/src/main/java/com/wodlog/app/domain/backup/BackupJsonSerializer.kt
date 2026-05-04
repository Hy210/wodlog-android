package com.wodlog.app.domain.backup

import kotlinx.serialization.json.Json

object BackupJsonSerializer {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(backup: WodlogBackup): String =
        json.encodeToString(WodlogBackup.serializer(), backup)

    fun decode(jsonString: String): WodlogBackup =
        json.decodeFromString(WodlogBackup.serializer(), jsonString)
}
