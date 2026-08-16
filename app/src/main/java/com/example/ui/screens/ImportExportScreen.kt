package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.util.RccUtils

@Composable
fun ImportExportScreen(
    viewModel: IntercomViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val entriesWithDist by viewModel.entriesWithDistance.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    val entries = entriesWithDist.map { it.entry }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechBlackPure)
    ) {
        // TOP BENTO TAB ROW
        Surface(
            color = TechBlackPure,
            border = BorderStroke(1.dp, TechWhiteBorderSubtle)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = TechBlackPure,
                contentColor = TechOrange,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = TechOrange
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "EKSPORT & IMPORT",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            color = if (selectedTab == 0) TechOrange else TechTextSecondary
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selectedTab == 0) TechOrange else TechTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "SYNCHRONIZACJA CHMURY",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            color = if (selectedTab == 1) TechOrange else TechTextSecondary
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selectedTab == 1) TechOrange else TechTextSecondary
                        )
                    }
                )
            }
        }

        // TAB CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                // TAB 1: IMPORT / EXPORT BENTO TILES
                ExportSectionBento(
                    entries = entries,
                    onExportJson = { viewModel.exportDatabaseJson(entries) },
                    onExportCsv = { viewModel.exportDatabaseCsv(entries) }
                )

                ImportSectionBento(
                    onImport = { text, isJson, overwrite, callback ->
                        viewModel.importData(text, isJson, overwrite, callback)
                    },
                    onSeedSampleData = {
                        viewModel.seedSampleData {
                            RccUtils.vibrate(context, 40)
                        }
                    }
                )
            } else {
                // TAB 2: CLOUD SYNC BENTO TILES
                CloudSyncSectionBento(
                    viewModel = viewModel,
                    syncState = syncState,
                    totalEntriesCount = entries.size
                )
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun ExportSectionBento(
    entries: List<IntercomEntry>,
    onExportJson: () -> String,
    onExportCsv: () -> String
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TechSurfaceElevated,
        border = BorderStroke(1.dp, TechWhiteBorderAlpha),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = TechOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "EKSPORT BAZY RCC2000",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TechOrange
                )
            }

            Text(
                text = "Zapisz kopię zapasową ${entries.size} instalacji do formatu JSON lub arkusza CSV.",
                color = TechTextSecondary,
                fontSize = 12.sp
            )

            // Two Bento action buttons in row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Export JSON
                Button(
                    onClick = {
                        val json = onExportJson()
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, json)
                            type = "application/json"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Eksportuj bazę JSON"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_export_json"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechOrange,
                        contentColor = TechBlackPure
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TechWhite)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EKSPORT JSON", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                // Export CSV
                Button(
                    onClick = {
                        val csv = onExportCsv()
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csv)
                            type = "text/csv"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Eksportuj CSV (Excel)"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_export_csv"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechBlackPure,
                        contentColor = TechWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EKSPORT CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ImportSectionBento(
    onImport: (String, Boolean, Boolean, (Boolean, Int, String) -> Unit) -> Unit,
    onSeedSampleData: () -> Unit
) {
    var rawInputText by remember { mutableStateOf("") }
    var overwriteExisting by remember { mutableStateOf(false) }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(true) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TechSurfaceElevated,
        border = BorderStroke(1.dp, TechWhiteBorderAlpha),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = TechOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "IMPORT DANYCH DO BAZY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TechOrange
                )
            }

            Text(
                text = "Wklej kod JSON lub wiersze CSV, aby zaimportować domofony i pastylki.",
                color = TechTextSecondary,
                fontSize = 12.sp
            )

            // Seed sample database button
            Button(
                onClick = {
                    onSeedSampleData()
                    importStatusMessage = "Wczytano bazę instalacji demonstracyjnych (Warszawa: Mokotów, Śródmieście, Ursynów, Wola)."
                    isSuccessStatus = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_seed_sample_data"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechOrangeContainer,
                    contentColor = TechOrangeBright
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TechOrange.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ZAŁADUJ BAZĘ PRZYKŁADOWĄ (DEMO)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Input textarea
            OutlinedTextField(
                value = rawInputText,
                onValueChange = { rawInputText = it },
                label = { Text("Wklej tutaj JSON lub CSV", color = TechTextSecondary, fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("import_text_field"),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TechWhite
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TechBlackPure,
                    unfocusedContainerColor = TechBlackPure,
                    focusedBorderColor = TechWhite,
                    unfocusedBorderColor = TechWhiteBorderSubtle,
                    cursorColor = TechOrange
                )
            )

            // Overwrite Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { overwriteExisting = !overwriteExisting }
            ) {
                Checkbox(
                    checked = overwriteExisting,
                    onCheckedChange = { overwriteExisting = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = TechOrange,
                        checkmarkColor = TechBlackPure,
                        uncheckedColor = TechTextSecondary
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nadpisz istniejącą bazę (usuń obecne wpisy)",
                    color = TechTextPrimary,
                    fontSize = 12.sp
                )
            }

            // Perform Import Button
            Button(
                onClick = {
                    if (rawInputText.isBlank()) {
                        importStatusMessage = "Wklej tekst JSON lub CSV przed importem."
                        isSuccessStatus = false
                        return@Button
                    }
                    val isJson = rawInputText.trim().startsWith("[") || rawInputText.trim().startsWith("{")
                    onImport(rawInputText, isJson, overwriteExisting) { success, count, msg ->
                        isSuccessStatus = success
                        importStatusMessage = msg
                        if (success) {
                            rawInputText = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_import_execute"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechOrange,
                    contentColor = TechBlackPure
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TechWhite)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("WYKONAJ IMPORT", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            // Status message
            if (importStatusMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSuccessStatus) TechSurfaceVariant else TechSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSuccessStatus) TechGreen else TechRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = importStatusMessage ?: "",
                        color = if (isSuccessStatus) TechGreen else TechRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CloudSyncSectionBento(
    viewModel: IntercomViewModel,
    syncState: com.example.data.repository.SyncState,
    totalEntriesCount: Int
) {
    var cloudUrl by remember { mutableStateOf(viewModel.getCloudUrl()) }
    var apiKey by remember { mutableStateOf(viewModel.getCloudApiKey()) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TechSurfaceElevated,
        border = BorderStroke(1.dp, TechWhiteBorderAlpha),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = TechOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "SYNCHRONIZACJA CHMUROWA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TechOrange
                )
            }

            Text(
                text = "Aplikacja działa w 100% offline (baza SQLite Room). Po nawiązaniu połączenia może synchronizować kody z serwerem centralnym RCC.",
                color = TechTextSecondary,
                fontSize = 12.sp
            )

            // Cloud Status Bento Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TechBlackPure,
                border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when {
                        syncState.isSyncing -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = TechOrange,
                                strokeWidth = 2.5.dp
                            )
                        }
                        syncState.syncSuccess == true -> {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = TechGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        syncState.syncSuccess == false -> {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = TechRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = TechGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STATUS SYNCHRONIZACJI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TechWhite.copy(alpha = 0.5f)
                        )
                        Text(
                            text = when {
                                syncState.isSyncing -> "Trwa synchronizacja danych..."
                                syncState.syncSuccess == true -> "Zsynchronizowano: ${syncState.lastSyncTime ?: "OK"}"
                                syncState.syncSuccess == false -> "Offline / Błąd: ${syncState.lastSyncMessage}"
                                else -> syncState.lastSyncMessage
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                syncState.syncSuccess == true -> TechGreen
                                syncState.syncSuccess == false -> TechRed
                                else -> TechWhite
                            }
                        )
                    }
                }
            }

            // Cloud Server URL
            OutlinedTextField(
                value = cloudUrl,
                onValueChange = {
                    cloudUrl = it
                    viewModel.setCloudUrl(it)
                },
                label = { Text("Serwer chmury (Endpoint API)", color = TechTextSecondary, fontSize = 11.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lan, contentDescription = null, tint = TechOrange, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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

            // API Key
            OutlinedTextField(
                value = apiKey,
                onValueChange = {
                    apiKey = it
                    viewModel.setCloudApiKey(it)
                },
                label = { Text("Klucz autoryzacyjny API (Token RCC)", color = TechTextSecondary, fontSize = 11.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = TechOrange, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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

            // Manual Sync Trigger Button
            Button(
                onClick = { viewModel.syncWithCloud() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_sync_now"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechOrange,
                    contentColor = TechBlackPure
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TechWhite)
            ) {
                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SYNCHRONIZUJ TERAZ Z CHMURĄ", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
