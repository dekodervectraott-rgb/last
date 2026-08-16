package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import com.example.data.model.IntercomEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RccUtils {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)

        vibrate(context, 40)
        Toast.makeText(context, "Skopiowano $label: $text", Toast.LENGTH_SHORT).show()
    }

    fun vibrate(context: Context, milliseconds: Long = 50) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(milliseconds)
                }
            }
        } catch (_: Exception) {}
    }

    var isFlashlightOn: Boolean = false
        private set

    fun toggleFlashlight(context: Context): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(
                    android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
                ) == true
            }
            if (cameraId != null) {
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(cameraId, isFlashlightOn)
                isFlashlightOn
            } else {
                false
            }
        } catch (e: Exception) {
            isFlashlightOn = false
            false
        }
    }

    fun exportToJson(entries: List<IntercomEntry>): String {
        val listType = Types.newParameterizedType(List::class.java, IntercomEntry::class.java)
        val adapter = moshi.adapter<List<IntercomEntry>>(listType).indent("  ")
        return adapter.toJson(entries)
    }

    fun importFromJson(jsonString: String): List<IntercomEntry>? {
        return try {
            val listType = Types.newParameterizedType(List::class.java, IntercomEntry::class.java)
            val adapter = moshi.adapter<List<IntercomEntry>>(listType)
            adapter.fromJson(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun exportToCsv(entries: List<IntercomEntry>): String {
        val sb = StringBuilder()
        // Header in Polish
        sb.append("Dzielnica;Ulica;NumerBloku;NumerKlatki;Pietro;Zasilacz;Odbiornik;KodDomofonu;KodRFID;TypRFID;Notatka;SzerokoscGPS;DlugoscGPS;Ulubione\n")
        for (entry in entries) {
            sb.append(escapeCsv(entry.district)).append(";")
                .append(escapeCsv(entry.street)).append(";")
                .append(escapeCsv(entry.blockNumber)).append(";")
                .append(escapeCsv(entry.staircaseNumber)).append(";")
                .append(escapeCsv(entry.floor)).append(";")
                .append(escapeCsv(entry.powerSupply)).append(";")
                .append(escapeCsv(entry.receiver)).append(";")
                .append(escapeCsv(entry.intercomCode)).append(";")
                .append(escapeCsv(entry.rfidCode)).append(";")
                .append(escapeCsv(entry.rfidType)).append(";")
                .append(escapeCsv(entry.note)).append(";")
                .append(entry.latitude?.toString() ?: "").append(";")
                .append(entry.longitude?.toString() ?: "").append(";")
                .append(if (entry.isFavorite) "1" else "0")
                .append("\n")
        }
        return sb.toString()
    }

    private fun escapeCsv(text: String): String {
        val clean = text.replace("\"", "\"\"")
        return if (clean.contains(";") || clean.contains("\n") || clean.contains("\"")) {
            "\"$clean\""
        } else {
            clean
        }
    }

    fun importFromCsv(csvText: String): List<IntercomEntry> {
        val result = mutableListOf<IntercomEntry>()
        val lines = csvText.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        for (i in 1 until lines.size) {
            val line = lines[i]
            val tokens = parseCsvLine(line)
            if (tokens.size >= 8) {
                val entry = IntercomEntry(
                    district = tokens.getOrNull(0) ?: "",
                    street = tokens.getOrNull(1) ?: "",
                    blockNumber = tokens.getOrNull(2) ?: "",
                    staircaseNumber = tokens.getOrNull(3) ?: "",
                    floor = tokens.getOrNull(4) ?: "",
                    powerSupply = tokens.getOrNull(5) ?: "",
                    receiver = tokens.getOrNull(6) ?: "",
                    intercomCode = tokens.getOrNull(7) ?: "",
                    rfidCode = tokens.getOrNull(8) ?: "",
                    rfidType = tokens.getOrNull(9).takeIf { !it.isNullOrBlank() } ?: "RFID 125kHz",
                    note = tokens.getOrNull(10) ?: "",
                    latitude = tokens.getOrNull(11)?.toDoubleOrNull(),
                    longitude = tokens.getOrNull(12)?.toDoubleOrNull(),
                    isFavorite = tokens.getOrNull(13) == "1" || tokens.getOrNull(13).equals("true", ignoreCase = true)
                )
                result.add(entry)
            }
        }
        return result
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ';' && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    fun getSampleInstallations(): List<IntercomEntry> {
        return listOf(
            IntercomEntry(
                district = "Mokotów",
                street = "Puławska",
                blockNumber = "42",
                staircaseNumber = "1",
                floor = "4",
                powerSupply = "ZAS-12V/2A w szachcie piwnicy (bezpiecznik B6)",
                receiver = "Laskomex LM-8 (Unifon cyfrowy)",
                intercomCode = "KLUCZ 14 2580",
                rfidCode = "0008459201",
                rfidType = "RFID 125kHz EM4100",
                note = "Wejście od podwórza. Klucz master do śmietnika: 1122. Kod do bramy wjazdowej: 9988.",
                latitude = 52.2014,
                longitude = 21.0235,
                isFavorite = true
            ),
            IntercomEntry(
                district = "Śródmieście",
                street = "Marszałkowska",
                blockNumber = "115",
                staircaseNumber = "A",
                floor = "Parter",
                powerSupply = "ACO TR-12V pod centralą w tablicy głównej",
                receiver = "ACO Familio / INS-UP",
                intercomCode = "# 25 7391",
                rfidCode = "01:A4:B2:89:FE",
                rfidType = "Dallas iButton DS1990A",
                note = "Zarządca: Spółdzielnia Centrum. Zasilacz wymieniony 03.2025. Odbiornik ustawiony na adres fizyczny 25.",
                latitude = 52.2319,
                longitude = 21.0067,
                isFavorite = true
            ),
            IntercomEntry(
                district = "Wola",
                street = "Kasprzaka",
                blockNumber = "18/20",
                staircaseNumber = "2",
                floor = "2",
                powerSupply = "Impulsowy 15V DC w szafie RACK klatka 2",
                receiver = "Cyfral Smart-D",
                intercomCode = "K12 3412",
                rfidCode = "A3:5F:9C:12",
                rfidType = "Mifare 13.56MHz Classic",
                note = "Dioda zasilania na płycie świeci na zielono. Wyłącznik antysabotażowy sprawny.",
                latitude = 52.2285,
                longitude = 20.9654,
                isFavorite = false
            ),
            IntercomEntry(
                district = "Ursynów",
                street = "Al. KEN",
                blockNumber = "54",
                staircaseNumber = "B",
                floor = "6",
                powerSupply = "ZAS-15V na szynie DIN w rozdzielnicy parter",
                receiver = "Proel PC-512",
                intercomCode = "KLUCZ 48 9012",
                rfidCode = "0012948172",
                rfidType = "RFID 125kHz EM4100",
                note = "Klatka z windą. Kod administratora serwisowego: 9999K. Bramka boczna otwiera się tym samym brelokiem.",
                latitude = 52.1485,
                longitude = 21.0452,
                isFavorite = false
            ),
            IntercomEntry(
                district = "Praga-Południe",
                street = "Grochowska",
                blockNumber = "214",
                staircaseNumber = "3",
                floor = "1",
                powerSupply = "Trafo 12V AC w piwnicy pod klatką 3",
                receiver = "Urmet Matibus SE / 1132",
                intercomCode = "104 K 8821",
                rfidCode = "01:F8:77:AA:99",
                rfidType = "Dallas iButton DS1990A",
                note = "Rygiel elektromagnetyczny rewersyjny. Odbiornik z funkcją wyciszenia dzwonka.",
                latitude = 52.2472,
                longitude = 21.0841,
                isFavorite = false
            )
        )
    }
}
