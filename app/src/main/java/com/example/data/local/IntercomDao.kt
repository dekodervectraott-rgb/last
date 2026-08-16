package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IntercomEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface IntercomDao {

    @Query("SELECT * FROM intercom_entries ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllEntries(): Flow<List<IntercomEntry>>

    @Query("SELECT * FROM intercom_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: Long): Flow<IntercomEntry?>

    @Query("SELECT * FROM intercom_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryByIdDirect(id: Long): IntercomEntry?

    @Query("""
        SELECT * FROM intercom_entries 
        WHERE street LIKE '%' || :query || '%' 
           OR district LIKE '%' || :query || '%' 
           OR blockNumber LIKE '%' || :query || '%' 
           OR intercomCode LIKE '%' || :query || '%' 
           OR rfidCode LIKE '%' || :query || '%' 
           OR receiver LIKE '%' || :query || '%' 
           OR powerSupply LIKE '%' || :query || '%' 
           OR note LIKE '%' || :query || '%' 
        ORDER BY isFavorite DESC, updatedAt DESC
    """)
    fun searchEntries(query: String): Flow<List<IntercomEntry>>

    @Query("SELECT * FROM intercom_entries WHERE district = :district ORDER BY street ASC, blockNumber ASC")
    fun getEntriesByDistrict(district: String): Flow<List<IntercomEntry>>

    @Query("SELECT DISTINCT district FROM intercom_entries WHERE district != '' ORDER BY district ASC")
    fun getAllDistricts(): Flow<List<String>>

    @Query("SELECT * FROM intercom_entries WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteEntries(): Flow<List<IntercomEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: IntercomEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<IntercomEntry>)

    @Update
    suspend fun updateEntry(entry: IntercomEntry)

    @Delete
    suspend fun deleteEntry(entry: IntercomEntry)

    @Query("DELETE FROM intercom_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("DELETE FROM intercom_entries")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM intercom_entries")
    suspend fun getCount(): Int
}
