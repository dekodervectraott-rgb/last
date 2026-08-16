package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.IntercomEntry
import com.example.ui.theme.TechBlackPure
import com.example.ui.theme.TechOrange
import com.example.ui.theme.TechOrangeBright
import com.example.ui.theme.TechOrangeContainer
import com.example.ui.theme.TechOrangeLight
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite
import com.example.ui.theme.TechWhiteBorder
import com.example.ui.theme.TechWhiteBorderAlpha
import com.example.ui.theme.TechWhiteBorderSubtle
import com.example.util.RccUtils

@Composable
fun KeypadDialog(
    entry: IntercomEntry,
    isFlashlightOn: Boolean,
    onToggleFlashlight: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = TechSurfaceElevated,
            border = BorderStroke(1.dp, TechWhiteBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RCC2000 PODGLĄD KODU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = TechOrange
                        )
                        Text(
                            text = buildString {
                                append(if (entry.street.isNotBlank()) entry.street else "Obiekt")
                                if (entry.blockNumber.isNotBlank()) append(" ${entry.blockNumber}")
                                if (entry.staircaseNumber.isNotBlank()) append(" kl. ${entry.staircaseNumber}")
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TechWhite
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zamknij",
                            tint = TechTextSecondary
                        )
                    }
                }

                // Hero Orange Bento Display for Code
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TechOrange,
                    border = BorderStroke(1.dp, TechWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "KOD WEJŚCIA DO DOMOFONU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TechBlackPure.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (entry.intercomCode.isNotBlank()) entry.intercomCode else "BRAK KODU",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TechBlackPure,
                            textAlign = TextAlign.Center
                        )

                        if (entry.rfidCode.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "RFID: ${entry.rfidCode} (${entry.rfidType})",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TechBlackPure.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Keypad Visual Aid layout
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TechBlackPure,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val keyRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("🔑 KLUCZ", "0", "🔔 / #")
                        )

                        keyRows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { label ->
                                    val isAction = label.contains("KLUCZ") || label.contains("#")
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isAction) TechOrangeContainer else TechSurfaceElevated,
                                        border = BorderStroke(1.dp, if (isAction) TechOrange else TechWhiteBorderSubtle),
                                        modifier = Modifier
                                            .size(if (isAction) 80.dp else 60.dp, 42.dp)
                                            .clickable {
                                                RccUtils.vibrate(context, 25)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = label,
                                                fontSize = if (isAction) 10.sp else 16.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (isAction) TechOrangeBright else TechWhite
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons: Copy & Flashlight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (entry.intercomCode.isNotBlank()) {
                                RccUtils.copyToClipboard(context, "Kod domofonu", entry.intercomCode)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TechOrange,
                            contentColor = TechBlackPure
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, TechWhite)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Kopiuj",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("KOPIUJ KOD", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = onToggleFlashlight,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFlashlightOn) TechOrange else TechBlackPure,
                            contentColor = if (isFlashlightOn) TechBlackPure else TechWhite
                        ),
                        border = BorderStroke(1.dp, if (isFlashlightOn) TechWhite else TechWhiteBorderSubtle),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Latarka",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFlashlightOn) "LATARKA WŁ" else "LATARKA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
