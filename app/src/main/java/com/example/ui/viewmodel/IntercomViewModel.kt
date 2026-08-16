package com.example.ui.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.RccDatabase
import com.example.data.model.IntercomEntry
import com.example.data.repository.IntercomRepository
import com.example.data.repository.SyncState
import com.example.util.LocationHelper
import com.example.util.LocationResult
import com.example.util.RccUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EntryWithDistance(
    val entry: IntercomEntry,
    val distanceMeters: Double? = null,
    val formattedDistance: String? = null
)

data class FilterCriteria(
    val query: String,
    val district: String,
    val favoritesOnly: Boolean,
    val sortByProximity: Boolean,
    val location: Location?
)

class IntercomViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: IntercomRepository

    init {
        val database = RccDatabase.getDatabase(application)
        repository = IntercomRepository(database.intercomDao(), application)
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    // UI state filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDistrict = MutableStateFlow("Wszystkie")
    val selectedDistrict = _selectedDistrict.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly = _favoritesOnly.asStateFlow()

    private val _sortByProximity = MutableStateFlow(false)
    val sortByProximity = _sortByProximity.asStateFlow()

    // Location state
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating = _isLocating.asStateFlow()

    private val _currentAddress = MutableStateFlow<LocationResult?>(null)
    val currentAddress = _currentAddress.asStateFlow()

    // Active full screen keypad modal
    private val _keypadModalEntry = MutableStateFlow<IntercomEntry?>(null)
    val keypadModalEntry = _keypadModalEntry.asStateFlow()

    // Flashlight state
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn = _isFlashlightOn.asStateFlow()

    val syncState: StateFlow<SyncState> = repository.syncState

    val allDistricts: StateFlow<List<String>> = repository.allDistricts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combine UI filter properties into single typed flow
    private val filterCriteria: Flow<FilterCriteria> = combine(
        combine(_searchQuery, _selectedDistrict, _favoritesOnly) { q, d, f ->
            Triple(q, d, f)
        },
        combine(_sortByProximity, _currentLocation) { s, loc ->
            Pair(s, loc)
        }
    ) { (q, d, f), (s, loc) ->
        FilterCriteria(
            query = q,
            district = d,
            favoritesOnly = f,
            sortByProximity = s,
            location = loc
        )
    }

    // Filtered & Distance-annotated entries stream
    val entriesWithDistance: StateFlow<List<EntryWithDistance>> = combine(
        repository.allEntries,
        filterCriteria
    ) { entries: List<IntercomEntry>, filter: FilterCriteria ->
        var list = entries

        if (filter.favoritesOnly) {
            list = list.filter { it.isFavorite }
        }

        if (filter.district.isNotBlank() && !filter.district.equals("Wszystkie", ignoreCase = true)) {
            list = list.filter { it.district.equals(filter.district, ignoreCase = true) }
        }

        if (filter.query.isNotBlank()) {
            val q = filter.query.trim().lowercase()
            list = list.filter {
                it.street.lowercase().contains(q) ||
                it.district.lowercase().contains(q) ||
                it.blockNumber.lowercase().contains(q) ||
                it.staircaseNumber.lowercase().contains(q) ||
                it.intercomCode.lowercase().contains(q) ||
                it.rfidCode.lowercase().contains(q) ||
                it.receiver.lowercase().contains(q) ||
                it.powerSupply.lowercase().contains(q) ||
                it.note.lowercase().contains(q)
            }
        }

        val mapped: List<EntryWithDistance> = list.map { entry ->
            val loc = filter.location
            if (loc != null && entry.latitude != null && entry.longitude != null) {
                val dist = LocationHelper.calculateDistanceMeters(
                    loc.latitude,
                    loc.longitude,
                    entry.latitude,
                    entry.longitude
                )
                EntryWithDistance(
                    entry = entry,
                    distanceMeters = dist,
                    formattedDistance = LocationHelper.formatDistance(dist)
                )
            } else {
                EntryWithDistance(entry = entry)
            }
        }

        if (filter.sortByProximity && filter.location != null) {
            mapped.sortedWith(
                compareBy<EntryWithDistance> { it.distanceMeters ?: Double.MAX_VALUE }
                    .thenByDescending { it.entry.isFavorite }
            )
        } else {
            mapped
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getEntryById(id: Long): IntercomEntry? {
        return repository.getEntryById(id)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedDistrict(district: String) {
        _selectedDistrict.value = district
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun toggleSortByProximity() {
        val next = !_sortByProximity.value
        _sortByProximity.value = next
        if (next && _currentLocation.value == null) {
            fetchCurrentLocation()
        }
    }

    fun fetchCurrentLocation(onLocationFetched: ((LocationResult) -> Unit)? = null) {
        viewModelScope.launch {
            _isLocating.value = true
            val loc = LocationHelper.getCurrentLocation(getApplication())
            _currentLocation.value = loc
            if (loc != null) {
                val addressResult = LocationHelper.reverseGeocode(
                    getApplication(),
                    loc.latitude,
                    loc.longitude
                )
                _currentAddress.value = addressResult
                onLocationFetched?.invoke(addressResult)
            }
            _isLocating.value = false
        }
    }

    fun saveEntry(entry: IntercomEntry, onDone: () -> Unit) {
        viewModelScope.launch {
            if (entry.id == 0L) {
                repository.insert(entry)
            } else {
                repository.update(entry)
            }
            onDone()
        }
    }

    fun deleteEntry(entry: IntercomEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    fun toggleFavorite(entry: IntercomEntry) {
        viewModelScope.launch {
            repository.toggleFavorite(entry)
        }
    }

    fun showKeypadModal(entry: IntercomEntry?) {
        _keypadModalEntry.value = entry
    }

    fun toggleFlashlight() {
        val newState = RccUtils.toggleFlashlight(getApplication())
        _isFlashlightOn.value = newState
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            repository.performCloudSync()
        }
    }

    fun getCloudUrl(): String = repository.getCloudServerUrl()
    fun setCloudUrl(url: String) = repository.setCloudServerUrl(url)

    fun getCloudApiKey(): String = repository.getCloudApiKey()
    fun setCloudApiKey(key: String) = repository.setCloudApiKey(key)

    fun exportDatabaseJson(entries: List<IntercomEntry>): String {
        return RccUtils.exportToJson(entries)
    }

    fun exportDatabaseCsv(entries: List<IntercomEntry>): String {
        return RccUtils.exportToCsv(entries)
    }

    fun importData(
        text: String,
        isJson: Boolean,
        overwrite: Boolean,
        onResult: (Boolean, Int, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val parsed = if (isJson) {
                    RccUtils.importFromJson(text)
                } else {
                    RccUtils.importFromCsv(text)
                }

                if (parsed.isNullOrEmpty()) {
                    onResult(false, 0, "Nie znaleziono poprawnych wpisów w pliku.")
                } else {
                    val count = repository.importEntries(parsed, overwrite)
                    onResult(true, count, "Pomyślnie zaimportowano $count wpisów.")
                }
            } catch (e: Exception) {
                onResult(false, 0, "Błąd importu: ${e.localizedMessage}")
            }
        }
    }

    fun seedSampleData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.importEntries(RccUtils.getSampleInstallations(), overwriteAll = false)
            onDone()
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IntercomViewModel(application) as T
                }
            }
    }
}
