package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.TechBentoBg
import com.example.ui.theme.TechBlackPure
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechGreen
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

data class SystemGuide(
    val brand: String,
    val model: String,
    val openFormula: String,
    val installerDefaultCode: String,
    val rfidFormat: String,
    val notes: String
)

@Composable
fun CheatSheetScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hexInput by remember { mutableStateOf("") }
    var convertedDec by remember { mutableStateOf("") }

    val guides = remember {
        listOf(
            SystemGuide(
                brand = "Laskomex",
                model = "CD-2502 / CD-3100 / CP-2503",
                openFormula = "Nr lokalu -> 🔑 KLUCZ -> Kod 4-cyfrowy (np. 14 🔑 2580)",
                installerDefaultCode = "Tryb instalatora: 🔑 + 9999 + PIN serwisu",
                rfidFormat = "Dallas iButton DS1990A oraz 125kHz EM4100",
                notes = "Odbiorniki cyfrowe (LM-8, LG-8). Adresowanie zworkami binarnymi w unifonie (1, 2, 4, 8, 16, 32, 64, 128)."
            ),
            SystemGuide(
                brand = "ACO",
                model = "Familio / CDN / CDNP / Inspiro",
                openFormula = "# / 🔑 -> Nr lokalu -> 4-cyfrowy kod wejścia (np. # 25 7391)",
                installerDefaultCode = "Setup fabryczny: # + 111111 lub # + 159357",
                rfidFormat = "Dallas iButton lub Mifare 13.56MHz / RFID 125kHz",
                notes = "Unifony INS-UP / INS-UP720. Zasilanie 12V-15V AC/DC. Wbudowany zamek szyfrowy i czytnik pastylek."
            ),
            SystemGuide(
                brand = "Cyfral",
                model = "CC-2000 / Cosmop / Smart-D",
                openFormula = "K -> Nr lokalu -> 4-cyfrowy kod tabeli (np. K 12 3412)",
                installerDefaultCode = "Kod master centrali: K + 9999 lub K + 0000",
                rfidFormat = "Dallas DS1990 lub RFID 125kHz Unique",
                notes = "Cyfrowa linia unifonów Smart-D (dwużyłowa). Dedykowany zasilacz 12V AC."
            ),
            SystemGuide(
                brand = "Proel",
                model = "KDC-1803 / KDC-3000 / PC-512",
                openFormula = "KLUCZ -> Nr lokalu -> 4 cyfry kodu (np. KLUCZ 48 9012)",
                installerDefaultCode = "Wejście w setup: KLUCZ + 9999 + hasło",
                rfidFormat = "RFID 125kHz EM4100 / UNIQUE",
                notes = "Unifony PC-512 / PC-255 z kodowaniem adresów fizycznych zworkami. Zasilacz 15V DC."
            ),
            SystemGuide(
                brand = "Urmet",
                model = "Matibus SE / Basic / Digitha",
                openFormula = "Nr lokalu -> 🔔/K -> Kod 4-cyfrowy (np. 104 🔔 8821)",
                installerDefaultCode = "Serwis fabryczny: 12345 lub 999999",
                rfidFormat = "Dallas iButton lub Mifare",
                notes = "Magistrala cyfrowa 12V AC/DC. Pamięć kodów indywidualnych dla każdego lokalu."
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechBlackPure)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TOP BENTO HEADER
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TechSurfaceElevated,
            border = BorderStroke(1.dp, TechWhiteBorderAlpha),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(TechOrange, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = TechBlackPure,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "ŚCIĄGA SERWISOWA INSTALATORA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TechWhite
                    )
                    Text(
                        text = "Procedury wybierania kodów domofonów & konwerter RFID",
                        fontSize = 11.sp,
                        color = TechOrange
                    )
                }
            }
        }

        // RFID CONVERTER BENTO TILE (HEX <-> DEC)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TechSurfaceElevated,
            border = BorderStroke(1.dp, TechOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = null,
                        tint = TechOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "KONWERTER KODÓW PASTYLEK RFID (HEX ➔ DEC)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TechOrange
                    )
                }

                Text(
                    text = "Wpisz kod HEX (np. 004ABC21 lub 1A:2B:3C) aby otrzymać postać dziesiętną do rejestracji:",
                    fontSize = 11.sp,
                    color = TechTextSecondary
                )

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        val clean = input.replace(":", "").replace(" ", "").trim()
                        convertedDec = try {
                            if (clean.isNotBlank()) {
                                val longVal = clean.toLong(16)
                                "DEC: $longVal (Format 10-cyfrowy: %010d)".format(longVal)
                            } else ""
                        } catch (e: Exception) {
                            "Nieprawidłowy ciąg HEX"
                        }
                    },
                    placeholder = { Text("np. 004ABC21", color = TechTextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TechBlackPure,
                        unfocusedContainerColor = TechBlackPure,
                        focusedBorderColor = TechWhite,
                        unfocusedBorderColor = TechWhiteBorderSubtle,
                        cursorColor = TechOrange
                    )
                )

                if (convertedDec.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TechBlackPure,
                        border = BorderStroke(1.dp, TechGreen.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = convertedDec,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TechGreen
                            )
                            IconButton(
                                onClick = {
                                    RccUtils.copyToClipboard(context, "Konwersja RFID DEC", convertedDec)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kopiuj",
                                    tint = TechGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // LIST OF SYSTEM GUIDES (Bento Cards)
        Text(
            text = "CENTRALE CYFROWE & ZASADY WYBIERANIA:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = TechWhite.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        guides.forEach { guide ->
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = TechSurfaceElevated,
                border = BorderStroke(1.dp, TechWhiteBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = guide.brand.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TechOrange
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TechBlackPure,
                            border = BorderStroke(1.dp, TechWhiteBorderSubtle)
                        ) {
                            Text(
                                text = guide.model,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TechTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Formula Bento pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TechBlackPure,
                        border = BorderStroke(1.dp, TechWhiteBorderAlpha),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "FORMUŁA OTWARCIA:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = TechWhite.copy(alpha = 0.5f)
                            )
                            Text(
                                text = guide.openFormula,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechWhite
                            )
                        }
                    }

                    Text(
                        text = "• ${guide.installerDefaultCode}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TechOrangeLight
                    )

                    Text(
                        text = "• RFID: ${guide.rfidFormat}",
                        fontSize = 11.sp,
                        color = TechCyan
                    )

                    Text(
                        text = guide.notes,
                        fontSize = 11.sp,
                        color = TechTextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
