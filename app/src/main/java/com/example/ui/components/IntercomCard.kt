package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IntercomEntry
import com.example.ui.theme.TechBentoBg
import com.example.ui.theme.TechBlackPure
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechGreen
import com.example.ui.theme.TechOrange
import com.example.ui.theme.TechOrangeBright
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
import com.example.util.RccUtils

@Composable
fun IntercomCard(
    entry: IntercomEntry,
    formattedDistance: String? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenKeypad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Bento Grid Card Container
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("intercom_card_${entry.id}"),
        shape = RoundedCornerShape(22.dp),
        color = TechBentoBg,
        border = BorderStroke(
            width = if (entry.isFavorite) 1.5.dp else 1.dp,
            color = if (entry.isFavorite) TechOrange else TechWhiteBorderAlpha
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ==========================================
            // BENTO TILE 1: LOKALIZACJA & ID (Top full-width tile)
            // ==========================================
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = TechSurfaceElevated,
                border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "LOKALIZACJA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TechOrange
                            )
                            if (formattedDistance != null) {
                                Text(
                                    text = "• $formattedDistance",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TechCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = buildString {
                                if (entry.district.isNotBlank()) append("${entry.district}, ")
                                append(if (entry.street.isNotBlank()) "ul. ${entry.street}" else "Brak nazwy")
                                if (entry.blockNumber.isNotBlank()) append(" ${entry.blockNumber}")
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechWhite
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TechSurfaceVariant,
                            border = BorderStroke(1.dp, TechWhiteBorderSubtle)
                        ) {
                            Text(
                                text = "ID: ${entry.id}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TechTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("fav_btn_${entry.id}")
                        ) {
                            Icon(
                                imageVector = if (entry.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Ulubione",
                                tint = if (entry.isFavorite) TechOrange else TechTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BENTO TILE 2: KOD WEJŚCIA (Hero Solid Orange Bento Tile)
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        if (entry.intercomCode.isNotBlank()) {
                            RccUtils.copyToClipboard(context, "Kod domofonu", entry.intercomCode)
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                color = TechOrange,
                border = BorderStroke(1.dp, TechWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Watermark background lock icon
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = TechBlackPure.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(76.dp)
                            .align(Alignment.BottomEnd)
                            .rotate(12f)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KOD WEJŚCIA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = TechBlackPure.copy(alpha = 0.85f)
                            )

                            // Quick Dialpad & Copy action pill
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechWhite.copy(alpha = 0.6f)),
                                    modifier = Modifier.clickable { onOpenKeypad() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Dialpad,
                                            contentDescription = "Klawiatura",
                                            tint = TechOrangeBright,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "DUŻA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TechWhite
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechWhite.copy(alpha = 0.6f)),
                                    modifier = Modifier.clickable {
                                        if (entry.intercomCode.isNotBlank()) {
                                            RccUtils.copyToClipboard(context, "Kod domofonu", entry.intercomCode)
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Kopiuj",
                                            tint = TechWhite,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "KOPIUJ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TechWhite
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (entry.intercomCode.isNotBlank()) entry.intercomCode else "---",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-0.5).sp,
                            color = TechBlackPure
                        )

                        Text(
                            text = buildString {
                                append("Odbiornik: ")
                                append(if (entry.receiver.isNotBlank()) entry.receiver else "Standard")
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechBlackPure.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // ==========================================
            // BENTO ROW 1: [Blok / Klatka] + [Piętro]
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BLOK / KLATKA TILE
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TechSurfaceElevated,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "BLOK / KLATKA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TechWhite.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = buildString {
                                append(if (entry.blockNumber.isNotBlank()) "Blok ${entry.blockNumber}" else "Brak")
                                if (entry.staircaseNumber.isNotBlank()) append(" / kl. ${entry.staircaseNumber}")
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechWhite
                        )
                    }
                }

                // PIĘTRO TILE
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TechSurfaceElevated,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "PIĘTRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TechWhite.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (entry.floor.isNotBlank()) "${entry.floor} Piętro" else "Parter (0)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechWhite
                        )
                    }
                }
            }

            // ==========================================
            // BENTO ROW 2: [RFID TAG] + [ZASILACZ]
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // RFID TAG TILE
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TechSurfaceElevated,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (entry.rfidCode.isNotBlank()) {
                                RccUtils.copyToClipboard(context, "Kod RFID", entry.rfidCode)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(TechBlackPure, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = "RFID",
                                tint = TechOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RFID TAG",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = TechWhite.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (entry.rfidCode.isNotBlank()) entry.rfidCode else "Brak",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.rfidCode.isNotBlank()) TechGreen else TechTextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }

                // ZASILACZ TILE
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TechSurfaceElevated,
                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(TechBlackPure, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "Zasilacz",
                                tint = TechOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ZASILACZ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = TechWhite.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (entry.powerSupply.isNotBlank()) entry.powerSupply else "12V AC/DC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechWhite,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BENTO TILE 5: NOTATKA / UWAGI & ACTION FOOTER
            // ==========================================
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = TechSurfaceElevated,
                border = BorderStroke(1.dp, TechWhiteBorderAlpha),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = TechWhite.copy(alpha = 0.05f),
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.TopEnd)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = TechOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "NOTATKA SERWISOWA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = TechOrange,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Text(
                            text = if (entry.note.isNotBlank()) "„${entry.note}”" else "„Brak dodatkowych uwag technicznych dla tego obiektu.”",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            color = if (entry.note.isNotBlank()) TechWhite.copy(alpha = 0.85f) else TechTextMuted,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Quick Action Buttons Row inside Bento Note Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // GPS Map button
                            if (entry.latitude != null && entry.longitude != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
                                    modifier = Modifier.clickable {
                                        val geoUri = Uri.parse("geo:${entry.latitude},${entry.longitude}?q=${entry.latitude},${entry.longitude}(${entry.street} ${entry.blockNumber})")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (_: Exception) {}
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Directions,
                                            contentDescription = "Nawiguj",
                                            tint = TechCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "MAPA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TechCyan
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            // Share, Edit, Delete icons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Share
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                                    modifier = Modifier.clickable {
                                        val shareText = buildString {
                                            append("RCC2000 - ${entry.street} ${entry.blockNumber}")
                                            if (entry.staircaseNumber.isNotBlank()) append(" kl. ${entry.staircaseNumber}")
                                            if (entry.district.isNotBlank()) append(" (${entry.district})")
                                            append("\nKod: ${entry.intercomCode}")
                                            if (entry.rfidCode.isNotBlank()) append("\nRFID: ${entry.rfidCode}")
                                            if (entry.receiver.isNotBlank()) append("\nOdbiornik: ${entry.receiver}")
                                            if (entry.powerSupply.isNotBlank()) append("\nZasilacz: ${entry.powerSupply}")
                                            if (entry.note.isNotBlank()) append("\nNotatka: ${entry.note}")
                                        }
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Udostępnij dane"))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Udostępnij",
                                        tint = TechTextSecondary,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(14.dp)
                                    )
                                }

                                // Edit
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                                    modifier = Modifier.clickable { onEdit() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edytuj",
                                        tint = TechOrangeLight,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(14.dp)
                                    )
                                }

                                // Delete
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TechBlackPure,
                                    border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                                    modifier = Modifier.clickable { onDelete() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Usuń",
                                        tint = TechRed,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
