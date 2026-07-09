package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "simulation_history")
data class SimulationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameName: String,
    val pingMs: Int,
    val packetLossPercent: Int,
    val jitterMs: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reflex_scores")
data class ReflexScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameName: String,
    val delayMs: Int,
    val responseTimeMs: Int, // Response time in ms
    val result: String, // "SUCCESS", "FAILED" (clicked wrong), "PACKET_LOSS" (click missed due to loss)
    val timestamp: Long = System.currentTimeMillis(),
    val kills: Int = 0,
    val deaths: Int = 0,
    val targetsHit: Int = 0,
    val latencyMs: Int = 0
)

@Dao
interface AppDao {
    @Query("SELECT * FROM simulation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<SimulationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: SimulationHistory)

    @Query("DELETE FROM simulation_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM reflex_scores ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<ReflexScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ReflexScore)

    @Query("DELETE FROM reflex_scores")
    suspend fun clearScores()
}

@Database(entities = [SimulationHistory::class, ReflexScore::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}

class AppRepository(private val appDao: AppDao) {
    val allHistory: Flow<List<SimulationHistory>> = appDao.getAllHistory()
    val allScores: Flow<List<ReflexScore>> = appDao.getAllScores()

    suspend fun insertHistory(history: SimulationHistory) = appDao.insertHistory(history)
    suspend fun clearHistory() = appDao.clearHistory()

    suspend fun insertScore(score: ReflexScore) = appDao.insertScore(score)
    suspend fun clearScores() = appDao.clearScores()
}
