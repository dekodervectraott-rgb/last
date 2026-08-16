package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "intercom_entries")
data class IntercomEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val district: String = "",         // Dzielnica (np. Mokotów, Śródmieście)
    val street: String = "",           // Ulica (np. Marszałkowska, Piotrkowska)
    val blockNumber: String = "",      // Numer bloku / budynku (np. 14, 25B)
    val staircaseNumber: String = "",  // Numer klatki (np. 1, A, II)
    val floor: String = "",            // Piętro (np. 3, Parter, 0)
    val powerSupply: String = "",      // Zasilacz (np. ZAS-12V/2A w szachcie piwnicy)
    val receiver: String = "",         // Odbiornik (np. Laskomex LM-8, Cyfral Smart)
    val intercomCode: String = "",     // Numer kodu do wpisania (np. Klucz 1234, 15K2580)
    val rfidCode: String = "",         // Pastylka RFID kod pastylki (np. 0008459201 lub Dallas 01:A2:3B:4C)
    val rfidType: String = "RFID 125kHz", // Typ pastylki (Dallas iButton, 125kHz EM4100, Mifare 13.56MHz)
    val note: String = "",             // Notatka (np. Wejście od podwórza, kod śmietnika 9988)
    val latitude: Double? = null,      // GPS szerokość geograficzna
    val longitude: Double? = null,     // GPS długość geograficzna
    val isFavorite: Boolean = false,   // Ulubione
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val cloudSyncStatus: String = "LOCAL", // "LOCAL", "SYNCED", "PENDING"
    val cloudId: String? = null
)
