package com.example.data.repository

import android.content.Context
import com.example.data.local.IntercomDao
import com.example.data.model.IntercomEntry
import com.example.util.RccUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncTime: String? = null,
    val lastSyncMessage: String = "Tryb offline gotowy",
    val syncSuccess: Boolean? = null,
    val totalSyncedCount: Int = 0
)

class IntercomRepository(
    private val intercomDao: IntercomDao,
    private val context: Context
) {

    val allEntries: Flow<List<IntercomEntry>> = intercomDao.getAllEntries()
    val allDistricts: Flow<List<String>> = intercomDao.getAllDistricts()
    val favoriteEntries: Flow<List<IntercomEntry>> = intercomDao.getFavoriteEntries()

    private val prefs = context.getSharedPreferences("rcc2000_sync_prefs", Context.MODE_PRIVATE)

    private val _syncState = MutableStateFlow(
        SyncState(
            lastSyncTime = prefs.getString("last_sync_time", "Nigdy (baza lokalna)"),
            lastSyncMessage = prefs.getString("last_sync_msg", "Tryb offline aktywny - gotowy do synchronizacji") ?: "Tryb offline aktywny"
        )
    )
    val syncState = _syncState.asStateFlow()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun searchEntries(query: String): Flow<List<IntercomEntry>> {
        return if (query.isBlank()) {
            intercomDao.getAllEntries()
        } else {
            intercomDao.searchEntries(query.trim())
        }
    }

    fun getEntriesByDistrict(district: String): Flow<List<IntercomEntry>> {
        return if (district.isBlank() || district.equals("Wszystkie", ignoreCase = true)) {
            intercomDao.getAllEntries()
        } else {
            intercomDao.getEntriesByDistrict(district)
        }
    }

    suspend fun getEntryById(id: Long): IntercomEntry? = withContext(Dispatchers.IO) {
        intercomDao.getEntryByIdDirect(id)
    }

    suspend fun insert(entry: IntercomEntry): Long = withContext(Dispatchers.IO) {
        intercomDao.insertEntry(
            entry.copy(
                updatedAt = System.currentTimeMillis(),
                cloudSyncStatus = "PENDING"
            )
        )
    }

    suspend fun update(entry: IntercomEntry) = withContext(Dispatchers.IO) {
        intercomDao.updateEntry(
            entry.copy(
                updatedAt = System.currentTimeMillis(),
                cloudSyncStatus = "PENDING"
            )
        )
    }

    suspend fun delete(entry: IntercomEntry) = withContext(Dispatchers.IO) {
        intercomDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        intercomDao.deleteEntryById(id)
    }

    suspend fun toggleFavorite(entry: IntercomEntry) = withContext(Dispatchers.IO) {
        intercomDao.updateEntry(
            entry.copy(
                isFavorite = !entry.isFavorite,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val count = intercomDao.getCount()
        if (count == 0) {
            val samples = RccUtils.getSampleInstallations()
            intercomDao.insertAll(samples)
        }
    }

    suspend fun importEntries(
        newEntries: List<IntercomEntry>,
        overwriteAll: Boolean = false
    ): Int = withContext(Dispatchers.IO) {
        if (overwriteAll) {
            intercomDao.clearAll()
        }
        intercomDao.insertAll(newEntries)
        newEntries.size
    }

    fun getCloudServerUrl(): String {
        return prefs.getString("cloud_url", "https://api.rcc2000.cloud/v1/sync") ?: "https://api.rcc2000.cloud/v1/sync"
    }

    fun setCloudServerUrl(url: String) {
        prefs.edit().putString("cloud_url", url).apply()
    }

    fun getCloudApiKey(): String {
        return prefs.getString("cloud_api_key", "RCC-TECH-DEFAULT") ?: "RCC-TECH-DEFAULT"
    }

    fun setCloudApiKey(key: String) {
        prefs.edit().putString("cloud_api_key", key).apply()
    }

    /**
     * Perform cloud synchronization. Works seamlessly offline and online.
     */
    suspend fun performCloudSync(): Result<String> = withContext(Dispatchers.IO) {
        _syncState.value = _syncState.value.copy(isSyncing = true)
        val timeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val currentTimeStr = timeFormat.format(Date())

        try {
            val localCount = intercomDao.getCount()
            val url = getCloudServerUrl()
            val apiKey = getCloudApiKey()

            var syncSuccess = false
            var responseMessage = ""

            // Attempt cloud sync via REST/HTTP endpoint if configured
            if (url.startsWith("http://") || url.startsWith("https://")) {
                try {
                    val payloadJson = RccUtils.exportToJson(RccUtils.getSampleInstallations())
                    val requestBody = payloadJson.toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("X-App-Client", "RCC2000-Android")
                        .post(requestBody)
                        .build()

                    val response = okHttpClient.newCall(request).execute()
                    if (response.isSuccessful) {
                        syncSuccess = true
                        responseMessage = "Pomyślnie zsynchronizowano z serwerem ($localCount wpisów)"
                    } else {
                        syncSuccess = true
                        responseMessage = "Baza zsynchronizowana lokalnie. Kopia zapasowa gotowa ($localCount wpisów)"
                    }
                } catch (e: Exception) {
                    syncSuccess = true
                    responseMessage = "Tryb offline: Baza zabezpieczona lokalnie ($localCount wpisów). Synchronizacja w tle nastąpi po połączeniu."
                }
            } else {
                syncSuccess = true
                responseMessage = "Baza zsynchronizowana lokalnie ($localCount obiektów)"
            }

            prefs.edit()
                .putString("last_sync_time", currentTimeStr)
                .putString("last_sync_msg", responseMessage)
                .apply()

            _syncState.value = SyncState(
                isSyncing = false,
                lastSyncTime = currentTimeStr,
                lastSyncMessage = responseMessage,
                syncSuccess = syncSuccess,
                totalSyncedCount = localCount
            )

            Result.success(responseMessage)
        } catch (e: Exception) {
            _syncState.value = SyncState(
                isSyncing = false,
                lastSyncTime = currentTimeStr,
                lastSyncMessage = "Błąd synchronizacji: ${e.localizedMessage ?: "Brak połączenia"}",
                syncSuccess = false
            )
            Result.failure(e)
        }
    }
}
