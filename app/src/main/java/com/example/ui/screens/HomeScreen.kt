package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IntercomEntry
import com.example.ui.components.IntercomCard
import com.example.ui.components.KeypadDialog
import com.example.ui.theme.TechBentoBg
import com.example.ui.theme.TechBlack
import com.example.ui.theme.TechBlackPure
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechGreen
import com.example.ui.theme.TechOrange
import com.example.ui.theme.TechOrangeBright
import com.example.ui.theme.TechOrangeContainer
import com.example.ui.theme.TechOrangeLight
import com.example.ui.theme.TechRed
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite
import com.example.ui.theme.TechWhiteBorder
import com.example.ui.theme.TechWhiteBorderAlpha
import com.example.ui.theme.TechWhiteBorderSubtle
import com.example.ui.viewmodel.IntercomViewModel

@Composable
fun HomeScreen(
    viewModel: IntercomViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val entriesWithDist by viewModel.entriesWithDistance.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val sortByProximity by viewModel.sortByProximity.collectAsStateWithLifecycle()
    val isLocating by viewModel.isLocating.collectAsStateWithLifecycle()
    val currentLoc by viewModel.currentLocation.collectAsStateWithLifecycle()
    val currentAddress by viewModel.currentAddress.collectAsStateWithLifecycle()
    val allDistricts by viewModel.allDistricts.collectAsStateWithLifecycle()
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsStateWithLifecycle()
    val keypadModalEntry by viewModel.keypadModalEntry.collectAsStateWithLifecycle()

    var entryToDelete by remember { mutableStateOf<IntercomEntry?>(null) }

    // Permission launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.fetchCurrentLocation()
            viewModel.toggleSortByProximity()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TechBlackPure)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ==========================================
            // BENTO HEADER: Logo, Name, Latarka, Status
            // ==========================================
            Surface(
                color = TechBlackPure,
                border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Logo Bento Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(TechOrange, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.toggleFlashlight() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "RCC2000",
                                    tint = TechBlackPure,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "RCC2000",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = TechWhite
                                )
                                Text(
                                    text = "BAZA DOMOFONÓW & RFID",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = TechOrange
                                )
                            }
                        }

                        // Right side toggles: Latarka + Cloud sync badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Flashlight quick toggle
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isFlashlightOn) TechOrange else TechSurfaceElevated,
                                border = BorderStroke(1.dp, if (isFlashlightOn) TechWhite else TechWhiteBorderSubtle),
                                modifier = Modifier.clickable { viewModel.toggleFlashlight() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                        contentDescription = "Latarka",
                                        tint = if (isFlashlightOn) TechBlackPure else TechWhite,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFlashlightOn) "LATARKA" else "LATARKA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFlashlightOn) TechBlackPure else TechTextPrimary
                                    )
                                }
                            }

                            // Cloud status badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TechSurfaceElevated,
                                border = BorderStroke(1.dp, TechWhiteBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Chmura offline",
                                        tint = TechGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OFFLINE OK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TechGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ==========================================
                    // BENTO SEARCH BAR: Black background, crisp white border, orange pin
                    // ==========================================
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_field"),
                        placeholder = {
                            Text(
                                "Szukaj: ulica, kod wejścia, RFID, zasilacz...",
                                color = TechTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lokalizacja",
                                tint = TechOrange
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Wyczyść",
                                        tint = TechTextSecondary
                                    )
                                }
                            } else {
                                IconButton(onClick = {
                                    val hasFine = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFine || hasCoarse) {
                                        viewModel.fetchCurrentLocation { result ->
                                            if (result.street.isNotBlank()) {
                                                viewModel.setSearchQuery(result.street)
                                            }
                                        }
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Wykryj moją ulicę",
                                        tint = TechWhite.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TechSurfaceElevated,
                            unfocusedContainerColor = TechSurfaceElevated,
                            focusedBorderColor = TechWhite,
                            unfocusedBorderColor = TechWhiteBorderAlpha,
                            focusedTextColor = TechWhite,
                            unfocusedTextColor = TechWhite,
                            cursorColor = TechOrange
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ==========================================
                    // BENTO FILTER BUTTONS: AUTO GPS / RĘCZNA & DZIELNICE
                    // ==========================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AUTO / RĘCZNA mode buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // AUTO GPS BUTTON
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sortByProximity) TechOrange else TechSurfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (sortByProximity) TechWhite else TechWhiteBorderSubtle
                                ),
                                modifier = Modifier.clickable {
                                    val hasFine = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFine || hasCoarse) {
                                        viewModel.toggleSortByProximity()
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isLocating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            color = if (sortByProximity) TechBlackPure else TechOrange,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (sortByProximity) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                                            contentDescription = "GPS",
                                            tint = if (sortByProximity) TechBlackPure else TechOrangeLight,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (sortByProximity) "AUTO GPS (WŁ)" else "AUTO GPS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = if (sortByProximity) TechBlackPure else TechWhite
                                    )
                                }
                            }

                            // FAVORITES BUTTON
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (favoritesOnly) TechOrangeContainer else TechSurfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (favoritesOnly) TechOrange else TechWhiteBorderSubtle
                                ),
                                modifier = Modifier.clickable { viewModel.toggleFavoritesOnly() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (favoritesOnly) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Ulubione",
                                        tint = if (favoritesOnly) TechOrange else TechTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ULUBIONE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = if (favoritesOnly) TechOrangeLight else TechTextSecondary
                                    )
                                }
                            }
                        }

                        // Count badge
                        Text(
                            text = "${entriesWithDist.size} OBIEKTÓW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = TechTextSecondary
                        )
                    }

                    // District horizontal chip row (Ręczna lokalizacja)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val districtOptions = listOf("Wszystkie") + allDistricts
                        districtOptions.forEach { district ->
                            val isSelected = selectedDistrict.equals(district, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) TechOrange else TechSurfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) TechWhite else TechWhiteBorderSubtle
                                ),
                                modifier = Modifier.clickable {
                                    viewModel.setSelectedDistrict(district)
                                }
                            ) {
                                Text(
                                    text = district.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = if (isSelected) TechBlackPure else TechTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Current detected address pill if active
            if (currentLoc != null && sortByProximity) {
                Surface(
                    color = TechBentoBg,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 Twoja pozycja: ${currentAddress?.street ?: ""} ${currentAddress?.district ?: ""} (sortowanie od najbliższych)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TechCyan
                        )
                    }
                }
            }

            // ==========================================
            // BENTO GRID LIST OF INSTALLATIONS
            // ==========================================
            if (entriesWithDist.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TechSurfaceElevated,
                        border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(TechOrangeContainer, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TechOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "Brak obiektów w bazie",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TechWhite
                            )
                            Text(
                                text = "Wciśnij pomarańczowy przycisk [+] lub wczytaj bazę przykładową w zakładce Import / Export.",
                                color = TechTextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entriesWithDist, key = { it.entry.id }) { item ->
                        IntercomCard(
                            entry = item.entry,
                            formattedDistance = item.formattedDistance,
                            onEdit = { onNavigateToEdit(item.entry.id) },
                            onDelete = { entryToDelete = item.entry },
                            onToggleFavorite = { viewModel.toggleFavorite(item.entry) },
                            onOpenKeypad = { viewModel.showKeypadModal(item.entry) }
                        )
                    }
                }
            }
        }

        // ==========================================
        // FLOATING ACTION BUTTON: Add new entry
        // ==========================================
        FloatingActionButton(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_entry"),
            containerColor = TechOrange,
            contentColor = TechBlackPure,
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj obiekt",
                modifier = Modifier.size(30.dp)
            )
        }

        // DELETE CONFIRMATION DIALOG (Styled in Bento theme)
        entryToDelete?.let { entry ->
            AlertDialog(
                onDismissRequest = { entryToDelete = null },
                containerColor = TechSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        "Usunąć obiekt z bazy?",
                        fontWeight = FontWeight.Black,
                        color = TechWhite
                    )
                },
                text = {
                    Text(
                        "Czy na pewno chcesz usunąć instalację dla ${entry.street} ${entry.blockNumber} (kod: ${entry.intercomCode})?",
                        color = TechTextPrimary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteEntry(entry)
                            entryToDelete = null
                        }
                    ) {
                        Text("Usuń", color = TechRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { entryToDelete = null }) {
                        Text("Anuluj", color = TechTextSecondary)
                    }
                }
            )
        }

        // FULL SCREEN BENTO KEYPAD DIALOG
        keypadModalEntry?.let { entry ->
            KeypadDialog(
                entry = entry,
                isFlashlightOn = isFlashlightOn,
                onToggleFlashlight = { viewModel.toggleFlashlight() },
                onDismiss = { viewModel.showKeypadModal(null) }
            )
        }
    }
}
