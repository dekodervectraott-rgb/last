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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IntercomEntry
import com.example.ui.theme.TechBentoBg
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: IntercomViewModel,
    entryId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDistricts by viewModel.allDistricts.collectAsStateWithLifecycle()
    val isLocating by viewModel.isLocating.collectAsStateWithLifecycle()

    var district by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var blockNumber by remember { mutableStateOf("") }
    var staircaseNumber by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var powerSupply by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf("") }
    var intercomCode by remember { mutableStateOf("") }
    var rfidCode by remember { mutableStateOf("") }
    var rfidType by remember { mutableStateOf("RFID 125kHz EM4100") }
    var note by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load existing data if editing
    LaunchedEffect(entryId) {
        if (entryId != null && entryId > 0) {
            val entry = viewModel.getEntryById(entryId)
            if (entry != null) {
                district = entry.district
                street = entry.street
                blockNumber = entry.blockNumber
                staircaseNumber = entry.staircaseNumber
                floor = entry.floor
                powerSupply = entry.powerSupply
                receiver = entry.receiver
                intercomCode = entry.intercomCode
                rfidCode = entry.rfidCode
                rfidType = entry.rfidType
                note = entry.note
                latitude = entry.latitude
                longitude = entry.longitude
                isFavorite = entry.isFavorite
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.fetchCurrentLocation { result ->
                latitude = result.latitude
                longitude = result.longitude
                if (street.isBlank() && result.street.isNotBlank()) street = result.street
                if (blockNumber.isBlank() && result.blockNumber.isNotBlank()) blockNumber = result.blockNumber
                if (district.isBlank() && result.district.isNotBlank()) district = result.district
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (entryId != null && entryId > 0) "EDYTUJ OBIEKT BENTO" else "NOWY OBIEKT BENTO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TechWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = TechWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Ulubione",
                            tint = if (isFavorite) TechOrange else TechTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechBlackPure,
                    titleContentColor = TechWhite
                )
            )
        },
        containerColor = TechBlackPure
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ERROR BANNER
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TechSurfaceElevated,
                    border = BorderStroke(1.dp, TechRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = TechRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ==========================================
            // SECTION 1: ADRES I LOKALIZACJA (Bento Tile)
            // ==========================================
            FormSectionBento(title = "1. LOKALIZACJA I ADRES") {
                // Quick GPS auto-fill button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TechBlackPure,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
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
                                    latitude = result.latitude
                                    longitude = result.longitude
                                    if (street.isBlank() && result.street.isNotBlank()) street = result.street
                                    if (blockNumber.isBlank() && result.blockNumber.isNotBlank()) blockNumber = result.blockNumber
                                    if (district.isBlank() && result.district.isNotBlank()) district = result.district
                                }
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TechOrange,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "GPS",
                                tint = TechOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POBIERZ WSPÓŁRZĘDNE & ADRES Z GPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = TechOrangeLight
                        )
                    }
                }

                if (latitude != null && longitude != null) {
                    Text(
                        text = "Współrzędne: ${latitude?.toString()?.take(8)}, ${longitude?.toString()?.take(8)}",
                        fontSize = 10.sp,
                        color = TechCyan,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // District Selector Chips
                Text(
                    text = "DZIELNICA:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TechWhite.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val defaultDistricts = listOf("Mokotów", "Śródmieście", "Wola", "Praga-Południe", "Ursynów", "Bielany", "Ochota", "Bemowo")
                    val combined = (defaultDistricts + allDistricts).distinct()
                    combined.forEach { d ->
                        val isSelected = district.equals(d, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) TechOrange else TechBlackPure,
                            border = BorderStroke(1.dp, if (isSelected) TechWhite else TechWhiteBorderSubtle),
                            modifier = Modifier.clickable { district = d }
                        ) {
                            Text(
                                text = d,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) TechBlackPure else TechTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                RccInputField(
                    value = district,
                    onValueChange = { district = it },
                    label = "Dzielnica (lub wpisz własną)",
                    leadingIcon = Icons.Default.LocationOn,
                    testTag = "input_district"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RccInputField(
                    value = street,
                    onValueChange = { street = it },
                    label = "Ulica (np. Puławska)",
                    leadingIcon = Icons.Default.LocationOn,
                    testTag = "input_street"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Side-by-side Bento input row: Blok / Klatka / Piętro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RccInputField(
                        value = blockNumber,
                        onValueChange = { blockNumber = it },
                        label = "Nr Bloku",
                        modifier = Modifier.weight(1f),
                        testTag = "input_block"
                    )
                    RccInputField(
                        value = staircaseNumber,
                        onValueChange = { staircaseNumber = it },
                        label = "Klatka",
                        modifier = Modifier.weight(1f),
                        testTag = "input_staircase"
                    )
                    RccInputField(
                        value = floor,
                        onValueChange = { floor = it },
                        label = "Piętro",
                        modifier = Modifier.weight(1f),
                        testTag = "input_floor"
                    )
                }
            }

            // ==========================================
            // SECTION 2: KOD WEJŚCIA DO DOMOFONU (Hero Bento Tile)
            // ==========================================
            FormSectionBento(title = "2. KOD WEJŚCIA DO DOMOFONU", highlight = true) {
                RccInputField(
                    value = intercomCode,
                    onValueChange = { intercomCode = it },
                    label = "Kod do wpisania (np. 4829#, KLUCZ 12 3456)",
                    leadingIcon = Icons.Default.Pin,
                    isMonospace = true,
                    testTag = "input_intercom_code"
                )

                // Quick preset buttons for common intercom formats
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SZYBKIE FORMATY KODÓW:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TechWhite.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf("KLUCZ [nr] [kod]", "# [kod]", "[nr] [kod]", "* [kod] #")
                    presets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TechBlackPure,
                            border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                            modifier = Modifier.clickable {
                                if (intercomCode.isBlank()) intercomCode = preset
                            }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TechOrangeLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 3: PASTYLKA RFID
            // ==========================================
            FormSectionBento(title = "3. PASTYLKA RFID") {
                RccInputField(
                    value = rfidCode,
                    onValueChange = { rfidCode = it },
                    label = "Kod pastylki RFID (Hex / Dec / Unique ID)",
                    leadingIcon = Icons.Default.Nfc,
                    isMonospace = true,
                    testTag = "input_rfid"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // RFID Type Chips
                Text(
                    text = "STANDARD PASTYLKI:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TechWhite.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val rfidTypes = listOf(
                        "RFID 125kHz EM4100",
                        "Mifare 13.56MHz",
                        "Dallas iButton DS1990",
                        "NFC Tag 213/215"
                    )
                    rfidTypes.forEach { type ->
                        val isSelected = rfidType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) TechOrangeContainer else TechBlackPure,
                            border = BorderStroke(1.dp, if (isSelected) TechOrange else TechWhiteBorderSubtle),
                            modifier = Modifier.clickable { rfidType = type }
                        ) {
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TechOrangeBright else TechTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Generator hex button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            if (rfidType.contains("Dallas")) {
                                val bytes = List(6) { Random.nextInt(0, 256) }
                                rfidCode = "01:" + bytes.joinToString(":") { "%02X".format(it) }
                            } else {
                                rfidCode = "%010d".format(Random.nextLong(1000000, 9999999999L))
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TechOrangeBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Generuj przykładowy kod RFID",
                            fontSize = 11.sp,
                            color = TechOrangeBright
                        )
                    }
                }
            }

            // ==========================================
            // SECTION 4: ZASILACZ, ODBIORNIK I NOTATKI
            // ==========================================
            FormSectionBento(title = "4. ZASILACZ, ODBIORNIK I NOTATKI") {
                RccInputField(
                    value = powerSupply,
                    onValueChange = { powerSupply = it },
                    label = "Zasilacz (lokalizacja, model, np. MeanWell 12V)",
                    leadingIcon = Icons.Default.ElectricBolt,
                    testTag = "input_power_supply"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RccInputField(
                    value = receiver,
                    onValueChange = { receiver = it },
                    label = "Odbiornik / Unifon (np. Laskomex LM-8, Cyfral, K45)",
                    leadingIcon = Icons.Default.HeadsetMic,
                    testTag = "input_receiver"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RccInputField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Notatka techniczna (drzwi się zacinają, wejście od podwórza)",
                    leadingIcon = Icons.Default.Notes,
                    maxLines = 4,
                    singleLine = false,
                    testTag = "input_note"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // SAVE BUTTON (Bento Hero Style)
            // ==========================================
            Button(
                onClick = {
                    if (street.isBlank() && blockNumber.isBlank() && intercomCode.isBlank()) {
                        errorMessage = "Wprowadź co najmniej ulicę, numer bloku lub kod wejściowy."
                        return@Button
                    }
                    errorMessage = null

                    val newEntry = IntercomEntry(
                        id = entryId ?: 0L,
                        district = district.trim(),
                        street = street.trim(),
                        blockNumber = blockNumber.trim(),
                        staircaseNumber = staircaseNumber.trim(),
                        floor = floor.trim(),
                        powerSupply = powerSupply.trim(),
                        receiver = receiver.trim(),
                        intercomCode = intercomCode.trim(),
                        rfidCode = rfidCode.trim(),
                        rfidType = rfidType,
                        note = note.trim(),
                        latitude = latitude,
                        longitude = longitude,
                        isFavorite = isFavorite
                    )

                    viewModel.saveEntry(newEntry) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_entry_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechOrange,
                    contentColor = TechBlackPure
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TechWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Zapisz",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZAPISZ OBIEKT W BAZIE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormSectionBento(
    title: String,
    highlight: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TechSurfaceElevated,
        border = BorderStroke(
            1.dp,
            if (highlight) TechOrange else TechWhiteBorderAlpha
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = TechOrange
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun RccInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isMonospace: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TechTextSecondary, fontSize = 11.sp) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(imageVector = leadingIcon, contentDescription = null, tint = TechOrange, modifier = Modifier.size(18.dp)) }
        } else null,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            fontWeight = if (isMonospace) FontWeight.Bold else FontWeight.Normal,
            color = TechWhite
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TechBlackPure,
            unfocusedContainerColor = TechBlackPure,
            focusedBorderColor = TechWhite,
            unfocusedBorderColor = TechWhiteBorderSubtle,
            cursorColor = TechOrange,
            focusedTextColor = TechWhite,
            unfocusedTextColor = TechWhite
        )
    )
}
