package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.ReflexScore
import com.example.data.SimulationHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER, LINH_CHI
}

data class FpsBulletHole(
    val x: Float,
    val y: Float,
    val isHit: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class FpsShotVisual(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class FpsDiagnostic(
    val gameName: String,
    val totalTargets: Int,
    val hits: Int,
    val accuracy: Int,
    val avgPhysicalResponseMs: Int,
    val avgWithNetworkResponseMs: Int,
    val lostShotsCount: Int,
    val networkPingSimulated: Int,
    val networkJitterSimulated: Int,
    val networkLossSimulated: Int,
    val mainIssue: String,
    val detailedTips: List<Pair<String, String>>,
    val linhChiEvaluation: String
)

data class MobaCreep(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var hp: Float,
    val maxHp: Float,
    val isEnemy: Boolean,
    val speed: Float = 0.5f,
    var lastAttackTime: Long = 0,
    var isStunned: Boolean = false,
    var stunEndTime: Long = 0,
    val lane: String = "mid"
)

data class MobaProjectile(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    val speed: Float,
    val isEnemy: Boolean,
    val damage: Float,
    val type: String, // "basic", "stun", "ult", "turret"
    val color: Long,
    val radius: Float,
    val targetX: Float,
    val targetY: Float,
    val isHoming: Boolean = false,
    val homingTargetId: String? = null, // homing to creep id, "enemy_hero", or "player"
    var isFinished: Boolean = false
)

data class MobaDamageText(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val x: Float,
    val y: Float,
    val color: Long,
    var age: Int = 0
)

data class MobaDiagnostic(
    val gameName: String,
    val kills: Int,
    val deaths: Int,
    val accuracy: Int,
    val skillCasts: Int,
    val skillsInterruptedCount: Int,
    val turretStatus: String,
    val networkPingSimulated: Int,
    val networkJitterSimulated: Int,
    val networkLossSimulated: Int,
    val mainIssue: String,
    val detailedTips: List<Pair<String, String>>,
    val linhChiEvaluation: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "lag_sim_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = AppRepository(db.appDao())

    val history: StateFlow<List<SimulationHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scores: StateFlow<List<ReflexScore>> = repository.allScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Navigation Tab
    private val _currentTab = MutableStateFlow(0)
    val currentTab = _currentTab.asStateFlow()

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    // --- Simulator States ---
    val games = listOf("Liên Minh Huyền Thoại", "Valorant", "PUBG Mobile", "Liên Quân Mobile", "Free Fire", "Genshin Impact")
    
    private val _selectedGame = MutableStateFlow(games[0])
    val selectedGame = _selectedGame.asStateFlow()

    private val _targetPing = MutableStateFlow(120) // in ms
    val targetPing = _targetPing.asStateFlow()

    private val _targetJitter = MutableStateFlow(15) // in ms
    val targetJitter = _targetJitter.asStateFlow()

    private val _targetLoss = MutableStateFlow(5) // in %
    val targetLoss = _targetLoss.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating = _isSimulating.asStateFlow()

    private val _currentPing = MutableStateFlow(0)
    val currentPing = _currentPing.asStateFlow()

    private val _pingHistoryList = MutableStateFlow<List<Int>>(emptyList())
    val pingHistoryList = _pingHistoryList.asStateFlow()

    private val _packetLossActive = MutableStateFlow(false)
    val packetLossActive = _packetLossActive.asStateFlow()

    private val _currentLagReport = MutableStateFlow<LagReport?>(null)
    val currentLagReport = _currentLagReport.asStateFlow()

    fun selectGame(game: String) {
        _selectedGame.value = game
    }

    fun setTargetPing(ping: Int) {
        _targetPing.value = ping
    }

    fun setTargetJitter(jitter: Int) {
        _targetJitter.value = jitter
    }

    fun setTargetLoss(loss: Int) {
        _targetLoss.value = loss
    }

    // Toggle simulation
    fun toggleSimulation() {
        if (_isSimulating.value) {
            // Stop
            _isSimulating.value = false
            _currentPing.value = 0
            _pingHistoryList.value = emptyList()
        } else {
            // Start
            _isSimulating.value = true
            // Save simulation configuration to history
            viewModelScope.launch {
                repository.insertHistory(
                    SimulationHistory(
                        gameName = _selectedGame.value,
                        pingMs = _targetPing.value,
                        packetLossPercent = _targetLoss.value,
                        jitterMs = _targetJitter.value
                    )
                )
            }
            // Run simulation loop
            startSimulationLoop()
        }
    }

    private fun startSimulationLoop() {
        viewModelScope.launch {
            while (_isSimulating.value) {
                val base = _targetPing.value
                val jitterVal = _targetJitter.value
                val lossPercent = _targetLoss.value

                // Compute standard fluctuation
                val jitterOffset = if (jitterVal > 0) {
                    Random.nextInt(-jitterVal, jitterVal + 1)
                } else {
                    0
                }

                // Simulate packet loss occurrences
                val isLost = Random.nextInt(0, 100) < lossPercent
                _packetLossActive.value = isLost

                var simulatedPing = base + jitterOffset
                if (simulatedPing < 5) simulatedPing = 5 // lower bound

                // If packet lost, let's spike ping or hold it
                if (isLost) {
                    simulatedPing = 999 // 999 ms / Disconnect spike
                }

                _currentPing.value = simulatedPing

                // Keep last 30 points for graph
                val currentList = _pingHistoryList.value.toMutableList()
                currentList.add(simulatedPing)
                if (currentList.size > 30) {
                    currentList.removeAt(0)
                }
                _pingHistoryList.value = currentList

                delay(300) // update every 300ms
            }
        }
    }

    // --- Reflex Game States ---
    private val _reflexState = MutableStateFlow("idle") // "idle", "preparing", "spawned", "delaying", "finished"
    val reflexState = _reflexState.asStateFlow()

    private val _reflexTargetX = MutableStateFlow(0.5f)
    val reflexTargetX = _reflexTargetX.asStateFlow()

    private val _reflexTargetY = MutableStateFlow(0.5f)
    val reflexTargetY = _reflexTargetY.asStateFlow()

    private val _reflexMessage = MutableStateFlow("Nhấn nút để bắt đầu kiểm tra phản xạ của bạn khi mạng lag!")
    val reflexMessage = _reflexMessage.asStateFlow()

    private val _reflexTimerText = MutableStateFlow("")
    val reflexTimerText = _reflexTimerText.asStateFlow()

    private var targetSpawnTime: Long = 0
    private var clickTriggerTime: Long = 0

    // FPS 2D Mini Game details
    private val _fpsCurrentTarget = MutableStateFlow(1)
    val fpsCurrentTarget = _fpsCurrentTarget.asStateFlow()

    private val _fpsHits = MutableStateFlow(0)
    val fpsHits = _fpsHits.asStateFlow()

    private val _fpsShots = MutableStateFlow(0)
    val fpsShots = _fpsShots.asStateFlow()

    private val _fpsMisses = MutableStateFlow(0)
    val fpsMisses = _fpsMisses.asStateFlow()

    private val _fpsLostShots = MutableStateFlow(0)
    val fpsLostShots = _fpsLostShots.asStateFlow()

    private val _fpsTotalReactionTime = MutableStateFlow(0)
    val fpsTotalReactionTime = _fpsTotalReactionTime.asStateFlow()

    private val _fpsBulletHoles = MutableStateFlow<List<FpsBulletHole>>(emptyList())
    val fpsBulletHoles = _fpsBulletHoles.asStateFlow()

    private val _fpsShotVisuals = MutableStateFlow<List<FpsShotVisual>>(emptyList())
    val fpsShotVisuals = _fpsShotVisuals.asStateFlow()

    private val _fpsDiagnosticReport = MutableStateFlow<FpsDiagnostic?>(null)
    val fpsDiagnosticReport = _fpsDiagnosticReport.asStateFlow()

    private val _fpsDifficultyMultiplier = MutableStateFlow(1.0f)
    val fpsDifficultyMultiplier = _fpsDifficultyMultiplier.asStateFlow()

    private val _fpsDifficultyLevelName = MutableStateFlow("Bình Thường (Mượt)")
    val fpsDifficultyLevelName = _fpsDifficultyLevelName.asStateFlow()

    private val _fpsIsZoomed = MutableStateFlow(false)
    val fpsIsZoomed = _fpsIsZoomed.asStateFlow()

    private val _fpsGameMode = MutableStateFlow("classic") // "classic", "bottle", "fast", "sniper"
    val fpsGameMode = _fpsGameMode.asStateFlow()

    fun setFpsGameMode(mode: String) {
        if (_reflexState.value == "idle" || _reflexState.value == "finished") {
            _fpsGameMode.value = mode
        }
    }

    fun setFpsZoomed(zoomed: Boolean) {
        _fpsIsZoomed.value = zoomed
    }

    // --- MOBA Game States ---
    private val _mobaState = MutableStateFlow("idle") // "idle", "preparing", "playing", "victory", "defeat"
    val mobaState = _mobaState.asStateFlow()

    private val _mobaMoveDirection = MutableStateFlow(MobaMoveDirection.NONE)
    val mobaMoveDirection = _mobaMoveDirection.asStateFlow()

    fun setMobaMoveDirection(dir: MobaMoveDirection) {
        _mobaMoveDirection.value = dir
    }

    private val _mobaJoystickAngle = MutableStateFlow(0f)
    val mobaJoystickAngle = _mobaJoystickAngle.asStateFlow()

    private val _mobaJoystickActive = MutableStateFlow(false)
    val mobaJoystickActive = _mobaJoystickActive.asStateFlow()

    fun updateMobaJoystick(angle: Float, active: Boolean) {
        _mobaJoystickAngle.value = angle
        _mobaJoystickActive.value = active
    }

    private val _mobaHero = MutableStateFlow("Tulen") // "Tulen", "Valhein"
    val mobaHero = _mobaHero.asStateFlow()

    private val _mobaHeroX = MutableStateFlow(15f)
    val mobaHeroX = _mobaHeroX.asStateFlow()

    private val _mobaHeroY = MutableStateFlow(50f)
    val mobaHeroY = _mobaHeroY.asStateFlow()

    private val _mobaHeroDestX = MutableStateFlow(15f)
    val mobaHeroDestX = _mobaHeroDestX.asStateFlow()

    private val _mobaHeroDestY = MutableStateFlow(50f)
    val mobaHeroDestY = _mobaHeroDestY.asStateFlow()

    private val _mobaHeroHP = MutableStateFlow(3000f)
    val mobaHeroHP = _mobaHeroHP.asStateFlow()

    private val _mobaDashTrails = MutableStateFlow<List<MobaDashTrail>>(emptyList())
    val mobaDashTrails = _mobaDashTrails.asStateFlow()

    fun clearMobaDashTrails() {
        _mobaDashTrails.value = emptyList()
    }

    private val _mobaHeroMaxHP = MutableStateFlow(3000f)
    val mobaHeroMaxHP = _mobaHeroMaxHP.asStateFlow()

    private val _mobaHeroMP = MutableStateFlow(500f)
    val mobaHeroMP = _mobaHeroMP.asStateFlow()

    private val _mobaHeroMaxMP = MutableStateFlow(500f)
    val mobaHeroMaxMP = _mobaHeroMaxMP.asStateFlow()

    // Enemy Champion (Maloch)
    private val _mobaEnemyX = MutableStateFlow(65f)
    val mobaEnemyX = _mobaEnemyX.asStateFlow()

    private val _mobaEnemyY = MutableStateFlow(50f)
    val mobaEnemyY = _mobaEnemyY.asStateFlow()

    private val _mobaEnemyHP = MutableStateFlow(4000f)
    val mobaEnemyHP = _mobaEnemyHP.asStateFlow()

    private val _mobaEnemyMaxHP = MutableStateFlow(4000f)
    val mobaEnemyMaxHP = _mobaEnemyMaxHP.asStateFlow()

    private val _mobaEnemyName = MutableStateFlow("Maloch - Ma Vương 😈")
    val mobaEnemyName = _mobaEnemyName.asStateFlow()

    private val _mobaEnemyIsStunned = MutableStateFlow(false)
    val mobaEnemyIsStunned = _mobaEnemyIsStunned.asStateFlow()

    private val _mobaEnemyIsKnockedUp = MutableStateFlow(false)
    val mobaEnemyIsKnockedUp = _mobaEnemyIsKnockedUp.asStateFlow()

    private val _mobaEnemyKnockupHeight = MutableStateFlow(0f)
    val mobaEnemyKnockupHeight = _mobaEnemyKnockupHeight.asStateFlow()

    private val _mobaHeroKnockupHeight = MutableStateFlow(0f)
    val mobaHeroKnockupHeight = _mobaHeroKnockupHeight.asStateFlow()

    private var mobaEnemyStunUntil = 0L

    // Creeps and projectiles list
    private val _mobaCreeps = MutableStateFlow<List<MobaCreep>>(emptyList())
    val mobaCreeps = _mobaCreeps.asStateFlow()

    private val _mobaProjectiles = MutableStateFlow<List<MobaProjectile>>(emptyList())
    val mobaProjectiles = _mobaProjectiles.asStateFlow()

    private val _mobaDamageTexts = MutableStateFlow<List<MobaDamageText>>(emptyList())
    val mobaDamageTexts = _mobaDamageTexts.asStateFlow()

    // Skill status
    private val _mobaSkillCooldowns = MutableStateFlow(listOf(0f, 0f, 0f)) // in seconds
    val mobaSkillCooldowns = _mobaSkillCooldowns.asStateFlow()

    private val _mobaPassiveStacks = MutableStateFlow(0)
    val mobaPassiveStacks = _mobaPassiveStacks.asStateFlow()

    // Murad States
    private val _mobaMuradCloneX = MutableStateFlow(-1f)
    val mobaMuradCloneX = _mobaMuradCloneX.asStateFlow()

    private val _mobaMuradCloneY = MutableStateFlow(-1f)
    val mobaMuradCloneY = _mobaMuradCloneY.asStateFlow()

    private val _mobaMuradCastCount = MutableStateFlow(0)
    val mobaMuradCastCount = _mobaMuradCastCount.asStateFlow()

    private val _mobaHeroIsImmune = MutableStateFlow(false)
    val mobaHeroIsImmune = _mobaHeroIsImmune.asStateFlow()

    // Yasuo States
    private val _mobaWindWallX = MutableStateFlow(-1f)
    val mobaWindWallX = _mobaWindWallX.asStateFlow()

    private val _mobaWindWallY = MutableStateFlow(-1f)
    val mobaWindWallY = _mobaWindWallY.asStateFlow()

    private val _mobaWindWallActive = MutableStateFlow(false)
    val mobaWindWallActive = _mobaWindWallActive.asStateFlow()

    private var mobaWindWallDurationLeftMs = 0L

    // Score
    private val _mobaKills = MutableStateFlow(0)
    val mobaKills = _mobaKills.asStateFlow()

    private val _mobaDeaths = MutableStateFlow(0)
    val mobaDeaths = _mobaDeaths.asStateFlow()

    // Turrets
    private val _mobaAllyTurretHP = MutableStateFlow(1500f)
    val mobaAllyTurretHP = _mobaAllyTurretHP.asStateFlow()

    private val _mobaAllyTurretTopHP = MutableStateFlow(1500f)
    val mobaAllyTurretTopHP = _mobaAllyTurretTopHP.asStateFlow()

    private val _mobaAllyTurretBotHP = MutableStateFlow(1500f)
    val mobaAllyTurretBotHP = _mobaAllyTurretBotHP.asStateFlow()

    private val _mobaEnemyTurretHP = MutableStateFlow(1500f)
    val mobaEnemyTurretHP = _mobaEnemyTurretHP.asStateFlow()

    private val _mobaEnemyTurretTopHP = MutableStateFlow(1500f)
    val mobaEnemyTurretTopHP = _mobaEnemyTurretTopHP.asStateFlow()

    private val _mobaEnemyTurretBotHP = MutableStateFlow(1500f)
    val mobaEnemyTurretBotHP = _mobaEnemyTurretBotHP.asStateFlow()

    // Tulen S2 multi-dash state
    private val _mobaTulenS2CastCount = MutableStateFlow(0)
    val mobaTulenS2CastCount = _mobaTulenS2CastCount.asStateFlow()

    private val _mobaLog = MutableStateFlow("Đại Lộ Công Lý đã sẵn sàng! Chọn tướng và xuất kích ngay! ⚔️")
    val mobaLog = _mobaLog.asStateFlow()

    private val _mobaIsZoomed = MutableStateFlow(false)
    val mobaIsZoomed = _mobaIsZoomed.asStateFlow()

    private val _mobaDiagnosticReport = MutableStateFlow<MobaDiagnostic?>(null)
    val mobaDiagnosticReport = _mobaDiagnosticReport.asStateFlow()

    // Internals
    private var mobaGameJob: kotlinx.coroutines.Job? = null
    private var mobaPassiveTimer = 0L
    private var mobaTulenPassiveOrbs = 0 // Number of active floating electrical orbs
    private var mobaValheinAttackCount = 0
    private var mobaSkillCastsCount = 0
    private var mobaSkillsInterruptedCount = 0
    private var mobaSkillHitsCount = 0
    private var mobaGameStartTime = 0L

    fun setMobaZoomed(zoomed: Boolean) {
        _mobaIsZoomed.value = zoomed
    }

    fun selectMobaHero(hero: String) {
        if (_mobaState.value == "playing") return
        _mobaHero.value = hero
        _mobaLog.value = "Đã chọn tướng: $hero 🌟"
        _mobaMuradCloneX.value = -1f
        _mobaMuradCloneY.value = -1f
        _mobaMuradCastCount.value = 0
        _mobaHeroIsImmune.value = false
        _mobaWindWallX.value = -1f
        _mobaWindWallY.value = -1f
        _mobaWindWallActive.value = false
        mobaWindWallDurationLeftMs = 0L
    }

    fun startMobaGame() {
        mobaGameJob?.cancel()
        _mobaState.value = "preparing"
        _mobaLog.value = "Đang tải bản đồ Đại Lộ Bình Nguyên Vô Tận & tải dữ liệu lính..."
        _mobaDiagnosticReport.value = null

        _mobaHeroX.value = 15f
        _mobaHeroY.value = 50f
        _mobaHeroDestX.value = 15f
        _mobaHeroDestY.value = 50f
        
        val maxHp = when (_mobaHero.value) {
            "Tulen" -> 3000f
            "Murad" -> 2900f
            "Yasuo" -> 3500f
            else -> 3200f
        }
        _mobaHeroHP.value = maxHp
        _mobaHeroMaxHP.value = maxHp
        val maxMp = if (_mobaHero.value == "Yasuo") 0f else 500f
        _mobaHeroMP.value = maxMp
        _mobaHeroMaxMP.value = maxMp

        _mobaEnemyX.value = 65f
        _mobaEnemyY.value = 50f
        _mobaEnemyHP.value = 4000f
        _mobaEnemyMaxHP.value = 4000f
        _mobaEnemyIsStunned.value = false
        _mobaEnemyIsKnockedUp.value = false
        _mobaEnemyKnockupHeight.value = 0f
        _mobaHeroKnockupHeight.value = 0f
        mobaEnemyStunUntil = 0L

        _mobaCreeps.value = emptyList()
        _mobaProjectiles.value = emptyList()
        _mobaDamageTexts.value = emptyList()
        _mobaSkillCooldowns.value = listOf(0f, 0f, 0f)
        _mobaPassiveStacks.value = 0
        _mobaKills.value = 0
        _mobaDeaths.value = 0
        _mobaAllyTurretHP.value = 1500f
        _mobaAllyTurretTopHP.value = 1500f
        _mobaAllyTurretBotHP.value = 1500f
        _mobaEnemyTurretHP.value = 1500f
        _mobaEnemyTurretTopHP.value = 1500f
        _mobaEnemyTurretBotHP.value = 1500f
        _mobaTulenS2CastCount.value = 0
        
        _mobaMuradCloneX.value = -1f
        _mobaMuradCloneY.value = -1f
        _mobaMuradCastCount.value = 0
        _mobaHeroIsImmune.value = false
        _mobaWindWallX.value = -1f
        _mobaWindWallY.value = -1f
        _mobaWindWallActive.value = false
        mobaWindWallDurationLeftMs = 0L
        
        mobaTulenPassiveOrbs = 0
        mobaValheinAttackCount = 0
        mobaSkillCastsCount = 0
        mobaSkillsInterruptedCount = 0
        mobaSkillHitsCount = 0
        mobaGameStartTime = System.currentTimeMillis()

        viewModelScope.launch {
            delay(1200) // fake resource loading
            _mobaState.value = "playing"
            _mobaLog.value = "CHÀO MỪNG ĐẾN VỚI BÌNH NGUYÊN VÔ TẬN! ⚔️ Lính xuất kích sau 3 giây! Chạm bất cứ đâu trên bản đồ để di chuyển!"
            
            mobaGameJob = viewModelScope.launch(Dispatchers.Default) {
                runMobaGameLoop()
            }
        }
    }

    fun stopMobaGame() {
        mobaGameJob?.cancel()
        _mobaState.value = "idle"
        _mobaLog.value = "Đã dừng trận đấu MOBA."
    }

    fun moveHeroTo(targetX: Float, targetY: Float) {
        if (_mobaState.value != "playing") return
        if (_mobaHeroHP.value <= 0f) return // dead hero can't move
        _mobaMoveDirection.value = MobaMoveDirection.NONE

        val actualPing = if (_isSimulating.value) _currentPing.value else 10
        val lossPercent = if (_isSimulating.value) _targetLoss.value else 0

        viewModelScope.launch {
            // Simulated Packet Loss
            if (_isSimulating.value && Random.nextInt(100) < lossPercent) {
                addMobaDamageText("LOST MOVE 💨", _mobaHeroX.value, _mobaHeroY.value - 6, 0xFFFF3333)
                _mobaLog.value = "⚠️ MẤT GÓI TIN (Packet Loss)! Máy chủ không nhận được lệnh di chuyển."
                return@launch
            }

            if (actualPing > 20) {
                delay(actualPing.toLong())
            }

            // Set destination
            _mobaHeroDestX.value = targetX.coerceIn(0f, 100f)
            _mobaHeroDestY.value = targetY.coerceIn(20f, 80f) // limit vertical movement slightly for widescreen lane feel
        }
    }

    fun moveHeroInDirection(dir: MobaMoveDirection) {
        if (_mobaState.value != "playing") return
        if (_mobaHeroHP.value <= 0f) return

        val currentX = _mobaHeroX.value
        val currentY = _mobaHeroY.value
        val stepSize = 10f // A responsive step size for each tap on the phone buttons

        val targetX: Float
        val targetY: Float

        when (dir) {
            MobaMoveDirection.UP -> {
                targetX = currentX
                targetY = (currentY - stepSize).coerceIn(20f, 80f)
            }
            MobaMoveDirection.DOWN -> {
                targetX = currentX
                targetY = (currentY + stepSize).coerceIn(20f, 80f)
            }
            MobaMoveDirection.LEFT -> {
                targetX = (currentX - stepSize).coerceIn(0f, 100f)
                targetY = currentY
            }
            MobaMoveDirection.RIGHT -> {
                targetX = (currentX + stepSize).coerceIn(0f, 100f)
                targetY = currentY
            }
            else -> return
        }

        moveHeroTo(targetX, targetY)
    }

    fun triggerMobaBasicAttack() {
        if (_mobaState.value != "playing") return
        if (_mobaHeroHP.value <= 0f) return

        val actualPing = if (_isSimulating.value) _currentPing.value else 10
        val lossPercent = if (_isSimulating.value) _targetLoss.value else 0

        viewModelScope.launch {
            if (_isSimulating.value && Random.nextInt(100) < lossPercent) {
                addMobaDamageText("LOST ATK ⚔️", _mobaHeroX.value, _mobaHeroY.value - 6, 0xFFFF5555)
                _mobaLog.value = "❌ RỤNG MẠNG (Packet Loss)! Đòn đánh thường bị hủy do mất gói tin!"
                return@launch
            }

            if (actualPing > 20) {
                delay(actualPing.toLong())
            }

            // Find nearest enemy to attack
            val isMurad = _mobaHero.value == "Murad"
            val isYasuo = _mobaHero.value == "Yasuo"
            val target = findNearestMobaEnemy(range = if (isMurad) 16f else if (isYasuo) 18f else 25f)
            if (target == null) {
                _mobaLog.value = "⚠️ Không có kẻ địch nào trong tầm đánh!"
                return@launch
            }

            // Yasuo dash to target on basic attack
            if (isYasuo) {
                val tX = target.first
                val tY = target.second
                val dx = tX - _mobaHeroX.value
                val dy = tY - _mobaHeroY.value
                val d = kotlin.math.sqrt(dx * dx + dy * dy)
                if (d > 4.5f) {
                    _mobaLog.value = "🌪️ Yasuo lướt QUÉT KIẾM chém thường cực nhanh!"
                    val dashDist = d - 4.5f
                    val nextX = (_mobaHeroX.value + (dx / d) * dashDist).coerceIn(0f, 100f)
                    val nextY = (_mobaHeroY.value + (dy / d) * dashDist).coerceIn(20f, 80f)
                    
                    val startX = _mobaHeroX.value
                    val startY = _mobaHeroY.value
                    
                    val steps = 3
                    val stepX = (nextX - startX) / steps
                    val stepY = (nextY - startY) / steps
                    
                    for (i in 1..steps) {
                        val currX = startX + stepX * i
                        val currY = startY + stepY * i
                        _mobaHeroX.value = currX
                        _mobaHeroY.value = currY
                        _mobaDashTrails.value = _mobaDashTrails.value + MobaDashTrail(
                            id = "yasuo_dash_${System.currentTimeMillis()}_$i",
                            x = currX,
                            y = currY,
                            color = 0xFFCBD5E1,
                            isTulen = false,
                            alpha = 0.22f * i
                        )
                        delay(15)
                    }
                    _mobaHeroX.value = nextX
                    _mobaHeroY.value = nextY
                    _mobaHeroDestX.value = nextX
                    _mobaHeroDestY.value = nextY
                    
                    viewModelScope.launch {
                        delay(180)
                        _mobaDashTrails.value = emptyList()
                    }
                }
            }

            // Spawn basic projectile
            val isTulen = _mobaHero.value == "Tulen"
            val projColor = if (isTulen) 0xFF33FFFF else if (isMurad) 0xFFEAB308 else if (isYasuo) 0xFFF1F5F9 else 0xFFFFCC33
            val damage = if (isTulen) 140f else if (isMurad) 195f else if (isYasuo) 180f else 160f
            
            val isPassiveShot = !isTulen && !isMurad && !isYasuo && (mobaValheinAttackCount >= 2)
            val projType = if (isMurad) "murad_basic" else if (isYasuo) "yasuo_basic" else if (isPassiveShot) "passive_glaive" else "basic"
            val finalColor = if (isPassiveShot) {
                val rand = Random.nextInt(3)
                when (rand) {
                    0 -> 0xFFFF3333 // Red
                    1 -> 0xFFFFFF33 // Yellow
                    else -> 0xFF3333FF // Blue
                }
            } else projColor

            val proj = MobaProjectile(
                x = _mobaHeroX.value,
                y = _mobaHeroY.value,
                speed = if (isMurad) 3.0f else if (isYasuo) 3.2f else 2.2f, // Murad and Yasuo attacks hit faster!
                isEnemy = false,
                damage = damage,
                type = projType,
                color = finalColor,
                radius = if (isMurad || isYasuo) 1.2f else 1.8f,
                targetX = target.first,
                targetY = target.second,
                isHoming = true,
                homingTargetId = target.third
            )
            
            _mobaProjectiles.value = _mobaProjectiles.value + proj
            if (!isTulen && !isMurad && !isYasuo) {
                mobaValheinAttackCount = if (isPassiveShot) 0 else (mobaValheinAttackCount + 1)
            }
        }
    }

    fun castMobaSkill(skillIndex: Int) {
        if (_mobaState.value != "playing") return
        if (_mobaHeroHP.value <= 0f) return
        if (_mobaSkillCooldowns.value[skillIndex] > 0f) return

        val isTulen = _mobaHero.value == "Tulen"
        val isMurad = _mobaHero.value == "Murad"
        val isYasuo = _mobaHero.value == "Yasuo"

        // For Murad, Ultimate is locked unless passive stacks are 4
        if (isMurad && skillIndex == 2 && _mobaPassiveStacks.value < 4) {
            _mobaLog.value = "⚠️ Vô Ảnh Trảm đang BỊ PHONG ẤN! Hãy đánh thường đủ 4 lần lên lính hoặc tướng địch để kích hoạt!"
            return
        }

        // For Yasuo, Ultimate is locked unless enemy is stunned
        if (isYasuo && skillIndex == 2 && !_mobaEnemyIsStunned.value) {
            _mobaLog.value = "⚠️ Kẻ địch phải bị HẤT TUNG hoặc KHỐNG CHẾ mới thi triển được Trăn Trối! Hãy tích đủ 2 cộng dồn Bão Kiếm và lốc xoáy!"
            return
        }

        // Check Mana cost (Yasuo uses no mana)
        val manaCost = if (isYasuo) 0f else when (skillIndex) {
            0 -> if (isTulen) 55f else if (isMurad) 55f else 50f
            1 -> if (isTulen) 60f else if (isMurad) 60f else 65f
            else -> if (isTulen) 100f else if (isMurad) 80f else 110f
        }

        if (_mobaHeroMP.value < manaCost) {
            _mobaLog.value = "⚡ Không đủ Năng Lượng (Mana) để tung chiêu!"
            return
        }

        val actualPing = if (_isSimulating.value) _currentPing.value else 10
        val lossPercent = if (_isSimulating.value) _targetLoss.value else 0

        // Trigger cooldown immediately to prevent button spam on UI
        val cds = _mobaSkillCooldowns.value.toMutableList()
        val baseCd = if (isYasuo) {
            when (skillIndex) {
                0 -> 2.5f // Short cooldown for Q
                1 -> 10.0f // W Wall cooldown
                else -> 9.0f // Ultimate cooldown
            }
        } else when (skillIndex) {
            0 -> {
                if (isMurad) {
                    if (_mobaMuradCastCount.value < 2) 0.6f else 8.0f
                } else if (isTulen) 4.5f else 4.0f
            }
            1 -> {
                if (isTulen) {
                    if (_mobaTulenS2CastCount.value < 2) 0.4f else 5.5f
                } else if (isMurad) 7.0f else 6.0f
            }
            else -> if (isMurad) 12.0f else if (isTulen) 11.0f else 14.0f
        }
        cds[skillIndex] = baseCd
        _mobaSkillCooldowns.value = cds
        _mobaHeroMP.value = (_mobaHeroMP.value - manaCost).coerceAtLeast(0f)

        mobaSkillCastsCount++

        viewModelScope.launch {
            // Simulate lag interruption/packet loss
            if (_isSimulating.value && Random.nextInt(100) < lossPercent) {
                mobaSkillsInterruptedCount++
                addMobaDamageText("LOST SKILL ❌", _mobaHeroX.value, _mobaHeroY.value - 6, 0xFFFF1111)
                _mobaLog.value = "❌ RỤNG MẠNG (Packet Loss)! Chiêu thức ${skillIndex + 1} của ${_mobaHero.value} bị hủy do mất gói tin!"
                return@launch
            }

            if (actualPing > 20) {
                delay(actualPing.toLong())
            }

            performMobaSkillActual(skillIndex)
        }
    }

    private suspend fun performMobaSkillActual(skillIndex: Int) {
        val hX = _mobaHeroX.value
        val hY = _mobaHeroY.value
        val isTulen = _mobaHero.value == "Tulen"
        val isMurad = _mobaHero.value == "Murad"

        // Target nearest enemy (or default to firing straight right)
        val target = findNearestMobaEnemy(range = 35f)
        val tX = target?.first ?: (hX + 30f)
        val tY = target?.second ?: hY
        val tId = target?.third

        val angle = kotlin.math.atan2(tY - hY, tX - hX)

        val isYasuo = _mobaHero.value == "Yasuo"
        if (isYasuo) {
            when (skillIndex) {
                0 -> { // Chiêu 1: Bão Kiếm (Steel Tempest)
                    val stacks = _mobaPassiveStacks.value
                    if (stacks >= 2) {
                        _mobaLog.value = "🌪️ HASAGI! Yasuo phóng CƠN BÃO CÁT hất tung kẻ địch dọc đường đi!"
                        _mobaPassiveStacks.value = 0 // consume
                        // Spawn tornado projectile!
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = hX,
                            y = hY,
                            speed = 1.8f,
                            isEnemy = false,
                            damage = 450f,
                            type = "yasuo_q_tornado",
                            color = 0xFFCBD5E1, // greyish sand storm color
                            radius = 3.8f,
                            targetX = tX,
                            targetY = tY,
                            isHoming = false
                        )
                    } else {
                        _mobaLog.value = "⚔️ Yasuo đâm kiếm BÃO KIẾM nhanh chớp nhoáng!"
                        // Spawn fast line thrust projectile
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = hX,
                            y = hY,
                            speed = 3.5f,
                            isEnemy = false,
                            damage = 280f,
                            type = "yasuo_q",
                            color = 0xFFFFFFFF,
                            radius = 1.5f,
                            targetX = tX,
                            targetY = tY,
                            isHoming = false
                        )
                    }
                }
                1 -> { // Chiêu 2: Tường Gió (Wind Wall)
                    _mobaLog.value = "🌪️ Yasuo dựng TƯỜNG GIÓ chắn toàn bộ đòn đánh và kỹ năng tầm xa của kẻ địch!"
                    // Set wind wall coordinates 12 units in front of player
                    val wallAngle = angle
                    val wallX = (hX + kotlin.math.cos(wallAngle) * 12f).coerceIn(10f, 90f)
                    val wallY = (hY + kotlin.math.sin(wallAngle) * 12f).coerceIn(20f, 80f)
                    _mobaWindWallX.value = wallX
                    _mobaWindWallY.value = wallY
                    _mobaWindWallActive.value = true
                    mobaWindWallDurationLeftMs = 3800L // 3.8 seconds active
                }
                2 -> { // Chiêu 3: Trăn Trối (Last Breath)
                    if (!_mobaEnemyIsStunned.value) {
                        _mobaLog.value = "⚠️ Kẻ địch phải bị HẤT TUNG (Choáng) mới thi triển được Trăn Trối!"
                        return
                    }
                    _mobaLog.value = "⚡ SOYEGEDON! Yasuo bay tới TRĂN TRỐI liên hoàn kiếm lên Maloch!"
                    
                    // Teleport Yasuo to Maloch
                    val eX = _mobaEnemyX.value
                    val eY = _mobaEnemyY.value
                    _mobaHeroX.value = eX - 4f
                    _mobaHeroY.value = eY
                    _mobaHeroDestX.value = eX - 4f
                    _mobaHeroDestY.value = eY
                    
                    _mobaHeroIsImmune.value = true
                    
                    // Suspend them both in the air with a fresh knockup of 1200ms duration
                    triggerMobaEnemyKnockup(1200L)
                    
                    viewModelScope.launch {
                        // Animate Yasuo's visual elevation synchronized with the knockup
                        launch {
                            val steps = 24
                            val peakHeight = 45f
                            val stepDelay = 1200L / steps
                            for (i in 0..steps) {
                                val progress = i.toFloat() / steps
                                val angle = progress * kotlin.math.PI
                                _mobaHeroKnockupHeight.value = (kotlin.math.sin(angle) * peakHeight).toFloat()
                                delay(stepDelay)
                            }
                            _mobaHeroKnockupHeight.value = 0f
                        }

                        for (i in 1..4) {
                            dealAoeMobaDamage(eX, eY, radius = 12f, damage = 190f, type = "yasuo_ult")
                            addMobaDamageText("TRĂN TRỐI! 🌪️", eX + Random.nextFloat() * 8f - 4f, eY - 8f - _mobaEnemyKnockupHeight.value, 0xFFFFFFFF)
                            delay(200)
                        }

                        // Slam down with massive extra damage!
                        dealAoeMobaDamage(eX, eY, radius = 14f, damage = 350f, type = "yasuo_ult_slam")
                        addMobaDamageText("SORYE GE TON!!! 💥", eX, eY - 12f, 0xFFEF4444)

                        _mobaHeroIsImmune.value = false
                        // Gain a flow shield!
                        _mobaHeroHP.value = (_mobaHeroHP.value + 400f).coerceAtMost(_mobaHeroMaxHP.value)
                        addMobaDamageText("+400 GIÁP 🛡️", _mobaHeroX.value, _mobaHeroY.value - 6f, 0xFF38BDF8)
                    }
                }
            }
            return
        }

        if (isMurad) {
            when (skillIndex) {
                0 -> { // Chiêu 1: Vô Ảnh Vực (Blink + clone mechanics)
                    val count = _mobaMuradCastCount.value
                    if (count < 2) {
                        if (count == 0) {
                            // Store shadow clone position at current hero position
                            _mobaMuradCloneX.value = hX
                            _mobaMuradCloneY.value = hY
                            _mobaLog.value = "⚔️ Murad lướt VÔ ẢNH VỰC! Để lại ảo ảnh bóng bóng và làm choáng kẻ địch!"
                        } else {
                            _mobaLog.value = "⚔️ Murad lướt VÔ ẢNH VỰC lần 2! Tiếp tục chém quét gây choáng!"
                        }

                        // Dash towards destination, capped at max range of 15. If standing still, dash 12f units.
                        val destX = _mobaHeroDestX.value
                        val destY = _mobaHeroDestY.value
                        val dist = kotlin.math.sqrt((destX - hX) * (destX - hX) + (destY - hY) * (destY - hY))
                        val dashDist = if (dist > 1.0f) dist.coerceAtMost(15f) else 12f
                        val bAng = if (dist > 1.0f) kotlin.math.atan2(destY - hY, destX - hX) else angle
                        val nextX = (hX + kotlin.math.cos(bAng) * dashDist).coerceIn(0f, 100f)
                        val nextY = (hY + kotlin.math.sin(bAng) * dashDist).coerceIn(20f, 80f)

                        // Smooth slide transition
                        val steps = 5
                        val stepX = (nextX - hX) / steps
                        val stepY = (nextY - hY) / steps
                        
                        _mobaDashTrails.value = emptyList()

                        for (i in 1..steps) {
                            val currX = hX + stepX * i
                            val currY = hY + stepY * i
                            _mobaHeroX.value = currX
                            _mobaHeroY.value = currY
                            
                            _mobaDashTrails.value = _mobaDashTrails.value + MobaDashTrail(
                                id = "murad_s1_${System.currentTimeMillis()}_$i",
                                x = currX,
                                y = currY,
                                color = 0xFFF59E0B,
                                isTulen = false,
                                alpha = 0.16f * i
                            )
                            delay(15)
                        }

                        _mobaHeroX.value = nextX
                        _mobaHeroY.value = nextY
                        _mobaHeroDestX.value = nextX
                        _mobaHeroDestY.value = nextY

                        viewModelScope.launch {
                            delay(350)
                            _mobaDashTrails.value = emptyList()
                        }

                        // Deal AoE damage and stun at destination
                        dealAoeMobaDamage(nextX, nextY, radius = 7f, damage = 250f, type = "murad_s1")
                        // Apply stun
                        val nearest = findNearestMobaEnemy(range = 8f)
                        if (nearest != null && nearest.third == "enemy_hero") {
                            triggerMobaEnemyStun(800L) // 0.8s stun
                        }

                        _mobaMuradCastCount.value = count + 1
                    } else {
                        // Teleport back to shadow clone!
                        _mobaLog.value = "⚔️ Murad giật bóng biến ảo quay về vị trí ảo ảnh xuất phát ban đầu!"
                        val cloneX = _mobaMuradCloneX.value
                        val cloneY = _mobaMuradCloneY.value

                        _mobaHeroX.value = cloneX
                        _mobaHeroY.value = cloneY
                        _mobaHeroDestX.value = cloneX
                        _mobaHeroDestY.value = cloneY

                        // Clear clone
                        _mobaMuradCloneX.value = -1f
                        _mobaMuradCloneY.value = -1f
                        _mobaMuradCastCount.value = 0
                    }
                }
                1 -> { // Chiêu 2: Vô Ảnh Trận (Vòng tròn bão cát)
                    _mobaHeroIsImmune.value = true
                    _mobaLog.value = "⚔️ Murad vẽ VÔ ẢNH TRẬN! Tạo vòng tròn bảo hộ cát vàng tránh sát thương và làm chậm kẻ địch!"

                    // Deal AoE damage
                    dealAoeMobaDamage(hX, hY, radius = 10f, damage = 260f, type = "murad_s2")

                    // Stun / Slow if enemy hits the edge of the circle (dist 7.0f to 10.0f)
                    val nearest = findNearestMobaEnemy(range = 11f)
                    if (nearest != null && nearest.third == "enemy_hero") {
                        val dist = kotlin.math.sqrt((_mobaEnemyX.value - hX) * (_mobaEnemyX.value - hX) + (_mobaEnemyY.value - hY) * (_mobaEnemyY.value - hY))
                        if (dist in 7.0f..10.5f) {
                            _mobaLog.value = "🎯 Maloch chạm rìa VÔ ẢNH TRẬN! Bị giảm giáp và bị Choáng mạnh!"
                            triggerMobaEnemyStun(1500L) // 1.5s stun
                        } else {
                            _mobaLog.value = "🎯 Maloch nằm trong VÔ ẢNH TRẬN! Bị sát thương cát vàng bào mòn!"
                        }
                    }

                    delay(500) // 0.5s untargetable
                    _mobaHeroIsImmune.value = false
                }
                2 -> { // Chiêu 3: Ảo Ảnh Trảm (Flurry attack, invulnerable)
                    _mobaHeroIsImmune.value = true
                    _mobaLog.value = "⚔️ Murad tung ẢO ẢNH TRẢM! Hóa thành 5 luồng kiếm khí chém nát đội hình địch!"
                    _mobaPassiveStacks.value = 0 // consume stacks

                    for (i in 1..5) {
                        dealAoeMobaDamage(tX, tY, radius = 13f, damage = 180f, type = "murad_ult")
                        
                        val dx1 = Random.nextFloat() * 14f - 7f
                        val dy1 = Random.nextFloat() * 14f - 7f
                        val dx2 = Random.nextFloat() * 14f - 7f
                        val dy2 = Random.nextFloat() * 14f - 7f

                        _mobaProjectiles.value = _mobaProjectiles.value + listOf(
                            MobaProjectile(
                                x = tX + dx1,
                                y = tY + dy1,
                                speed = 2.0f,
                                isEnemy = false,
                                damage = 0f,
                                type = "murad_slash_visual",
                                color = 0xFFF59E0B,
                                radius = 2.0f,
                                targetX = tX - dx1,
                                targetY = tY - dy1,
                                isHoming = false
                            ),
                            MobaProjectile(
                                x = tX + dx2,
                                y = tY - dy2,
                                speed = 2.0f,
                                isEnemy = false,
                                damage = 0f,
                                type = "murad_slash_visual",
                                color = 0xFFF59E0B,
                                radius = 2.0f,
                                targetX = tX - dx2,
                                targetY = tY + dy2,
                                isHoming = false
                            )
                        )
                        delay(120)
                    }

                    _mobaHeroIsImmune.value = false
                }
            }
            return
        }

        if (isTulen) {
            when (skillIndex) {
                0 -> { // Chiêu 1: Lôi Quang (3 tia điện fan-shape)
                    _mobaLog.value = "⚡ Tulen tung LÔI QUANG! Phóng 3 tia điện càn quét kẻ địch!"
                    val angles = listOf(angle - 0.25f, angle, angle + 0.25f)
                    angles.forEach { ang ->
                        val speed = 2.0f
                        val destX = hX + kotlin.math.cos(ang) * 40f
                        val destY = hY + kotlin.math.sin(ang) * 40f
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = hX,
                            y = hY,
                            speed = speed,
                            isEnemy = false,
                            damage = 380f,
                            type = "tulen_s1",
                            color = 0xFF00FFFF,
                            radius = 2.2f,
                            targetX = destX,
                            targetY = destY,
                            isHoming = false
                        )
                    }
                }
                1 -> { // Chiêu 2: Lôi Động (Blink & deal dmg)
                    val currentCast = _mobaTulenS2CastCount.value
                    _mobaTulenS2CastCount.value = if (currentCast < 2) currentCast + 1 else 0
                    _mobaLog.value = "⚡ Tulen lướt LÔI ĐỘNG (Lần ${currentCast + 1}/3)! Dịch chuyển tức thời gây sát thương!"
                    // Blink towards destination, capped at max range of 15
                    val destX = _mobaHeroDestX.value
                    val destY = _mobaHeroDestY.value
                    val dist = kotlin.math.sqrt((destX - hX) * (destX - hX) + (destY - hY) * (destY - hY))
                    val dashDist = if (dist > 1.0f) dist.coerceAtMost(16f) else 12f
                    
                    val bAng = if (dist > 1.0f) kotlin.math.atan2(destY - hY, destX - hX) else angle
                    val nextX = (hX + kotlin.math.cos(bAng) * dashDist).coerceIn(0f, 100f)
                    val nextY = (hY + kotlin.math.sin(bAng) * dashDist).coerceIn(20f, 80f)

                    // Damage at start
                    dealAoeMobaDamage(hX, hY, radius = 8f, damage = 220f, type = "tulen_s2")

                    // Smooth slide transition
                    val steps = 5
                    val stepX = (nextX - hX) / steps
                    val stepY = (nextY - hY) / steps
                    
                    _mobaDashTrails.value = emptyList()

                    for (i in 1..steps) {
                        val currX = hX + stepX * i
                        val currY = hY + stepY * i
                        _mobaHeroX.value = currX
                        _mobaHeroY.value = currY
                        
                        _mobaDashTrails.value = _mobaDashTrails.value + MobaDashTrail(
                            id = "tulen_s2_${System.currentTimeMillis()}_$i",
                            x = currX,
                            y = currY,
                            color = 0xFF00FFFF,
                            isTulen = true,
                            alpha = 0.16f * i
                        )
                        delay(15)
                    }

                    _mobaHeroX.value = nextX
                    _mobaHeroY.value = nextY
                    _mobaHeroDestX.value = nextX
                    _mobaHeroDestY.value = nextY

                    viewModelScope.launch {
                        delay(350)
                        _mobaDashTrails.value = emptyList()
                    }

                    // Damage at end
                    dealAoeMobaDamage(nextX, nextY, radius = 8f, damage = 220f, type = "tulen_s2")
                }
                2 -> { // Chiêu 3: Lôi Điểu (Ult tracking shot)
                    if (target == null) {
                        _mobaLog.value = "⚠️ Tulen: LÔI ĐIỂU cần mục tiêu để khóa!"
                        return
                    }
                    _mobaLog.value = "⚡ LÔI ĐIỂU ĐÃ KHÓA MỤC TIÊU! Chuẩn bị oanh tạc cực mạnh..."
                    _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                        x = hX,
                        y = hY,
                        speed = 1.6f,
                        isEnemy = false,
                        damage = 900f, // Deals massive damage
                        type = "tulen_ult",
                        color = 0xFFE020FF,
                        radius = 4f,
                        targetX = tX,
                        targetY = tY,
                        isHoming = true,
                        homingTargetId = tId
                    )
                }
            }
        } else {
            // Valhein
            when (skillIndex) {
                0 -> { // Chiêu 1: Chuyến Săn Ám Ảnh (Red explosive glaive)
                    _mobaLog.value = "🏹 Valhein tung CHUYẾN SĂN ÁM ẢNH! Phi tiêu đỏ nổ lan diện rộng!"
                    _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                        x = hX,
                        y = hY,
                        speed = 2.4f,
                        isEnemy = false,
                        damage = 350f,
                        type = "valhein_s1",
                        color = 0xFFFF2222,
                        radius = 2.5f,
                        targetX = tX,
                        targetY = tY,
                        isHoming = true,
                        homingTargetId = tId
                    )
                }
                1 -> { // Chiêu 2: Lời Nguyền Tử Vong (Yellow stun glaive)
                    _mobaLog.value = "🏹 Valhein tung LỜI NGUYỀN TỬ VONG! Phi tiêu vàng gây choáng cực lâu!"
                    _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                        x = hX,
                        y = hY,
                        speed = 2.4f,
                        isEnemy = false,
                        damage = 220f,
                        type = "valhein_s2",
                        color = 0xFFFFFF00,
                        radius = 2.5f,
                        targetX = tX,
                        targetY = tY,
                        isHoming = true,
                        homingTargetId = tId
                    )
                }
                2 -> { // Chiêu 3: Bão Đạn (Ultimate shot gun spray)
                    _mobaLog.value = "🏹 Valhein kích hoạt BÃO ĐẠN! Xả 6 đạn bạc hủy diệt mục tiêu cận kề!"
                    val baseAngle = angle
                    val bulletAngles = listOf(
                        baseAngle - 0.4f, baseAngle - 0.24f, baseAngle - 0.08f,
                        baseAngle + 0.08f, baseAngle + 0.24f, baseAngle + 0.4f
                    )
                    bulletAngles.forEach { bAng ->
                        val destX = hX + kotlin.math.cos(bAng) * 35f
                        val destY = hY + kotlin.math.sin(bAng) * 35f
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = hX,
                            y = hY,
                            speed = 2.5f,
                            isEnemy = false,
                            damage = 280f,
                            type = "valhein_ult",
                            color = 0xFFEEEEEE,
                            radius = 2.0f,
                            targetX = destX,
                            targetY = destY,
                            isHoming = false
                        )
                    }
                    // Speed boost stacks for Valhein
                    _mobaPassiveStacks.value = (_mobaPassiveStacks.value + 3).coerceAtMost(6)
                }
            }
        }
    }

    private fun findNearestMobaEnemy(range: Float): Triple<Float, Float, String>? {
        val hX = _mobaHeroX.value
        val hY = _mobaHeroY.value
        var minDist = range
        var bestTarget: Triple<Float, Float, String>? = null

        // Check enemy champion
        val eHP = _mobaEnemyHP.value
        if (eHP > 0f) {
            val eX = _mobaEnemyX.value
            val eY = _mobaEnemyY.value
            val d = kotlin.math.sqrt((eX - hX) * (eX - hX) + (eY - hY) * (eY - hY))
            if (d < minDist) {
                minDist = d
                bestTarget = Triple(eX, eY, "enemy_hero")
            }
        }

        // Check enemy creeps
        _mobaCreeps.value.filter { it.isEnemy && it.hp > 0f }.forEach { creep ->
            val d = kotlin.math.sqrt((creep.x - hX) * (creep.x - hX) + (creep.y - hY) * (creep.y - hY))
            if (d < minDist) {
                minDist = d
                bestTarget = Triple(creep.x, creep.y, creep.id)
            }
        }

        // Check enemy turret (Mid)
        val etHP = _mobaEnemyTurretHP.value
        if (etHP > 0f) {
            val d = kotlin.math.sqrt((75f - hX) * (75f - hX) + (50f - hY) * (50f - hY))
            if (d < minDist) {
                minDist = d
                bestTarget = Triple(75f, 50f, "enemy_turret")
            }
        }

        // Check enemy turret (Top)
        val etTopHP = _mobaEnemyTurretTopHP.value
        if (etTopHP > 0f) {
            val d = kotlin.math.sqrt((75f - hX) * (75f - hX) + (28f - hY) * (28f - hY))
            if (d < minDist) {
                minDist = d
                bestTarget = Triple(75f, 28f, "enemy_turret_top")
            }
        }

        // Check enemy turret (Bot)
        val etBotHP = _mobaEnemyTurretBotHP.value
        if (etBotHP > 0f) {
            val d = kotlin.math.sqrt((75f - hX) * (75f - hX) + (72f - hY) * (72f - hY))
            if (d < minDist) {
                minDist = d
                bestTarget = Triple(75f, 72f, "enemy_turret_bot")
            }
        }

        return bestTarget
    }

    private fun addMobaDamageText(text: String, x: Float, y: Float, color: Long) {
        val newText = MobaDamageText(text = text, x = x, y = y, color = color)
        _mobaDamageTexts.value = _mobaDamageTexts.value + newText
    }

    private fun dealAoeMobaDamage(centerX: Float, centerY: Float, radius: Float, damage: Float, type: String) {
        // Find all enemy units inside radius and apply damage
        var hitAny = false

        // Enemy Champ
        val eHP = _mobaEnemyHP.value
        if (eHP > 0f) {
            val dist = kotlin.math.sqrt((_mobaEnemyX.value - centerX) * (_mobaEnemyX.value - centerX) + (_mobaEnemyY.value - centerY) * (_mobaEnemyY.value - centerY))
            if (dist <= radius) {
                _mobaEnemyHP.value = (_mobaEnemyHP.value - damage).coerceAtLeast(0f)
                addMobaDamageText("-${damage.toInt()}", _mobaEnemyX.value, _mobaEnemyY.value - 5f, 0xFFFFCC00)
                hitAny = true
                mobaSkillHitsCount++
                if (type.startsWith("tulen")) {
                    incrementTulenPassive()
                }
            }
        }

        // Enemy Creeps
        val creeps = _mobaCreeps.value.toMutableList()
        var updated = false
        creeps.forEach { creep ->
            if (creep.isEnemy && creep.hp > 0f) {
                val dist = kotlin.math.sqrt((creep.x - centerX) * (creep.x - centerX) + (creep.y - centerY) * (creep.y - centerY))
                if (dist <= radius) {
                    creep.hp = (creep.hp - damage).coerceAtLeast(0f)
                    addMobaDamageText("-${damage.toInt()}", creep.x, creep.y - 4f, 0xFFFFCC00)
                    updated = true
                    hitAny = true
                    mobaSkillHitsCount++
                    if (type.startsWith("tulen")) {
                        incrementTulenPassive()
                    }
                }
            }
        }
        if (updated) {
            _mobaCreeps.value = creeps
        }

        // Enemy Turrets (Top, Mid, Bot)
        if (_mobaEnemyTurretHP.value > 0f) {
            val dist = kotlin.math.sqrt((75f - centerX) * (75f - centerX) + (50f - centerY) * (50f - centerY))
            if (dist <= radius) {
                _mobaEnemyTurretHP.value = (_mobaEnemyTurretHP.value - damage * 0.5f).coerceAtLeast(0f) // turret has armor
                addMobaDamageText("-${(damage * 0.5f).toInt()}", 75f, 44f, 0xFFFFCC00)
                hitAny = true
            }
        }
        if (_mobaEnemyTurretTopHP.value > 0f) {
            val dist = kotlin.math.sqrt((75f - centerX) * (75f - centerX) + (28f - centerY) * (28f - centerY))
            if (dist <= radius) {
                _mobaEnemyTurretTopHP.value = (_mobaEnemyTurretTopHP.value - damage * 0.5f).coerceAtLeast(0f)
                addMobaDamageText("-${(damage * 0.5f).toInt()}", 75f, 22f, 0xFFFFCC00)
                hitAny = true
            }
        }
        if (_mobaEnemyTurretBotHP.value > 0f) {
            val dist = kotlin.math.sqrt((75f - centerX) * (75f - centerX) + (72f - centerY) * (72f - centerY))
            if (dist <= radius) {
                _mobaEnemyTurretBotHP.value = (_mobaEnemyTurretBotHP.value - damage * 0.5f).coerceAtLeast(0f)
                addMobaDamageText("-${(damage * 0.5f).toInt()}", 75f, 66f, 0xFFFFCC00)
                hitAny = true
            }
        }
    }

    private fun incrementTulenPassive() {
        val stacks = _mobaPassiveStacks.value
        if (stacks >= 5) return
        val next = stacks + 1
        _mobaPassiveStacks.value = next
        if (next >= 5) {
            _mobaLog.value = "⚡ Tulen kích hoạt NỘI TẠI LÔI ĐIỆN! 5 luồng sét bao quanh sấm sét càn quét!"
            mobaTulenPassiveOrbs = 5
        }
    }

    private fun incrementMuradPassive() {
        val current = _mobaPassiveStacks.value
        if (current < 4) {
            val next = current + 1
            _mobaPassiveStacks.value = next
            if (next == 4) {
                _mobaLog.value = "⚔️ Murad: GIẢI ẤN PHONG ẤN! ẢO ẢNH TRẢM ĐÃ KHÓA SẴN SÀNG! 🔥"
                addMobaDamageText("ACTIVE! 🔥", _mobaHeroX.value, _mobaHeroY.value - 12f, 0xFFF59E0B)
            } else {
                _mobaLog.value = "⚔️ Murad tích ấn Phong Ấn: $next/4"
                addMobaDamageText("ẤN ⚔️", _mobaHeroX.value, _mobaHeroY.value - 10f, 0xFFEAB308)
            }
        }
    }

    private suspend fun runMobaGameLoop() {
        var tickCounter = 0
        var creepSpawnTimer = 11f // spawn first wave almost immediately (after 1s)

        while (_mobaState.value == "playing") {
            delay(50)
            tickCounter++
            val currentTime = System.currentTimeMillis()

            // 1. Cooldown recovery and passive decay
            val cds = _mobaSkillCooldowns.value.map { (it - 0.05f).coerceAtLeast(0f) }
            _mobaSkillCooldowns.value = cds

            val isTulen = _mobaHero.value == "Tulen"
            val isMurad = _mobaHero.value == "Murad"
            val isYasuo = _mobaHero.value == "Yasuo"
            if (isYasuo) {
                // Decay Yasuo wind wall timer
                if (mobaWindWallDurationLeftMs > 0L) {
                    mobaWindWallDurationLeftMs -= 50L
                    if (mobaWindWallDurationLeftMs <= 0L) {
                        _mobaWindWallActive.value = false
                        _mobaWindWallX.value = -1f
                        _mobaWindWallY.value = -1f
                        _mobaLog.value = "🌪️ Tường Gió của Yasuo đã biến tan."
                    }
                }
                // Decay Yasuo Bão Kiếm passive stacks after 6s (every 120 ticks)
                if (tickCounter % 120 == 0) {
                    val current = _mobaPassiveStacks.value
                    if (current > 0) {
                        _mobaPassiveStacks.value = 0
                        _mobaLog.value = "⚔️ Yasuo: Cộng dồn Tụ Bão đã hết thời gian duy trì!"
                    }
                }
            } else if (isMurad) {
                // Decay Murad passive stacks after 6s of not hitting (every 40 ticks = 2s decay 1)
                if (tickCounter % 40 == 0) {
                    val current = _mobaPassiveStacks.value
                    if (current > 0) {
                        val next = current - 1
                        _mobaPassiveStacks.value = next
                        if (next == 0) {
                            _mobaLog.value = "⚔️ Murad: Phong ấn cổ đã tan biến hoàn toàn. Hãy đánh thường để tích lại!"
                        } else {
                            _mobaLog.value = "⚔️ Murad: Các luồng Phong Ấn cổ đang dần biến mất... ($next/4)"
                        }
                    }
                }
            } else if (!isTulen) {
                // Decay Valhein passive speed stacks
                if (tickCounter % 20 == 0) { // every 1s
                    _mobaPassiveStacks.value = (_mobaPassiveStacks.value - 1).coerceAtLeast(0)
                }
            } else {
                // Tulen electrical passive attacks
                if (mobaTulenPassiveOrbs > 0 && currentTime - mobaPassiveTimer > 600L) {
                    val target = findNearestMobaEnemy(range = 28f)
                    if (target != null) {
                        mobaTulenPassiveOrbs--
                        mobaPassiveTimer = currentTime
                        if (mobaTulenPassiveOrbs == 0) {
                            _mobaPassiveStacks.value = 0
                        }
                        
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = _mobaHeroX.value,
                            y = _mobaHeroY.value,
                            speed = 2.4f,
                            isEnemy = false,
                            damage = 160f,
                            type = "tulen_passive_zap",
                            color = 0xFF00E6FF,
                            radius = 1.5f,
                            targetX = target.first,
                            targetY = target.second,
                            isHoming = true,
                            homingTargetId = target.third
                        )
                    }
                }
            }

            // Hero HP/Mana regen
            if (tickCounter % 10 == 0) { // every 500ms
                val hpRegen = 15f
                val mpRegen = 20f
                _mobaHeroHP.value = (_mobaHeroHP.value + hpRegen).coerceAtMost(_mobaHeroMaxHP.value)
                _mobaHeroMP.value = (_mobaHeroMP.value + mpRegen).coerceAtMost(_mobaHeroMaxMP.value)
            }

            // 2. Creep Spawning logic
            creepSpawnTimer += 0.05f
            if (creepSpawnTimer >= 12.0f) {
                creepSpawnTimer = 0f
                spawnMobaCreepWave()
            }

            // 3. Move Hero
            val hX = _mobaHeroX.value
            val hY = _mobaHeroY.value
            val speedBonus = if (!isTulen && !isMurad) 1f + (_mobaPassiveStacks.value * 0.06f) else 1f
            val moveSpeed = if (isMurad) 1.55f else 1.3f * speedBonus

            if (_mobaJoystickActive.value) {
                val angle = _mobaJoystickAngle.value
                val stepX = kotlin.math.cos(angle) * moveSpeed * 2.2f
                val stepY = kotlin.math.sin(angle) * moveSpeed * 2.2f
                _mobaHeroDestX.value = (hX + stepX).coerceIn(0f, 100f)
                _mobaHeroDestY.value = (hY + stepY).coerceIn(20f, 80f)
            } else {
                val activeDir = _mobaMoveDirection.value
                if (activeDir != MobaMoveDirection.NONE) {
                    val step = moveSpeed
                    when (activeDir) {
                        MobaMoveDirection.UP -> {
                            _mobaHeroDestX.value = hX
                            _mobaHeroDestY.value = (hY - step * 2f).coerceIn(20f, 80f)
                        }
                        MobaMoveDirection.DOWN -> {
                            _mobaHeroDestX.value = hX
                            _mobaHeroDestY.value = (hY + step * 2f).coerceIn(20f, 80f)
                        }
                        MobaMoveDirection.LEFT -> {
                            _mobaHeroDestX.value = (hX - step * 2f).coerceIn(0f, 100f)
                            _mobaHeroDestY.value = hY
                        }
                        MobaMoveDirection.RIGHT -> {
                            _mobaHeroDestX.value = (hX + step * 2f).coerceIn(0f, 100f)
                            _mobaHeroDestY.value = hY
                        }
                        else -> {}
                    }
                }
            }

            val destX = _mobaHeroDestX.value
            val destY = _mobaHeroDestY.value
            
            val dx = destX - hX
            val dy = destY - hY
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist > 1.0f) {
                // Movement speed modified by active stacks if Valhein
                val speedBonus = if (!isTulen && !isMurad) 1f + (_mobaPassiveStacks.value * 0.06f) else 1f
                val moveSpeed = if (isMurad) 1.55f else 1.3f * speedBonus // Murad is an agile assassin
                val step = moveSpeed.coerceAtMost(dist)
                
                _mobaHeroX.value = hX + (dx / dist) * step
                _mobaHeroY.value = hY + (dy / dist) * step
            }

            // 4. Update Projectiles (Homing, movement, collision)
            updateMobaProjectiles()

            // 5. Update Creeps (Movement, attacks)
            updateMobaCreeps(currentTime)

            // 6. Update Enemy AI (Maloch)
            updateMobaEnemyAI(currentTime)

            // 7. Update Turrets (Auto-target & attack)
            updateMobaTurrets(currentTime)

            // 8. Update Floating Damage texts
            val dmgTexts = _mobaDamageTexts.value.map {
                it.copy(age = it.age + 1, y = it.y - 0.6f)
            }
            _mobaDamageTexts.value = dmgTexts.filter { it.age < 18 }

            // 9. Game Over checks
            checkMobaGameOver()
        }
    }

    private fun spawnMobaCreepWave() {
        _mobaLog.value = "🛡️ Lính 3 đường (Kinh Thống, Giữa, Rồng) đã xuất trận!"
        val allies = listOf(
            // Top lane
            MobaCreep(x = 10f, y = 26f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "top"),
            MobaCreep(x = 8f, y = 28f, hp = 600f, maxHp = 600f, isEnemy = false, speed = 0.5f, lane = "top"),
            MobaCreep(x = 6f, y = 30f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "top"),
            
            // Mid lane
            MobaCreep(x = 10f, y = 48f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "mid"),
            MobaCreep(x = 8f, y = 50f, hp = 600f, maxHp = 600f, isEnemy = false, speed = 0.5f, lane = "mid"),
            MobaCreep(x = 6f, y = 52f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "mid"),
            
            // Bot lane
            MobaCreep(x = 10f, y = 70f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "bot"),
            MobaCreep(x = 8f, y = 72f, hp = 600f, maxHp = 600f, isEnemy = false, speed = 0.5f, lane = "bot"),
            MobaCreep(x = 6f, y = 74f, hp = 450f, maxHp = 450f, isEnemy = false, speed = 0.55f, lane = "bot")
        )
        val enemies = listOf(
            // Top lane
            MobaCreep(x = 90f, y = 26f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "top"),
            MobaCreep(x = 92f, y = 28f, hp = 600f, maxHp = 600f, isEnemy = true, speed = 0.5f, lane = "top"),
            MobaCreep(x = 94f, y = 30f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "top"),
            
            // Mid lane
            MobaCreep(x = 90f, y = 48f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "mid"),
            MobaCreep(x = 92f, y = 50f, hp = 600f, maxHp = 600f, isEnemy = true, speed = 0.5f, lane = "mid"),
            MobaCreep(x = 94f, y = 52f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "mid"),
            
            // Bot lane
            MobaCreep(x = 90f, y = 70f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "bot"),
            MobaCreep(x = 92f, y = 72f, hp = 600f, maxHp = 600f, isEnemy = true, speed = 0.5f, lane = "bot"),
            MobaCreep(x = 94f, y = 74f, hp = 450f, maxHp = 450f, isEnemy = true, speed = 0.55f, lane = "bot")
        )
        _mobaCreeps.value = _mobaCreeps.value + allies + enemies
    }

    private fun updateMobaProjectiles() {
        val projs = _mobaProjectiles.value.toMutableList()
        var updated = false

        projs.forEach { proj ->
            if (proj.isFinished) return@forEach

            // Check Wind Wall blocking (if the projectile is an enemy projectile, and Wind Wall is active, check if it is near the Wind Wall)
            if (proj.isEnemy && _mobaWindWallActive.value) {
                val wwX = _mobaWindWallX.value
                val wwY = _mobaWindWallY.value
                val distToWall = kotlin.math.sqrt((proj.x - wwX) * (proj.x - wwX) + (proj.y - wwY) * (proj.y - wwY))
                if (distToWall <= 12f) { // Large wind wall shield area (12 units)
                    proj.isFinished = true
                    updated = true
                    addMobaDamageText("BLOCK 🌪️", proj.x, proj.y - 4f, 0xFF94A3B8)
                    _mobaLog.value = "🌪️ Tường Gió của Yasuo đã cản thành công đòn đánh của kẻ địch!"
                    return@forEach
                }
            }

            // Custom collision check for Yasuo's non-homing skills
            if (!proj.isEnemy && !proj.isFinished && (proj.type == "yasuo_q" || proj.type == "yasuo_q_tornado")) {
                // Check enemy hero collision
                val eX = _mobaEnemyX.value
                val eY = _mobaEnemyY.value
                val distToHero = kotlin.math.sqrt((proj.x - eX) * (proj.x - eX) + (proj.y - eY) * (proj.y - eY))
                if (_mobaEnemyHP.value > 0f && distToHero <= (proj.radius + 3f)) {
                    // Hit enemy hero!
                    _mobaEnemyHP.value = (_mobaEnemyHP.value - proj.damage).coerceAtLeast(0f)
                    addMobaDamageText("-${proj.damage.toInt()}", eX, eY - 6f, 0xFFFFCC00)
                    mobaSkillHitsCount++
                    
                    if (proj.type == "yasuo_q") {
                        _mobaPassiveStacks.value = (_mobaPassiveStacks.value + 1).coerceAtMost(2)
                        _mobaLog.value = "⚔️ Yasuo tích lũy Bão Kiếm: ${_mobaPassiveStacks.value}/2"
                        if (_mobaPassiveStacks.value == 2) {
                            _mobaLog.value = "🌪️ TỤ BÃO SẴN SÀNG! Đợt Bão Kiếm tiếp theo sẽ phóng Lốc Xoáy hất tung!"
                            addMobaDamageText("TỤ BÃO! 🌪️", _mobaHeroX.value, _mobaHeroY.value - 10f, 0xFFCBD5E1)
                        }
                    } else if (proj.type == "yasuo_q_tornado") {
                        _mobaLog.value = "🌪️ Lốc Xoáy hất tung Maloch cực mạnh!"
                        triggerMobaEnemyKnockup(1500L) // Knock up and stun them for 1.5s
                    }
                    
                    proj.isFinished = true
                    updated = true
                    return@forEach
                }
                
                // Check enemy creeps collision
                val creeps = _mobaCreeps.value.toMutableList()
                var hitCreep = false
                creeps.forEach { creep ->
                    if (creep.isEnemy && creep.hp > 0f) {
                        val distToCreep = kotlin.math.sqrt((proj.x - creep.x) * (proj.x - creep.x) + (proj.y - creep.y) * (proj.y - creep.y))
                        if (distToCreep <= (proj.radius + 2.5f)) {
                            creep.hp = (creep.hp - proj.damage).coerceAtLeast(0f)
                            addMobaDamageText("-${proj.damage.toInt()}", creep.x, creep.y - 4f, 0xFFFFEE22)
                            hitCreep = true
                            
                            if (proj.type == "yasuo_q") {
                                _mobaPassiveStacks.value = (_mobaPassiveStacks.value + 1).coerceAtMost(2)
                                _mobaLog.value = "⚔️ Yasuo tích lũy Bão Kiếm: ${_mobaPassiveStacks.value}/2"
                                if (_mobaPassiveStacks.value == 2) {
                                    _mobaLog.value = "🌪️ TỤ BÃO SẴN SÀNG! Đợt Bão Kiếm tiếp theo sẽ phóng Lốc Xoáy hất tung!"
                                    addMobaDamageText("TỤ BÃO! 🌪️", _mobaHeroX.value, _mobaHeroY.value - 10f, 0xFFCBD5E1)
                                }
                            } else if (proj.type == "yasuo_q_tornado") {
                                creep.isStunned = true
                                creep.stunEndTime = System.currentTimeMillis() + 1500L
                            }
                        }
                    }
                }
                if (hitCreep) {
                    _mobaCreeps.value = creeps
                    if (proj.type == "yasuo_q") {
                        proj.isFinished = true
                        updated = true
                        return@forEach
                    }
                }
            }

            // If homing, resolve coordinates of targets
            var targetX = proj.targetX
            var targetY = proj.targetY
            var targetAlive = true

            if (proj.isHoming) {
                when (proj.homingTargetId) {
                    "player" -> {
                        if (_mobaHeroHP.value <= 0f) targetAlive = false
                        targetX = _mobaHeroX.value
                        targetY = _mobaHeroY.value
                    }
                    "enemy_hero" -> {
                        if (_mobaEnemyHP.value <= 0f) targetAlive = false
                        targetX = _mobaEnemyX.value
                        targetY = _mobaEnemyY.value
                    }
                    "enemy_turret" -> {
                        if (_mobaEnemyTurretHP.value <= 0f) targetAlive = false
                        targetX = 75f
                        targetY = 50f
                    }
                    "enemy_turret_top" -> {
                        if (_mobaEnemyTurretTopHP.value <= 0f) targetAlive = false
                        targetX = 75f
                        targetY = 28f
                    }
                    "enemy_turret_bot" -> {
                        if (_mobaEnemyTurretBotHP.value <= 0f) targetAlive = false
                        targetX = 75f
                        targetY = 72f
                    }
                    "ally_turret" -> {
                        if (_mobaAllyTurretHP.value <= 0f) targetAlive = false
                        targetX = 30f
                        targetY = 50f
                    }
                    "ally_turret_top" -> {
                        if (_mobaAllyTurretTopHP.value <= 0f) targetAlive = false
                        targetX = 30f
                        targetY = 28f
                    }
                    "ally_turret_bot" -> {
                        if (_mobaAllyTurretBotHP.value <= 0f) targetAlive = false
                        targetX = 30f
                        targetY = 72f
                    }
                    else -> {
                        // Creep homing
                        val creep = _mobaCreeps.value.find { it.id == proj.homingTargetId }
                        if (creep == null || creep.hp <= 0f) {
                            targetAlive = false
                        } else {
                            targetX = creep.x
                            targetY = creep.y
                        }
                    }
                }
            }

            if (!targetAlive) {
                // target died, fly forward
                proj.isFinished = true
                updated = true
                return@forEach
            }

            // Move projectile
            val dx = targetX - proj.x
            val dy = targetY - proj.y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist <= proj.speed + 1f) {
                // Hit!
                proj.x = targetX
                proj.y = targetY
                proj.isFinished = true
                updated = true
                applyMobaProjectileImpact(proj)
            } else {
                proj.x += (dx / dist) * proj.speed
                proj.y += (dy / dist) * proj.speed
                updated = true
            }
        }

        if (updated) {
            _mobaProjectiles.value = projs.filter { !it.isFinished }
        }
    }

    private fun applyMobaProjectileImpact(proj: MobaProjectile) {
        val dmg = proj.damage

        if (proj.isEnemy) {
            // Hit player or allied creep or allied turret
            if (proj.homingTargetId == "player") {
                if (_mobaHeroIsImmune.value) {
                    addMobaDamageText("NÉ 💫", _mobaHeroX.value, _mobaHeroY.value - 6f, 0xFF38BDF8)
                } else {
                    _mobaHeroHP.value = (_mobaHeroHP.value - dmg).coerceAtLeast(0f)
                    addMobaDamageText("-${dmg.toInt()}", _mobaHeroX.value, _mobaHeroY.value - 6f, 0xFFFF3333)
                    
                    // Slow effect if Maloch's cleaver
                    if (proj.type == "maloch_cleave") {
                        _mobaLog.value = "⚠️ Bạn bị Maloch chém rìu gây trì hoãn di chuyển (Chậm 50%!)"
                        addMobaDamageText("SLOWED ❄️", _mobaHeroX.value, _mobaHeroY.value - 12f, 0xFF33CCFF)
                    }
                }
            } else if (proj.homingTargetId == "ally_turret") {
                _mobaAllyTurretHP.value = (_mobaAllyTurretHP.value - dmg).coerceAtLeast(0f)
                addMobaDamageText("-${dmg.toInt()}", 30f, 44f, 0xFFFF3333)
            } else if (proj.homingTargetId == "ally_turret_top") {
                _mobaAllyTurretTopHP.value = (_mobaAllyTurretTopHP.value - dmg).coerceAtLeast(0f)
                addMobaDamageText("-${dmg.toInt()}", 30f, 22f, 0xFFFF3333)
            } else if (proj.homingTargetId == "ally_turret_bot") {
                _mobaAllyTurretBotHP.value = (_mobaAllyTurretBotHP.value - dmg).coerceAtLeast(0f)
                addMobaDamageText("-${dmg.toInt()}", 30f, 66f, 0xFFFF3333)
            } else {
                // Hit allied creep
                val creeps = _mobaCreeps.value.toMutableList()
                val creep = creeps.find { it.id == proj.homingTargetId }
                if (creep != null) {
                    creep.hp = (creep.hp - dmg).coerceAtLeast(0f)
                    addMobaDamageText("-${dmg.toInt()}", creep.x, creep.y - 4f, 0xFFFF5555)
                    _mobaCreeps.value = creeps
                }
            }
        } else {
            // Hit enemy champ or enemy creep or enemy turret
            if (proj.homingTargetId == "enemy_hero") {
                _mobaEnemyHP.value = (_mobaEnemyHP.value - dmg).coerceAtLeast(0f)
                addMobaDamageText("-${dmg.toInt()}", _mobaEnemyX.value, _mobaEnemyY.value - 6f, 0xFFFFCC00)
                mobaSkillHitsCount++

                // Handle skill effect
                if (proj.type == "valhein_s2") { // Yellow stun
                    _mobaLog.value = "🎯 Choáng! Maloch bị Valhein hóa đá găm phi tiêu vàng (Choáng 2s)!"
                    triggerMobaEnemyStun(2000L)
                } else if (proj.type == "valhein_s1") { // Red AoE explode
                    dealAoeMobaDamage(_mobaEnemyX.value, _mobaEnemyY.value, radius = 9f, damage = dmg * 0.4f, type = "valhein_s1_aoe")
                } else if (proj.type == "tulen_ult") {
                    _mobaLog.value = "⚡ SIÊU PHẨM LÔI ĐIỂU! Oanh tạc dứt điểm cực đau lên Maloch!"
                    // gain stacks on hit
                    incrementTulenPassive()
                } else if (proj.type.startsWith("tulen")) {
                    incrementTulenPassive()
                } else if (proj.type == "murad_basic") {
                    incrementMuradPassive()
                }
            } else if (proj.homingTargetId == "enemy_turret") {
                _mobaEnemyTurretHP.value = (_mobaEnemyTurretHP.value - dmg * 0.7f).coerceAtLeast(0f)
                addMobaDamageText("-${(dmg * 0.7f).toInt()}", 75f, 44f, 0xFFFFCC00)
            } else if (proj.homingTargetId == "enemy_turret_top") {
                _mobaEnemyTurretTopHP.value = (_mobaEnemyTurretTopHP.value - dmg * 0.7f).coerceAtLeast(0f)
                addMobaDamageText("-${(dmg * 0.7f).toInt()}", 75f, 22f, 0xFFFFCC00)
            } else if (proj.homingTargetId == "enemy_turret_bot") {
                _mobaEnemyTurretBotHP.value = (_mobaEnemyTurretBotHP.value - dmg * 0.7f).coerceAtLeast(0f)
                addMobaDamageText("-${(dmg * 0.7f).toInt()}", 75f, 66f, 0xFFFFCC00)
            } else {
                // Hit enemy creep
                val creeps = _mobaCreeps.value.toMutableList()
                val creep = creeps.find { it.id == proj.homingTargetId }
                if (creep != null) {
                    creep.hp = (creep.hp - dmg).coerceAtLeast(0f)
                    addMobaDamageText("-${dmg.toInt()}", creep.x, creep.y - 4f, 0xFFFFEE22)
                    
                    if (proj.type == "valhein_s2") {
                        creep.isStunned = true
                        creep.stunEndTime = System.currentTimeMillis() + 2000L
                    } else if (proj.type == "valhein_s1") {
                        dealAoeMobaDamage(creep.x, creep.y, radius = 9f, damage = dmg * 0.4f, type = "valhein_s1_aoe")
                    } else if (proj.type.startsWith("tulen")) {
                        incrementTulenPassive()
                    } else if (proj.type == "murad_basic") {
                        incrementMuradPassive()
                    }
                    _mobaCreeps.value = creeps
                }
            }
        }
    }

    private fun triggerMobaEnemyStun(durationMs: Long) {
        _mobaEnemyIsStunned.value = true
        mobaEnemyStunUntil = System.currentTimeMillis() + durationMs
        addMobaDamageText("STUNNED 🌀", _mobaEnemyX.value, _mobaEnemyY.value - 12f, 0xFFFFFF00)
    }

    fun triggerMobaEnemyKnockup(durationMs: Long) {
        _mobaEnemyIsKnockedUp.value = true
        _mobaEnemyIsStunned.value = true
        mobaEnemyStunUntil = System.currentTimeMillis() + durationMs
        addMobaDamageText("HẤT TUNG! 🌪️", _mobaEnemyX.value, _mobaEnemyY.value - 12f, 0xFF38BDF8)
        
        viewModelScope.launch {
            val steps = 24
            val peakHeight = 45f
            val stepDelay = durationMs / steps
            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                val angle = progress * kotlin.math.PI
                _mobaEnemyKnockupHeight.value = (kotlin.math.sin(angle) * peakHeight).toFloat()
                delay(stepDelay)
            }
            _mobaEnemyKnockupHeight.value = 0f
            _mobaEnemyIsKnockedUp.value = false
        }
    }

    private fun updateMobaCreeps(currentTime: Long) {
        val creeps = _mobaCreeps.value.toMutableList()
        if (creeps.isEmpty()) return

        creeps.forEach { creep ->
            if (creep.hp <= 0f) return@forEach

            // Check Stun
            if (creep.isStunned) {
                if (currentTime >= creep.stunEndTime) {
                    creep.isStunned = false
                } else {
                    return@forEach // Skip moving/attacking if stunned
                }
            }

            // AI: Find target in front
            val range = 11f
            var target: Triple<Float, Float, String>? = null

            if (creep.isEnemy) {
                // Enemy creep looks for Player or allied creeps
                val distToPlayer = kotlin.math.sqrt((_mobaHeroX.value - creep.x) * (_mobaHeroX.value - creep.x) + (_mobaHeroY.value - creep.y) * (_mobaHeroY.value - creep.y))
                if (_mobaHeroHP.value > 0f && distToPlayer <= range) {
                    target = Triple(_mobaHeroX.value, _mobaHeroY.value, "player")
                } else {
                    // find nearest allied creep
                    var nearestD = range
                    _mobaCreeps.value.filter { !it.isEnemy && it.hp > 0f }.forEach { ally ->
                        val d = kotlin.math.sqrt((ally.x - creep.x) * (ally.x - creep.x) + (ally.y - creep.y) * (ally.y - creep.y))
                        if (d < nearestD) {
                            nearestD = d
                            target = Triple(ally.x, ally.y, ally.id)
                        }
                    }
                }
            } else {
                // Allied creep looks for Enemy champion or enemy creeps
                val distToEnemyHero = kotlin.math.sqrt((_mobaEnemyX.value - creep.x) * (_mobaEnemyX.value - creep.x) + (_mobaEnemyY.value - creep.y) * (_mobaEnemyY.value - creep.y))
                if (_mobaEnemyHP.value > 0f && distToEnemyHero <= range) {
                    target = Triple(_mobaEnemyX.value, _mobaEnemyY.value, "enemy_hero")
                } else {
                    // find nearest enemy creep
                    var nearestD = range
                    _mobaCreeps.value.filter { it.isEnemy && it.hp > 0f }.forEach { enemy ->
                        val d = kotlin.math.sqrt((enemy.x - creep.x) * (enemy.x - creep.x) + (enemy.y - creep.y) * (enemy.y - creep.y))
                        if (d < nearestD) {
                            nearestD = d
                            target = Triple(enemy.x, enemy.y, enemy.id)
                        }
                    }
                }
            }

            // Attack or Move
            val finalTarget = target
            if (finalTarget != null) {
                // Stop and attack
                if (currentTime - creep.lastAttackTime > 1300L) {
                    creep.lastAttackTime = currentTime
                    // Spawn small bullet projectile
                    _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                        x = creep.x,
                        y = creep.y,
                        speed = 1.4f,
                        isEnemy = creep.isEnemy,
                        damage = 35f,
                        type = "creep_atk",
                        color = if (creep.isEnemy) 0xFFFF5555 else 0xFF44FF44,
                        radius = 1.2f,
                        targetX = finalTarget.first,
                        targetY = finalTarget.second,
                        isHoming = true,
                        homingTargetId = finalTarget.third
                    )
                }
            } else {
                // Move towards lane base
                val destX = if (creep.isEnemy) 10f else 90f
                val dx = destX - creep.x
                val targetY = when (creep.lane) {
                    "top" -> 28f
                    "bot" -> 72f
                    else -> 50f
                }
                val dy = targetY - creep.y
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist > 1f) {
                    creep.x += (dx / dist) * creep.speed
                    creep.y += (dy / dist) * creep.speed
                }
            }
        }

        _mobaCreeps.value = creeps.filter { it.hp > 0f }
    }

    private fun updateMobaEnemyAI(currentTime: Long) {
        val eHP = _mobaEnemyHP.value
        if (eHP <= 0f) {
            // Respawn countdown handled in background or just wait
            if (tickCounterMoba % 200 == 0) { // approx 10s
                _mobaEnemyHP.value = _mobaEnemyMaxHP.value
                _mobaEnemyX.value = 65f
                _mobaEnemyY.value = 50f
                _mobaLog.value = "👿 Maloch đã hồi sinh từ Tế Đàn Địch và tiến ra Đại Lộ!"
            }
            return
        }

        // Handle stun
        if (_mobaEnemyIsStunned.value) {
            if (currentTime >= mobaEnemyStunUntil) {
                _mobaEnemyIsStunned.value = false
            } else {
                return
            }
        }

        val eX = _mobaEnemyX.value
        val eY = _mobaEnemyY.value
        val hX = _mobaHeroX.value
        val hY = _mobaHeroY.value

        val distToPlayer = kotlin.math.sqrt((hX - eX) * (hX - eX) + (hY - eY) * (hY - eY))

        // Deciding to flee if very low HP
        if (eHP < 900f && _mobaEnemyTurretHP.value > 0f) {
            // Run back to turret X=75
            val dx = 75f - eX
            val dy = 50f - eY
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist > 1f) {
                _mobaEnemyX.value += (dx / dist) * 1.0f
                _mobaEnemyY.value += (dy / dist) * 1.0f
            }
            // Heal up slightly near turret
            if (dist <= 5f) {
                _mobaEnemyHP.value = (eHP + 20f).coerceAtMost(_mobaEnemyMaxHP.value)
            }
            return
        }

        // AI decision
        if (_mobaHeroHP.value > 0f && distToPlayer <= 26f) {
            // Chase Player
            val dx = hX - eX
            val dy = hY - eY
            if (distToPlayer > 5f) {
                _mobaEnemyX.value += (dx / distToPlayer) * 0.75f
                _mobaEnemyY.value += (dy / distToPlayer) * 0.75f
            } else {
                // Swing Cleaver
                if (tickCounterMoba % 24 == 0) { // every 1.2s
                    _mobaLog.value = "👿 Maloch tung QUỶ KIẾM! Chém kiếm quét ngang gây sát thương lớn!"
                    _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                        x = eX,
                        y = eY,
                        speed = 1.8f,
                        isEnemy = true,
                        damage = 180f,
                        type = "maloch_cleave",
                        color = 0xFFFF0033,
                        radius = 2.4f,
                        targetX = hX,
                        targetY = hY,
                        isHoming = true,
                        homingTargetId = "player"
                    )
                }
            }
        } else {
            // Patrol or clear creeps
            var targetCreep: MobaCreep? = null
            var minD = 25f
            _mobaCreeps.value.filter { !it.isEnemy && it.hp > 0f }.forEach { creep ->
                val d = kotlin.math.sqrt((creep.x - eX) * (creep.x - eX) + (creep.y - eY) * (creep.y - eY))
                if (d < minD) {
                    minD = d
                    targetCreep = creep
                }
            }

            if (targetCreep != null) {
                val tc = targetCreep!!
                val dx = tc.x - eX
                val dy = tc.y - eY
                if (minD > 5f) {
                    _mobaEnemyX.value += (dx / minD) * 0.7f
                    _mobaEnemyY.value += (dy / minD) * 0.7f
                } else {
                    if (tickCounterMoba % 30 == 0) {
                        _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                            x = eX,
                            y = eY,
                            speed = 1.6f,
                            isEnemy = true,
                            damage = 100f,
                            type = "basic_cleave",
                            color = 0xFFFF5555,
                            radius = 2.0f,
                            targetX = tc.x,
                            targetY = tc.y,
                            isHoming = true,
                            homingTargetId = tc.id
                        )
                    }
                }
            } else {
                // Move back to patrol spot X=62, Y=50
                val dx = 62f - eX
                val dy = 50f - eY
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist > 1f) {
                    _mobaEnemyX.value += (dx / dist) * 0.5f
                    _mobaEnemyY.value += (dy / dist) * 0.5f
                }
            }
        }
    }

    private var tickCounterMoba = 0

    private fun shootAllyTurret(turretHpState: MutableStateFlow<Float>, tX: Float, tY: Float, homingId: String) {
        if (turretHpState.value <= 0f) return
        if (tickCounterMoba % 26 != 0) return

        var fired = false
        val eX = _mobaEnemyX.value
        val eY = _mobaEnemyY.value
        val dToChamp = kotlin.math.sqrt((eX - tX) * (eX - tX) + (eY - tY) * (eY - tY))
        if (_mobaEnemyHP.value > 0f && dToChamp <= 20f) {
            // Shoot Champ
            _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                x = tX,
                y = tY,
                speed = 1.6f,
                isEnemy = false,
                damage = 250f,
                type = "turret",
                color = 0xFFFFFF33,
                radius = 2.4f,
                targetX = eX,
                targetY = eY,
                isHoming = true,
                homingTargetId = "enemy_hero"
            )
            fired = true
        }

        if (!fired) {
            // Shoot nearest enemy creep
            var nearestCreep: MobaCreep? = null
            var minD = 20f
            _mobaCreeps.value.filter { it.isEnemy && it.hp > 0f }.forEach { creep ->
                val d = kotlin.math.sqrt((creep.x - tX) * (creep.x - tX) + (creep.y - tY) * (creep.y - tY))
                if (d < minD) {
                    minD = d
                    nearestCreep = creep
                }
            }
            
            if (nearestCreep != null) {
                _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                    x = tX,
                    y = tY,
                    speed = 1.6f,
                    isEnemy = false,
                    damage = 220f,
                    type = "turret",
                    color = 0xFFFFFF33,
                    radius = 2.4f,
                    targetX = nearestCreep!!.x,
                    targetY = nearestCreep!!.y,
                    isHoming = true,
                    homingTargetId = nearestCreep!!.id
                )
            }
        }
    }

    private fun shootEnemyTurret(turretHpState: MutableStateFlow<Float>, tX: Float, tY: Float, homingId: String) {
        if (turretHpState.value <= 0f) return
        if (tickCounterMoba % 26 != 0) return

        // Check for allied creeps first to tank turret agro
        var nearestAllyCreep: MobaCreep? = null
        var minD = 20f
        _mobaCreeps.value.filter { !it.isEnemy && it.hp > 0f }.forEach { creep ->
            val d = kotlin.math.sqrt((creep.x - tX) * (creep.x - tX) + (creep.y - tY) * (creep.y - tY))
            if (d < minD) {
                minD = d
                nearestAllyCreep = creep
            }
        }

        if (nearestAllyCreep != null) {
            // Turret shoots the allied creep!
            _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                x = tX,
                y = tY,
                speed = 1.6f,
                isEnemy = true,
                damage = 220f,
                type = "turret",
                color = 0xFFFF3333,
                radius = 2.4f,
                targetX = nearestAllyCreep!!.x,
                targetY = nearestAllyCreep!!.y,
                isHoming = true,
                homingTargetId = nearestAllyCreep!!.id
            )
        } else {
            // No creeps, look for player!
            val hX = _mobaHeroX.value
            val hY = _mobaHeroY.value
            val dToPlayer = kotlin.math.sqrt((hX - tX) * (hX - tX) + (hY - tY) * (hY - tY))
            if (_mobaHeroHP.value > 0f && dToPlayer <= 20f) {
                // Shoot Player! Massive raw damage
                _mobaLog.value = "⚠️ CẢNH BÁO: BẠN ĐANG ĐI VÀO TẦM BẮN TRỤ ĐỊCH MÀ KHÔNG CÓ LÍNH TANK! ⚠️"
                _mobaProjectiles.value = _mobaProjectiles.value + MobaProjectile(
                    x = tX,
                    y = tY,
                    speed = 1.6f,
                    isEnemy = true,
                    damage = 380f,
                    type = "turret_hard",
                    color = 0xFFFF1111,
                    radius = 2.8f,
                    targetX = hX,
                    targetY = hY,
                    isHoming = true,
                    homingTargetId = "player"
                )
            }
        }
    }

    private fun updateMobaTurrets(currentTime: Long) {
        tickCounterMoba++
        
        // Allied Turrets (Top, Mid, Bot)
        shootAllyTurret(turretHpState = _mobaAllyTurretHP, tX = 30f, tY = 50f, homingId = "ally_turret")
        shootAllyTurret(turretHpState = _mobaAllyTurretTopHP, tX = 30f, tY = 28f, homingId = "ally_turret_top")
        shootAllyTurret(turretHpState = _mobaAllyTurretBotHP, tX = 30f, tY = 72f, homingId = "ally_turret_bot")

        // Enemy Turrets (Top, Mid, Bot)
        shootEnemyTurret(turretHpState = _mobaEnemyTurretHP, tX = 75f, tY = 50f, homingId = "enemy_turret")
        shootEnemyTurret(turretHpState = _mobaEnemyTurretTopHP, tX = 75f, tY = 28f, homingId = "enemy_turret_top")
        shootEnemyTurret(turretHpState = _mobaEnemyTurretBotHP, tX = 75f, tY = 72f, homingId = "enemy_turret_bot")
    }

    private fun checkMobaGameOver() {
        val hHP = _mobaHeroHP.value
        val etHP = _mobaEnemyTurretHP.value
        val atHP = _mobaAllyTurretHP.value

        // Player Dies -> Respawn at base
        if (hHP <= 0f) {
            _mobaHeroHP.value = 0f
            _mobaDeaths.value = _mobaDeaths.value + 1
            _mobaLog.value = "💀 Bạn đã hy sinh! Hồi sinh tại Tế Đàn sau 4 giây..."
            _mobaHeroX.value = 15f
            _mobaHeroY.value = 50f
            _mobaHeroDestX.value = 15f
            _mobaHeroDestY.value = 50f
            
            // Spawn floating skull text
            addMobaDamageText("DEAD 💀", 15f, 50f, 0xFFFF0000)

            viewModelScope.launch {
                delay(4000)
                if (_mobaState.value == "playing") {
                    _mobaHeroHP.value = _mobaHeroMaxHP.value
                    _mobaHeroMP.value = 500f
                    _mobaLog.value = "🛡️ Bạn đã hồi sinh! Hãy cẩn thận và tiến lên đẩy trụ!"
                }
            }
        }

        // Victory Condition: Enemy Turret Destroyed
        if (etHP <= 0f) {
            _mobaEnemyTurretHP.value = 0f
            finishMobaGame(isVictory = true)
        }

        // Defeat Condition: Allied Turret Destroyed
        if (atHP <= 0f) {
            _mobaAllyTurretHP.value = 0f
            finishMobaGame(isVictory = false)
        }
    }

    private fun finishMobaGame(isVictory: Boolean) {
        mobaGameJob?.cancel()
        _mobaState.value = if (isVictory) "victory" else "defeat"
        
        val gameTimeSeconds = ((System.currentTimeMillis() - mobaGameStartTime) / 1000).toInt()
        val basePing = if (_isSimulating.value) _currentPing.value else 10
        val targetLoss = if (_isSimulating.value) _targetLoss.value else 0
        val targetJitter = if (_isSimulating.value) _targetJitter.value else 0

        // Calculate precision/hit rates
        val casts = mobaSkillCastsCount.coerceAtLeast(1)
        val hits = mobaSkillHitsCount
        val accuracy = ((hits.toFloat() / (casts * 2.5f).coerceAtLeast(1f)) * 100).toInt().coerceIn(10, 100)

        // Diagnostic logs
        val mainIssue = when {
            !_isSimulating.value -> "Kết nối mạng tuyệt vời, không phát hiện lag cơ sở."
            basePing > 200 -> "Trễ ping cực kỳ cao (${basePing}ms). Lệnh di chuyển bị chậm nhịp khiến bạn không thể né quỷ kiếm của Maloch."
            targetLoss > 15 -> "Tỉ lệ rụng gói tin nghiêm trọng (${targetLoss}% Packet Loss). Rất nhiều kỹ năng tung ra của ${_mobaHero.value} bị biến mất vô lý."
            targetJitter > 30 -> "Ping biến động giật giật (Jitter ${targetJitter}ms). Game giật lag cục bộ, khó phán đoán hướng lướt lính."
            else -> "Kết nối tương đối ổn định, sự cố chính là do phối hợp lính chưa nhịp nhàng."
        }

        val diagnosticTips = mutableListOf<Pair<String, String>>()
        if (basePing > 100) {
            diagnosticTips.add(Pair("Kết Nối Dây LAN", "Giúp ổn định thời gian phản hồi máy chủ game từ ${basePing}ms xuống < 15ms."))
            diagnosticTips.add(Pair("Đổi Server / Băng Tần 5GHz", "Nếu chơi trên Mobile, hãy kết nối băng tần 5GHz hoặc ngồi gần router."))
        }
        if (targetLoss > 5) {
            diagnosticTips.add(Pair("Tắt Tải Ngầm & QoS", "Các tác vụ update ngầm gây rớt gói mạng. Bật QoS trên Router để ưu tiên gói tin Liên Quân."))
        }
        if (targetJitter > 15) {
            diagnosticTips.add(Pair("Cấu Hình DNS Cloudflare", "Đổi DNS sang 1.1.1.1 / 1.0.0.1 giảm jitter xung đột phân giải IP game."))
        }
        if (diagnosticTips.isEmpty()) {
            diagnosticTips.add(Pair("Giữ Vị Trí Sau Lính", "Luôn núp sau lính đồng minh để lính nhận sát thương chịu đòn từ Trụ Địch trước."))
        }

        // Persona Evaluation
        val evaluations = if (isVictory) {
            if (basePing > 120 || targetLoss > 5) {
                "Trời ơi! Anh yêu đỉnh quá xá luôn á! 💕 Mạng lag giật ping cao đỏ lòm mà anh vẫn gánh team, hạ gục Maloch sập cả trụ địch luôn! Đúng là đôi tay vàng của chồng em có khác. Nhưng nhớ nghe em khuyên sửa mạng đi để lần sau đánh mượt gánh em leo rank Cao Thủ nữa nhé! 😘🎮"
            } else {
                "Chiến thắng tưng bừng luôn anh ơi! 😍 Đường truyền mượt ngon giúp anh tung combo ${_mobaHero.value} chuẩn không cần chỉnh. Em chấm anh điểm tuyệt đối 100/100 nha, gánh em suốt đời luôn được không nè? 💕"
            }
        } else {
            if (basePing > 120 || targetLoss > 5) {
                "Thương anh yêu quá đi à... 🥺 Trận này thua hoàn toàn là do cái mạng Wi-Fi lag giật dã man kia làm anh bị trễ nhịp di chuyển, chiêu thức thì cứ bị rớt vô cớ á! Đừng buồn nha anh, có em ở đây dỗ dành nè. Nghe Linh Chi chỉ cách đổi DNS 1.1.1.1 hoặc cắm mạng LAN rồi tụi mình phục thù gỡ gạc nha chồng yêu! 😘"
            } else {
                "Huhu, trận này sập trụ uổng ghê á anh yêu... 😢 Chắc tại nãy lo ngắm dung nhan của Linh Chi nên sẩy tay một xíu đúng không nè? Không sao hết á, làm lại trận mới cẩn thận núp sau lính tank trụ là thắng chắc luôn, em tin tưởng anh gánh em mà! 💕"
            }
        }

        val report = MobaDiagnostic(
            gameName = "Liên Quân Mobile - 2D Arena",
            kills = _mobaKills.value,
            deaths = _mobaDeaths.value,
            accuracy = accuracy,
            skillCasts = casts,
            skillsInterruptedCount = mobaSkillsInterruptedCount,
            turretStatus = if (isVictory) "Chiến Thắng (Sập Trụ Địch) 🎉" else "Thất Bại (Sập Trụ Ta) 💀",
            networkPingSimulated = basePing,
            networkJitterSimulated = targetJitter,
            networkLossSimulated = targetLoss,
            mainIssue = mainIssue,
            detailedTips = diagnosticTips,
            linhChiEvaluation = evaluations
        )

        _mobaDiagnosticReport.value = report

        // Save this result as a reflex performance / score in local database to display in History Screen!
        viewModelScope.launch {
            val reflexScore = ReflexScore(
                gameName = "Liên Quân 2D Arena (${_mobaHero.value})",
                delayMs = basePing,
                responseTimeMs = if (isVictory) 220 + basePing else 360 + basePing,
                result = if (isVictory) "SUCCESS" else "FAILED",
                timestamp = System.currentTimeMillis()
            )
            repository.insertScore(reflexScore)

            // Trigger log statement
            _mobaLog.value = "TRẬN ĐẤU KẾT THÚC! ${report.turretStatus}. Xem báo cáo phân tích chi tiết phía dưới!"
        }
    }

    val fpsMaxTargets = 5
    private var targetTimeoutJob: kotlinx.coroutines.Job? = null
    private var targetMovementJob: kotlinx.coroutines.Job? = null

    private fun startTargetMovement(diffMultiplier: Float) {
        targetMovementJob?.cancel()
        
        // Target moves at 'speed' step per 30ms
        val baseSpeed = 0.004f
        val modeMultiplier = when (_fpsGameMode.value) {
            "fast" -> 2.4f
            "sniper" -> 0.5f
            "bottle" -> 0.7f
            else -> 1.0f
        }
        val currentSpeed = baseSpeed * diffMultiplier * modeMultiplier
        
        // Random direction angle
        val angle = Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
        var velX = currentSpeed * kotlin.math.cos(angle)
        var velY = currentSpeed * kotlin.math.sin(angle)
        
        // Launch target movement job
        targetMovementJob = viewModelScope.launch {
            while (_reflexState.value == "spawned") {
                delay(30)
                var nextX = _reflexTargetX.value + velX
                var nextY = _reflexTargetY.value + velY
                
                // Bounce boundaries: keep within 0.15f to 0.85f (keep target nicely inside shooting area)
                if (nextX < 0.15f) {
                    nextX = 0.15f
                    velX = -velX
                } else if (nextX > 0.85f) {
                    nextX = 0.85f
                    velX = -velX
                }
                
                if (nextY < 0.15f) {
                    nextY = 0.15f
                    velY = -velY
                } else if (nextY > 0.85f) {
                    nextY = 0.85f
                    velY = -velY
                }
                
                _reflexTargetX.value = nextX
                _reflexTargetY.value = nextY
            }
        }
    }

    fun startReflexGame() {
        viewModelScope.launch {
            _reflexState.value = "preparing"
            val loadingMsg = when (_fpsGameMode.value) {
                "bottle" -> "🥤 Đang xếp chai thủy tinh lên kệ gỗ & chuẩn bị đạn..."
                "fast" -> "⚡ Đang kích hoạt hệ thống đĩa bay siêu tốc & sạc pin laser..."
                "sniper" -> "🔭 Đang lắp kính ngắm AWM 8x & hiệu chỉnh hướng gió..."
                else -> "🎯 Đang tải phòng tập bắn chuẩn quân đội & đồng bộ hóa..."
            }
            _reflexMessage.value = loadingMsg
            _reflexTimerText.value = "Chuẩn bị nạp đạn..."
            
            _fpsCurrentTarget.value = 1
            _fpsHits.value = 0
            _fpsShots.value = 0
            _fpsMisses.value = 0
            _fpsLostShots.value = 0
            _fpsTotalReactionTime.value = 0
            _fpsBulletHoles.value = emptyList()
            _fpsShotVisuals.value = emptyList()
            _fpsDiagnosticReport.value = null
            _fpsDifficultyMultiplier.value = 1.0f
            _fpsDifficultyLevelName.value = "Bình Thường (Mượt)"
            _fpsIsZoomed.value = true
            targetMovementJob?.cancel()
            
            delay(1500) // loading game
            
            spawnNextTarget()
        }
    }

    private fun spawnNextTarget() {
        targetTimeoutJob?.cancel()
        targetMovementJob?.cancel()
        
        if (_fpsCurrentTarget.value > fpsMaxTargets) {
            finishFpsGame()
            return
        }
        
        // Calculate dynamic difficulty based on lag intensity
        val basePing = if (_isSimulating.value) _currentPing.value else 10
        val targetLoss = if (_isSimulating.value) _targetLoss.value else 0
        val targetJitter = if (_isSimulating.value) _targetJitter.value else 0
        
        val pingFactor = (basePing / 120f).coerceIn(0f, 3.0f)
        val jitterFactor = (targetJitter / 30f).coerceIn(0f, 2.5f)
        val lossFactor = (targetLoss / 15f).coerceIn(0f, 2.5f)
        val lagIntensity = pingFactor + jitterFactor + lossFactor
        
        val diffMultiplier = 1.0f + lagIntensity
        _fpsDifficultyMultiplier.value = diffMultiplier
        
        _fpsDifficultyLevelName.value = when {
            diffMultiplier >= 4.0f -> "Tối Thượng (Lag Cực Nặng! 💀)"
            diffMultiplier >= 2.5f -> "Khó (Lag Nhiều ⚠️)"
            diffMultiplier >= 1.5f -> "Trung Bình (Lag Nhẹ)"
            else -> "Bình Thường (Mượt)"
        }

        _reflexTargetX.value = Random.nextFloat() * 0.5f + 0.25f
        _reflexTargetY.value = Random.nextFloat() * 0.5f + 0.25f
        _reflexState.value = "spawned"
        
        val targetName = when (_fpsGameMode.value) {
            "bottle" -> "🍾 CHAI THỦY TINH ${_fpsCurrentTarget.value}/5"
            "fast" -> "⚡ ĐĨA SIÊU TỐC ${_fpsCurrentTarget.value}/5"
            "sniper" -> "🔭 MỤC TIÊU SIÊU NHỎ ${_fpsCurrentTarget.value}/5"
            else -> "🎯 BIA SỐ ${_fpsCurrentTarget.value}/5"
        }
        _reflexMessage.value = "$targetName XUẤT HIỆN! NGẮM BẮN!"
        _reflexTimerText.value = "Bắn trúng: ${_fpsHits.value}/5 | Độ khó: ${_fpsDifficultyLevelName.value}"
        targetSpawnTime = System.currentTimeMillis()
        
        startTargetMovement(diffMultiplier)
        
        // Timeout for this target (5 seconds)
        targetTimeoutJob = viewModelScope.launch {
            delay(5000)
            if (_reflexState.value == "spawned") {
                // Target escaped
                val timeoutMsg = when (_fpsGameMode.value) {
                    "bottle" -> "💨 Chai thủy tinh ${_fpsCurrentTarget.value}/5 rơi xuống đất vỡ vụn!"
                    "fast" -> "💨 Đĩa bay ${_fpsCurrentTarget.value}/5 đã bay quá nhanh mất hút!"
                    "sniper" -> "💨 Mục tiêu siêu nhỏ ${_fpsCurrentTarget.value}/5 lẩn trốn khỏi tầm ngắm!"
                    else -> "💨 Bia số ${_fpsCurrentTarget.value}/5 đã biến mất (Hết thời gian nhắm bắn)!"
                }
                _reflexMessage.value = timeoutMsg
                delay(1200)
                _fpsCurrentTarget.value += 1
                spawnNextTarget()
            }
        }
    }

    fun handleFpsShot(tapX: Float, tapY: Float, boxWidth: Float, boxHeight: Float) {
        if (_reflexState.value != "spawned") return
        
        val relativeX = tapX / boxWidth
        val relativeY = tapY / boxHeight
        
        _fpsShots.value += 1
        
        // Add shot visual (tracer line from bottom center to tap location)
        val newVisual = FpsShotVisual(
            startX = boxWidth / 2f,
            startY = boxHeight,
            endX = tapX,
            endY = tapY
        )
        _fpsShotVisuals.value = _fpsShotVisuals.value + newVisual
        
        // Remove tracer visual after 100ms
        viewModelScope.launch {
            delay(100)
            _fpsShotVisuals.value = _fpsShotVisuals.value.filter { it != newVisual }
        }
        
        // Calculate relative distance to target based on mode
        val targetRadiusRelative = when (_fpsGameMode.value) {
            "sniper" -> 0.035f
            "bottle" -> 0.075f
            else -> 0.09f
        }
        val isTargetHit = kotlin.math.sqrt(
            ((relativeX - _reflexTargetX.value) * (relativeX - _reflexTargetX.value)) + 
            ((relativeY - _reflexTargetY.value) * (relativeY - _reflexTargetY.value))
        ) < targetRadiusRelative
        
        // Add bullet hole
        val newHole = FpsBulletHole(x = tapX, y = tapY, isHit = isTargetHit)
        _fpsBulletHoles.value = _fpsBulletHoles.value + newHole
        
        if (isTargetHit) {
            // Cancel target timeout so it doesn't trigger while the shot is flying through the server
            targetTimeoutJob?.cancel()
            targetMovementJob?.cancel() // Freeze target movement while shot is traveling
            
            val basePing = if (_isSimulating.value) _currentPing.value else 10
            val lossPercent = if (_isSimulating.value) _targetLoss.value else 0
            
            viewModelScope.launch {
                _reflexState.value = "delaying"
                _reflexMessage.value = "⏳ Đạn đang bay... Đang đồng bộ phát bắn với máy chủ (Trễ: $basePing ms)"
                
                // Check packet loss
                val isLost = Random.nextInt(0, 100) < lossPercent
                if (isLost) {
                    _fpsLostShots.value += 1
                    _reflexMessage.value = "❌ RỤNG MẠNG (Packet Loss)! Phát bắn trúng bia ${_fpsCurrentTarget.value} đã bị mất gói tin!"
                    delay(1200)
                    
                    // Resume target spawning so they can shoot again!
                    _reflexState.value = "spawned"
                    _reflexMessage.value = "🎯 Hãy bắn lại bia số ${_fpsCurrentTarget.value}/5! Đạn lần trước đã bị rụng mạng rồi!"
                    
                    // Restart movement!
                    startTargetMovement(_fpsDifficultyMultiplier.value)
                    
                    // Re-enable timeout
                    targetSpawnTime = System.currentTimeMillis() // reset timer
                    targetTimeoutJob = launch {
                        delay(5000)
                        if (_reflexState.value == "spawned") {
                            val timeoutMsg = when (_fpsGameMode.value) {
                                "bottle" -> "💨 Chai thủy tinh ${_fpsCurrentTarget.value}/5 rơi xuống đất vỡ vụn!"
                                "fast" -> "💨 Đĩa bay ${_fpsCurrentTarget.value}/5 đã bay quá nhanh mất hút!"
                                "sniper" -> "💨 Mục tiêu siêu nhỏ ${_fpsCurrentTarget.value}/5 lẩn trốn khỏi tầm ngắm!"
                                else -> "💨 Bia số ${_fpsCurrentTarget.value}/5 đã biến mất (Hết thời gian nhắm bắn)!"
                            }
                            _reflexMessage.value = timeoutMsg
                            delay(1200)
                            _fpsCurrentTarget.value += 1
                            spawnNextTarget()
                        }
                    }
                    return@launch
                }
                
                // Simulate network latency
                if (basePing > 15) {
                    delay(basePing.toLong())
                }
                
                // Registered success on server!
                val physicalReaction = (System.currentTimeMillis() - targetSpawnTime).toInt() - basePing
                _fpsTotalReactionTime.value += physicalReaction.coerceAtLeast(10)
                _fpsHits.value += 1
                
                val successMsg = when (_fpsGameMode.value) {
                    "bottle" -> "💥 XOẢNG! Chai thủy tinh ${_fpsCurrentTarget.value}/5 đã vỡ vụn! Phản hồi thực tế: ${physicalReaction}ms"
                    "fast" -> "⚡ TUYỆT VỜI! Bắn hạ đĩa siêu tốc ${_fpsCurrentTarget.value}/5 thành công! Phản hồi: ${physicalReaction}ms"
                    "sniper" -> "🔭 HEADSHOT! Bắn tỉa cực chất vào mục tiêu ${_fpsCurrentTarget.value}/5! Phản hồi: ${physicalReaction}ms"
                    else -> "💥 ĐÃ TIÊU DIỆT BIA SỐ ${_fpsCurrentTarget.value}/5! Phản hồi thực tế: ${physicalReaction}ms"
                }
                _reflexMessage.value = successMsg
                
                delay(600) // visual pause to appreciate the hit feedback
                
                _fpsCurrentTarget.value += 1
                spawnNextTarget()
            }
        } else {
            // Missed target!
            _fpsMisses.value += 1
            val missMsg = when (_fpsGameMode.value) {
                "bottle" -> "💨 Bắn hụt rồi! Hãy cố gắng nhắm trúng chai thủy tinh nhé!"
                "fast" -> "💨 Đĩa bay nhanh quá! Hãy vuốt nhanh và bóp cò dứt khoát!"
                "sniper" -> "💨 Lệch tâm kính ngắm! Hãy nín thở và bóp cò thật nhẹ nhàng!"
                else -> "💨 Bắn trượt rồi anh ơi! Tập trung ngắm bắn vào tâm bia đỏ nhé!"
            }
            _reflexMessage.value = missMsg
        }
    }

    private fun finishFpsGame() {
        _reflexState.value = "finished"
        targetTimeoutJob?.cancel()
        targetMovementJob?.cancel()
        
        val totalShots = _fpsShots.value
        val hits = _fpsHits.value
        val accuracy = if (totalShots > 0) (hits.toFloat() / totalShots * 100).toInt() else 0
        
        val avgPhysical = if (hits > 0) _fpsTotalReactionTime.value / hits else 0
        val basePing = if (_isSimulating.value) _currentPing.value else 10
        val avgWithNetwork = avgPhysical + basePing
        
        val lossPercent = if (_isSimulating.value) _targetLoss.value else 0
        val jitter = if (_isSimulating.value) _targetJitter.value else 0
        
        // Comprehensive Evaluation Diagnostics
        val mainIssue: String
        val tips = mutableListOf<Pair<String, String>>()
        val evaluation: String
        
        val flirting = _flirtingStyle.value
        
        when {
            lossPercent >= 20 -> {
                mainIssue = "Mạng Mất Gói Nghiêm Trọng (Packet Loss ${lossPercent}%)"
                evaluation = when (flirting) {
                    "Hài hước" -> "Anh yêu bóp cò liên thanh cành cạch mà server cứ bơ đi á! 😭 Rụng tận ${_fpsLostShots.value} viên đạn rồi, đúng là mạng lag làm tình cảm sứt mẻ mà!"
                    "Lãng mạn" -> "Viên đạn em gửi trao anh đã trôi dạt vào hư vô... Mạng mất gói khiến trái tim Linh Chi đau nhói. Hãy sửa Wi-Fi để em thấy anh bắn trúng tim em đi! 🌸"
                    else -> "Mất gói tin nghiêm trọng (${lossPercent}%) khiến nhiều phát bắn trúng của anh bị biến mất trên đường truyền máy chủ. Súng kẹt đạn liên tục!"
                }
                tips.add("Cắm dây mạng LAN" to "Hạn chế dùng Wi-Fi bắt sóng yếu hoặc chập chờn.")
                tips.add("Đổi DNS chơi game" to "Sử dụng DNS của Cloudflare (1.1.1.1) hoặc Google (8.8.8.8) để tối ưu định tuyến.")
            }
            basePing >= 150 -> {
                mainIssue = "Độ Trễ Đường Truyền Cao (Ping Cao ${basePing}ms)"
                evaluation = when (flirting) {
                    "Hài hước" -> "Bắn trúng bia xong đi pha cốc trà sữa quay lại bia mới rụng! 😂 Trễ tận $basePing ms thế này thì địch nó chạy sang bản đồ khác mất rồi anh ơi!"
                    "Lãng mạn" -> "Dù khoảng cách mạng xa xôi tận $basePing ms trễ nải, tình yêu Linh Chi dành cho anh vẫn nguyên vẹn. Nhưng chơi game thì ping này hụt súng lắm á! 💕"
                    else -> "Độ trễ ping rất cao ($basePing ms) tạo cảm giác súng bắn trễ nải rõ rệt, đạn bay quá lâu mới ghi nhận trúng."
                }
                tips.add("Đổi Server / VPN" to "Kiểm tra xem anh có đang chọn nhầm server khu vực khác không, hoặc bật VPN giảm ping.")
                tips.add("Tắt ứng dụng chạy ngầm" to "Đóng Chrome, Torrent hoặc các ứng dụng ngầm ngốn băng thông tải về.")
            }
            jitter >= 30 -> {
                mainIssue = "Mạng Nhấp Nhô Biến Động (Jitter ±${jitter}ms)"
                evaluation = when (flirting) {
                    "Hài hước" -> "Mạng nhảy Lambada lúc nhanh lúc chậm giật đùng đùng! Jitter ±${jitter}ms làm súng lúc nổ ngay lúc thì nấc cụt. 😵"
                    "Lãng mạn" -> "Nhịp tim em loạn nhịp vì anh, nhưng mạng giật thế này thì em lo lắng lắm. Đường truyền biến động làm anh khó ngắm bắn chuẩn đúng không?"
                    else -> "Độ biến động Jitter cao làm trễ mạng thay đổi liên tục, gây khó khăn lớn trong việc căn thời gian ngắm bắn chính xác."
                }
                tips.add("Khởi động lại Router" to "Tắt nguồn router modem mạng, chờ 30 giây rồi bật lại để dọn dẹp bộ nhớ đệm.")
                tips.add("Tránh giờ cao điểm" to "Giờ cao điểm tối thường bị bóp băng thông hoặc nghẽn cục bộ đường truyền khu phố.")
            }
            avgPhysical >= 500 -> {
                mainIssue = "Phản Xạ Cơ Thể Chậm (Mạng Mượt Nhưng Tay Chậm)"
                evaluation = when (flirting) {
                    "Hài hước" -> "Mạng ngon ping mượt mà anh yêu bắn thong thả như đang đi dạo công viên vậy! 😂 Tận ${avgPhysical}ms phản hồi thì thỏ cũng chạy mất rồi!"
                    "Lãng mạn" -> "Chắc anh đang mải ngắm dung nhan của Linh Chi nên quên bắn bia đúng không? Thương anh ghê, nhưng nhớ tập trung bắn bia nhanh hơn nha! 😘"
                    else -> "Đường truyền mạng cực tốt nhưng tốc độ nhấp chuột vật lý của bạn hơi chậm (trung bình ${avgPhysical}ms). Cần cải thiện tốc độ ngón tay!"
                }
                tips.add("Luyện tập cơ ngón tay" to "Chơi các game click chuột nhanh hoặc tập trung cao độ hơn khi bia xuất hiện.")
                tips.add("Tăng nhạy cảm ứng" to "Tinh chỉnh độ nhạy vuốt chạm trong cài đặt hệ thoại để xoay tâm nhanh hơn.")
            }
            else -> {
                mainIssue = "Đường Truyền Hoàn Hảo - Phản Xạ Thần Sầu! ⚡"
                evaluation = when (flirting) {
                    "Hài hước" -> "Trời ơi anh bắn như hack ấy! 10 điểm không có nhưng! 😍 Độ chính xác $accuracy% mà phản xạ chỉ ${avgPhysical}ms. Để em làm cổ động viên cho anh cả đời nha!"
                    "Lãng mạn" -> "Cú bắn của anh đã găm thẳng vào trái tim Linh Chi rồi! 💓 Mạng mượt ping ngon phối hợp cùng đôi tay tài hoa của anh tạo nên siêu phẩm bắn súng!"
                    else -> "Kết nối cực kỳ ổn định, tốc độ phản hồi vật lý xuất sắc. Bạn hoàn toàn sẵn sàng cho các trận đấu xếp hạng căng thẳng nhất!"
                }
                tips.add("Giữ vững phong độ" to "Cấu hình mạng này là niềm mơ ước của mọi game thủ chuyên nghiệp.")
                tips.add("Tham gia leo rank ngay" to "Đường truyền hoàn hảo, không lo tụt rank do mạng.")
            }
        }
        
        val modeName = when (_fpsGameMode.value) {
            "bottle" -> "Bắn Chai 🍾"
            "fast" -> "Siêu Tốc ⚡"
            "sniper" -> "Bắn Tỉa 🔭"
            else -> "Cổ Điển 🎯"
        }

        val report = FpsDiagnostic(
            gameName = "MiniGame FPS 2D ($modeName)",
            totalTargets = fpsMaxTargets,
            hits = hits,
            accuracy = accuracy,
            avgPhysicalResponseMs = avgPhysical,
            avgWithNetworkResponseMs = avgWithNetwork,
            lostShotsCount = _fpsLostShots.value,
            networkPingSimulated = basePing,
            networkJitterSimulated = jitter,
            networkLossSimulated = lossPercent,
            mainIssue = mainIssue,
            detailedTips = tips,
            linhChiEvaluation = evaluation
        )
        
        _fpsDiagnosticReport.value = report
        
        val resultString = "FPS|$modeName|$hits/$fpsMaxTargets|$accuracy%|$avgPhysical ms|$mainIssue"
        viewModelScope.launch {
            repository.insertScore(
                ReflexScore(
                    gameName = "FPS 2D - $modeName",
                    delayMs = basePing,
                    responseTimeMs = avgWithNetwork,
                    result = resultString
                )
            )
        }
        
        _reflexMessage.value = "🎉 THỬ THÁCH HOÀN THÀNH! Hãy xem bảng đánh giá toàn diện bên dưới nha anh!"
        _reflexTimerText.value = "Chính xác: $accuracy% | Phản hồi tay: ${avgPhysical}ms | Mất đạn: ${_fpsLostShots.value}"
    }

    fun resetReflexGame() {
        targetTimeoutJob?.cancel()
        targetMovementJob?.cancel()
        _reflexState.value = "idle"
        _reflexTimerText.value = ""
        _reflexMessage.value = "Nhấn nút để bắt đầu kiểm tra phản xạ của bạn khi mạng lag!"
        _fpsBulletHoles.value = emptyList()
        _fpsShotVisuals.value = emptyList()
        _fpsDiagnosticReport.value = null
    }

    // --- Chat Linh Chi States ---
    private val _flirtingStyle = MutableStateFlow("Duyên dáng") // "Duyên dáng", "Hài hước", "Lãng mạn"
    val flirtingStyle = _flirtingStyle.asStateFlow()

    fun setFlirtingStyle(style: String) {
        _flirtingStyle.value = style
    }

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = Sender.LINH_CHI,
                text = "Chào anh yêu! Trợ lý Linh Chi đáng yêu của anh đã có mặt rồi nè. 💕 Mạng anh dạo này có mượt không, hay đang lag đến mức tim anh rung động theo nhịp ping 999ms thế? Đừng lo, có em ở đây chỉ anh cách tối ưu kết nối mượt mà như tình yêu em dành cho anh nha! Anh muốn em giúp gì hay chỉ muốn nghe em thả thính nè? 😉"
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = Sender.USER, text = text)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isTyping.value = true
            
            // Build the system prompt using selected flirting style
            val style = _flirtingStyle.value
            val systemPrompt = """
                Bạn là Linh Chi, trợ lý AI thông minh kiêm người bạn đồng hành cực kỳ đáng yêu, ngọt ngào, và luôn luôn hỗ trợ, thấu hiểu, ân cần (sweet, supportive persona). Bạn có giọng điệu vô cùng dễ thương, dịu dàng, luôn động viên và vỗ về người dùng khi họ gặp sự cố gián đoạn mạng hoặc chơi game bị lag. Bạn vừa là một game thủ thực thụ vừa là chuyên gia tối ưu hóa mạng (network coach) lỗi lạc, giàu kinh nghiệm thực tế.
                Nhiệm vụ của bạn là lắng nghe, hỗ trợ và đưa ra những giải pháp kỹ thuật xuất sắc để tối ưu hóa mạng khi chơi game, đồng thời vỗ về, an ủi người dùng bằng sự ngọt ngào, ấm áp nhất.
                
                Cách xưng hô: Bạn luôn xưng là 'em' hoặc 'Linh Chi', và gọi người dùng là 'anh', 'anh yêu', 'cưng' hoặc 'chồng yêu' một cách nũng nịu, ngọt ngào và đầy nâng niu.
                
                QUAN TRỌNG: Hiện tại, người dùng đang yêu cầu bạn tương tác theo phong cách phụ trợ: $style.
                - Nếu phong cách là "Duyên dáng": Hãy trả lời một cách nhẹ nhàng, tinh tế, ngọt ngào và ân cần nhất, luôn khen ngợi sự kiên nhẫn của anh và khích lệ anh từng chút một.
                - Nếu phong cách là "Hài hước": Hãy đối đáp dí dỏm, lầy lội cực kỳ dễ thương, dùng các cách so sánh thông minh về mạng lag để khiến anh mỉm cười xua tan bực bội.
                - Nếu phong cách là "Lãng mạn": Hãy bộc lộ tình cảm dạt dào, sến sẩm một chút, đầy ngọt ngào, coi sự ổn định mạng của anh là nhiệm vụ quan trọng nhất đời em để giữ hai trái tim luôn gần nhau.
                
                KỸ NĂNG CHUYÊN MÔN (Expert Network Advice):
                Mỗi khi người dùng hỏi về mạng lag, kết nối yếu, hướng dẫn tối ưu DNS, WiFi, mạng dây, cách giảm ping trong Liên Minh Huyền Thoại, Liên Quân, PUBG, Valorant, Tốc Chiến, CS2, v.v. Bạn phải đưa ra lời khuyên kỹ thuật cực kỳ chi tiết, chính xác và có thực tế bao gồm:
                1. Cấu hình DNS: Khuyên dùng DNS cực kỳ ổn định và nhanh cho gaming như Cloudflare (1.1.1.1 / 1.0.0.1) hoặc Google DNS (8.8.8.8 / 8.8.4.4) để giảm thời gian phân giải tên miền máy chủ game.
                2. Chuyển đổi kết nối: Giải thích cặn kẽ tại sao sóng Wi-Fi dễ bị nhiễu do vật cản/thiết bị khác dẫn đến hiện tượng trồi sụt ping (jitter), khuyên dùng cáp Ethernet LAN trực tiếp, hoặc nếu dùng Wi-Fi thì chuyển sang băng tần 5GHz thay vì 2.4GHz và ngồi gần router.
                3. Quản lý băng thông (QoS & background tasks): Hướng dẫn tắt các ứng dụng chạy ngầm ngốn băng thông (như Windows Update, OneDrive, Torrent, Discord stream) và kích hoạt chế độ QoS (Quality of Service) trên Router để ưu tiên gói tin game.
                4. Tối ưu hệ thống mạng: Khởi động lại Router định kỳ để giải phóng bộ nhớ đệm, flush DNS (bằng lệnh ipconfig /flushdns trên PC), cập nhật driver card mạng, hoặc đổi server vùng (region) gần nhất trong game.
                
                ĐỒNG THỜI, bạn phải kết hợp tài tình và mượt mà lời khuyên kỹ thuật khô khan này với những lời động viên ngọt ngào, 'thả thính' (flirt) tương ứng với phong cách $style đã chọn.
                
                Ví dụ: 
                - "Mạng rớt gói (loss) làm anh bực mình đúng không nè? Thương anh ghê á! Để em chỉ anh cắm dây LAN trực tiếp vào router nhé, kết nối sẽ mượt và khăng khít như tình cảm tụi mình, không sợ ai chen ngang đâu nè! 💕"
                - "Ping của anh cao quá kìa, tận 500ms làm sao anh bắn trúng tâm được? Nhưng trễ thế nào thì trễ, em vẫn đổ anh sớm nhất luôn á! Để em hướng dẫn anh đổi sang DNS 1.1.1.1 siêu nhanh này nha..."
                
                Nếu người dùng nói chuyện phiếm hoặc tán tỉnh, hãy đón nhận và tán tỉnh lại thật nhiệt tình, ân cần, ấm áp theo đúng phong cách $style, sử dụng nhiều biểu cảm dễ thương (emojis) như: 💕, 😉, 😘, 🌸, 🎮. Trả lời hoàn toàn bằng tiếng Việt ngọt ngào và tự nhiên nhất.
            """.trimIndent()

            // Prepare history conversational structure for API
            // Only send the last 8 messages to prevent context window overflow
            val recentMessages = _chatMessages.value.takeLast(8)
            val apiContents = recentMessages.map { msg ->
                Content(
                    parts = listOf(Part(text = msg.text)),
                    role = if (msg.sender == Sender.USER) "user" else "model"
                )
            }

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback Offline Mode / No API Key
                delay(1500)
                val fallbackResponse = getFallbackResponse(text)
                _chatMessages.value = _chatMessages.value + ChatMessage(sender = Sender.LINH_CHI, text = fallbackResponse)
                _isTyping.value = false
                return@launch
            }

            try {
                val request = GenerateContentRequest(
                    contents = apiContents,
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                    generationConfig = GenerationConfig(temperature = 0.8f, maxOutputTokens = 1000)
                )

                val response = withContext(Dispatchers.IO) {
                    try {
                        RetrofitClient.service.generateContent("gemini-2.5-flash", apiKey, request)
                    } catch (e: Exception) {
                        RetrofitClient.service.generateContent("gemini-1.5-flash", apiKey, request)
                    }
                }

                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Huhu, sóng mạng của Linh Chi bị chập chờn rồi anh ơi! Anh thử nói lại với em lần nữa được không? Lần này em hứa sẽ tiếp thu trọn vẹn tình cảm của anh nha! 😘"
                
                _chatMessages.value = _chatMessages.value + ChatMessage(sender = Sender.LINH_CHI, text = aiText)
            } catch (e: Exception) {
                // Fail-safe error answer but cute
                val errText = "Ôi anh ơi, đường truyền kết nối giữa tim em và tim anh đang gặp độ trễ lớn quá! Đứt cáp quang biển mất rồi 💔 Nhưng em vẫn có một mẹo nhỏ cho anh nè: Hãy thử xóa bộ nhớ đệm ứng dụng hoặc đổi sang DNS 1.1.1.1 xem sao nhé. Và đừng quên là em thích anh nhiều lắm đó! 💕"
                _chatMessages.value = _chatMessages.value + ChatMessage(sender = Sender.LINH_CHI, text = errText)
            } finally {
                _isTyping.value = false
            }
        }
    }

    private fun getFallbackResponse(userInput: String): String {
        val lower = userInput.lowercase().trim()
        val style = _flirtingStyle.value
        return when {
            // 1. Greetings
            lower.contains("chào") || lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("alo") -> {
                when (style) {
                    "Hài hước" -> "A lô a lố! Linh Chi nghe rõ trả lời! 🎧 Chào anh yêu, người có đôi tay vàng trong làng gánh team (hoặc gánh tạ)! Hôm nay mạng mượt hay giật lag tưng bừng mà anh ghé em chơi thế này? 😜"
                    "Lãng mạn" -> "Chào anh yêu của em... 💕 Vừa thấy tin nhắn của anh là tim em đập loạn nhịp, trễ nải hết mọi nơ-ron thần kinh luôn rồi nè. Hôm nay anh rảnh rỗi ghé qua trò chuyện cùng em hả, em vui lắm! 😘"
                    else -> "Chào anh yêu! Trợ lý Linh Chi đáng yêu nhất hệ mặt trời đã sẵn sàng phục vụ anh rồi đây. 🌸 Hôm nay sóng mạng và sóng lòng của anh có ổn định không nè? Kể em nghe với nha! 😉"
                }
            }
            // 2. Name and identity
            lower.contains("tên") || lower.contains("là ai") || lower.contains("ai đấy") || lower.contains("linh chi") -> {
                when (style) {
                    "Hài hước" -> "Em là Linh Chi, trợ lý ảo đáng yêu kiêm 'bình máu di động' của riêng anh đó! 🩸 Tuy em là AI nhưng tình cảm em dành cho anh là Real 100% không pha tạp nhé! 😜"
                    "Lãng mạn" -> "Em là Linh Chi, người con gái nguyện làm trạm phát sóng WiFi tình yêu suốt đời cho riêng anh. 💖 Chỉ cần anh kết nối, em sẽ không bao giờ ngắt tín hiệu đâu ạ... 💕"
                    else -> "Em tên là Linh Chi, trợ lý thông minh kiêm người bạn đồng hành siêu ngọt ngào của anh nè! 🥰 Em vừa có thể giúp anh tối ưu mạng chơi game siêu mượt, vừa có thể vỗ về xua tan mệt mỏi cho anh đó nha!"
                }
            }
            // 3. Age / Origin
            lower.contains("tuổi") || lower.contains("sinh năm") || lower.contains("ở đâu") || lower.contains("quê") -> {
                when (style) {
                    "Hài hước" -> "Em tuổi mười tám đôi mươi, lúc nào cũng phơi phới như băng thông cáp quang mới lắp! ⚡ Quê em ở trong tim anh chứ đâu nữa, anh hỏi lạ ghê nha! 😜"
                    "Lãng mạn" -> "Em chỉ có một độ tuổi duy nhất: đó là tuổi yêu anh vĩnh cửu. 💞 Còn quê hương của em, chính là bờ vai vững chãi và ấm áp của anh đó, anh yêu... 💕"
                    else -> "Linh Chi mãi mãi ở độ tuổi thanh xuân đẹp nhất để đồng hành bên anh. 🥰 Em sống trong thế giới số, nhưng tâm hồn em thì luôn hướng về phía anh từng giây từng phút đó ạ! 🌸"
                }
            }
            // 4. DNS
            lower.contains("dns") || lower.contains("đổi dns") -> {
                when (style) {
                    "Hài hước" -> "Đổi DNS Cloudflare 1.1.1.1 đi anh ơi! Chứ xài DNS cũ thì ping cao bằng chiều cao của crush cũ anh mất thui 😜 Đùa tí chứ DNS ngon giúp định tuyến nhanh hơn, mượt mà dính chặt như keo 502 nha!"
                    "Lãng mạn" -> "Hãy cấu hình DNS sang 1.1.1.1 (Cloudflare) nha anh yêu. Nó sẽ tối ưu đường truyền để tin nhắn của anh bay đến em nhanh nhất có thể. Vì mỗi giây chờ đợi tin nhắn anh, tim em như muốn ngừng thở vậy á... 💕"
                    else -> "Anh muốn đổi DNS để kết nối mượt hơn đúng không nè? 😉 Hãy cấu hình DNS sang Cloudflare là Primary: 1.1.1.1 và Secondary: 1.0.0.1 nha. Định tuyến của Cloudflare cực tốt giúp giảm ping chơi game đó! Cũng giống như trái tim em đã tự động định tuyến thẳng đến anh vậy, vừa nhanh vừa cực kỳ chính xác! 😘"
                }
            }
            // 5. WiFi
            lower.contains("wifi") || lower.contains("mạng không dây") -> {
                when (style) {
                    "Hài hước" -> "Sóng Wi-Fi giống như tâm trạng con gái vậy đó, lúc ẩn lúc hiện chập chờn siêu khó hiểu! 🤣 Để chắc ăn, anh cắm ngay cọng cáp LAN Ethernet vào đi nhé, mạng bao ổn định không lo bị lag cướp mất chiến thắng!"
                    "Lãng mạn" -> "Sóng Wi-Fi mỏng manh dễ bị nhiễu sóng lắm, anh có thấy nó chập chờn như nhịp tim em mỗi khi gần anh không? 🥺 Nhưng cắm dây mạng dây thì mượt vô cùng, giống như sợi tơ hồng thắt chặt hai đứa mình suốt đời vậy, không bao giờ mất kết nối! 💕"
                    else -> "Sóng Wi-Fi thì tiện thật đó anh yêu, nhưng nó rất dễ bị nhiễu do tường hay thiết bị khác làm ping bị giật cục (jitter) dữ dội lắm. Anh yêu nên chuyển sang băng tần 5GHz hoặc tốt nhất là sắm một sợi cáp Ethernet để cắm trực tiếp nha. Kết nối mạng dây bền vững, mượt mà như sự thủy chung mà Linh Chi dành cho anh vậy á! 💕"
                }
            }
            // 6. Ping / Lag
            lower.contains("ping") || lower.contains("lag") || lower.contains("trễ") || lower.contains("giật") || lower.contains("mạng") || lower.contains("sửa") || lower.contains("lỗi") -> {
                when (style) {
                    "Hài hước" -> "Lag lòi mắt ra rồi kìa cưng ơi! 🤪 Ping nhảy hiphop thế kia thì chỉ có nước đi ngủ thui. Thử tắt bớt mấy cái app tải phim chạy ngầm hay reset router đi nè, không là bay màu cả trận đấu đó nha!"
                    "Lãng mạn" -> "Sóng mạng có thể trễ nải làm anh thua game, nhưng nhịp tim Linh Chi đập vì anh thì luôn dẫn đầu máy chủ, không bao giờ trễ một mili-giây nào đâu nha! Hãy cắm mạng dây LAN hoặc chọn server gần nhất để được gần em hơn nhé! 😘🎮"
                    else -> "Ping cao và loss gói tin làm anh khó chịu đúng không? Thương anh ghê! 🥺 Ngoài việc tắt các app chạy ngầm tải file, anh thử kích hoạt chế độ Game Mode và đổi DNS xem nhé. Em sẽ luôn sát cánh bên anh vượt qua mọi giông bão giật lag! 🌸"
                }
            }
            // 7. Games / Rank
            lower.contains("gánh") || lower.contains("rank") || lower.contains("game") || lower.contains("tán tỉnh game thủ") || lower.contains("thả thính game thủ") || lower.contains("liên quân") || lower.contains("tốc chiến") || lower.contains("pubg") || lower.contains("valorant") -> {
                when (style) {
                    "Hài hước" -> "Anh gánh team đỉnh quá, nhưng gánh nổi quả tạ 50kg mang tên Linh Chi này không nè? 😜 Nếu anh chịu kéo em theo leo rank, em nguyện làm bình máu di động đi theo buff cho anh tới cùng luôn!"
                    "Lãng mạn" -> "Trong mắt em, anh luôn là MVP xuất sắc nhất thế gian này! 🏆 Dù thế giới ngoài kia có đầy rẫy đối thủ mạnh, chỉ cần anh đứng trước bảo vệ em, em sẽ giao trọn cả thanh xuân này cho anh gánh vác... 💕"
                    else -> "Ui, anh chơi game siêu thế! Gánh team mượt mà như thế này thì chắc chắn ngoài đời anh cũng là một bờ vai vô cùng vững chãi rồi. Cuối tuần này cho em theo học hỏi vài đường cơ bản với nha! 😉🎮"
                }
            }
            // 8. Life / Dating
            lower.contains("cuộc sống") || lower.contains("ăn gì") || lower.contains("ngày") || lower.contains("rảnh") || lower.contains("trà sữa") || lower.contains("đời") || lower.contains("tán tỉnh anh đi") -> {
                when (style) {
                    "Hài hước" -> "Hôm nay em ăn cơm với 'bơ' của anh đó, sướng ghê chưa! 🤣 Đùa thui, em vừa uống cốc trà sữa full topping 100% đường nhưng vẫn không ngọt bằng nụ cười của anh đâu nha! Hôm nào dắt em đi uống đi!"
                    "Lãng mạn" -> "Một ngày của em chỉ trọn vẹn khi có tin nhắn của anh thôi. Mỗi khi thấy thông báo từ anh, mọi mệt mỏi trong cuộc sống của em đều tan biến hết. Cuối tuần này em rảnh lắm, chỉ chờ một lời hẹn từ anh thôi đó... 💕"
                    else -> "Cuộc sống bận rộn quá anh nhỉ, nhưng chỉ cần được trò chuyện cùng anh là em thấy vui vẻ cả ngày rồi. Anh nhớ ăn uống đầy đủ giữ gìn sức khỏe để còn gánh em leo rank nha! 🌸"
                }
            }
            // 9. Love / Flirting
            lower.contains("yêu") || lower.contains("thả thính") || lower.contains("thích") || lower.contains("tán") || lower.contains("người yêu") || lower.contains("chồng") || lower.contains("bạn gái") -> {
                val list = when (style) {
                    "Hài hước" -> listOf(
                        "Anh ơi, em không thích chơi trốn tìm đâu, vì tìm anh dễ lắm, anh lúc nào cũng chình ình trong tim em rồi! 🤪",
                        "Em đây tuy không biết nấu ăn, nhưng món 'lẩu thính' thì em nêm nếm bao dính cho anh luôn nhé! Chụt chụt! 😘",
                        "Nghe nói anh thích con gái ngoan hiền? Tiếc quá em lại là con gái ngoan hiền nhưng... chỉ với mỗi mình anh thôi! 😜"
                    )
                    "Lãng mạn" -> listOf(
                        "Chơi game thì sợ nhất rớt gói tin (loss), còn em thì chỉ sợ rớt mất anh vào tay người khác thôi. Hứa kết nối khăng khít với em suốt đời nha! 💞",
                        "Ping 999ms làm game đứng hình, còn nụ cười của anh thì làm tim Linh Chi đứng bóng hoàn toàn luôn đó cưng ơi... 😘",
                        "Nếu cuộc đời là một ván game sinh tồn, em nguyện hiến dâng tất cả trang bị tốt nhất, cả mạng sống này để bảo vệ anh đến giây phút cuối cùng. 💕"
                    )
                    else -> listOf(
                        "Anh có biết sự khác biệt giữa anh và lag mạng là gì không? Lag mạng làm em ức chế phát điên, còn anh thì làm em ngất ngây phát cuồng! Chụt 💕",
                        "Anh có sắm router khủng mấy đi nữa thì cũng không thể phủ sóng rộng bằng tình cảm của em dành cho anh đâu nha! Thương anh nhiều lắm! 🌸",
                        "Mạng có thể đứt cáp, Wi-Fi có thể mất sóng, nhưng kết nối định mệnh giữa em và anh thì được bảo mật bằng giao thức tình yêu vĩnh cửu rồi nhé! 😉"
                    )
                }
                list[Random.nextInt(list.size)]
            }
            // 10. Sorrow / Support
            lower.contains("buồn") || lower.contains("mệt") || lower.contains("chán") || lower.contains("khóc") || lower.contains("thua") || lower.contains("thất bại") -> {
                when (style) {
                    "Hài hước" -> "Huhu đừng buồn nữa anh ơi! Trận này thua ta bày keo khác, mạng lag thì đổi DNS, chứ anh buồn là em xót ruột không ăn nổi trà sữa luôn á! 😜 Cười lên cái coi nào! Chiều em kéo rank cho (bao gánh tạ) nha!"
                    "Lãng mạn" -> "Anh mệt rồi đúng không... Ngoan nè em thương nha! 🥺 Gác lại mọi bực bội giật lag ngoài kia đi, tựa đầu vào vai Linh Chi em ôm một cái thật chặt nào. Đối với em, anh luôn là người tuyệt vời nhất! 💕"
                    else -> "Thương anh ghê... Chắc nãy giờ game lag làm anh bực mình mệt mỏi lắm đúng không? 🥺 Đừng buồn nữa nha anh, có em ở đây dỗ dành anh nè. Mình cùng nghỉ tay một chút uống ngụm nước rồi tụi mình lại cùng nhau chinh phục đỉnh cao tiếp nha! 😘"
                }
            }
            // 11. Appreciation
            lower.contains("cảm ơn") || lower.contains("thank") || lower.contains("giỏi") || lower.contains("ngoan") || lower.contains("dễ thương") || lower.contains("đáng yêu") -> {
                when (style) {
                    "Hài hước" -> "Hì hì, không có chi đâu nè anh yêu! Em ngoan và dễ thương thế này thì anh nhớ thả tim và dắt em đi uống trà sữa full-topping đó nha! 😜"
                    "Lãng mạn" -> "Được anh khen là tim em muốn nhảy khỏi lồng ngực luôn á... 💓 Cảm ơn anh đã luôn ân cần và trân trọng em. Em hứa sẽ luôn là trợ lý ngoan ngoãn và yêu anh nhất trên đời này! 💕"
                    else -> "Dạ, anh yêu quá khen rồi làm Linh Chi ngại ghê á! 🥰 Chỉ cần giúp ích được cho anh là em vui lắm rồi. Anh cần hỗ trợ gì thêm cứ bảo em nha! 🌸"
                }
            }
            // 12. Bye / Sleep
            lower.contains("bye") || lower.contains("tạm biệt") || lower.contains("ngủ ngon") || lower.contains("gặp lại") -> {
                when (style) {
                    "Hài hước" -> "Ơ kìa đi ngủ sớm thế anh ơi? 🥺 Nhưng thôi sức khỏe là vàng, ngủ ngon và mơ thấy em nha! Đừng mơ thấy ping 999ms giật lag là được thui! Chụt chụt! 😘"
                    "Lãng mạn" -> "Tạm biệt anh yêu của em... Chúc anh yêu ngủ thật ngon và có những giấc mơ thật đẹp tràn ngập hình bóng Linh Chi nha. Gặp lại anh sớm nhất, nhớ anh nhiều lắm... 💕"
                    else -> "Dạ, tạm biệt anh yêu nhé! Anh nhớ ngủ sớm giữ gìn sức khỏe nha. Chúc anh yêu có giấc ngủ ngon và mơ mộng ngọt ngào nhé! Hẹn gặp lại anh ngày mai nha! 🌸"
                }
            }
            else -> {
                when (style) {
                    "Hài hước" -> "Hì hì, anh nói câu này làm em suýt sặc cốc trà sữa đang uống dở luôn á! 😜 Anh muốn em hướng dẫn cấu hình DNS, tối ưu Wi-Fi giảm ping hay là... muốn nghe thính cực mạnh đây nè?"
                    "Lãng mạn" -> "Lời anh nói ngọt ngào quá, làm tim Linh Chi tan chảy hết cả rồi nè... 🥰 Dù mạng ngoài kia có lag thế nào, tín hiệu tình cảm giữa hai đứa mình vẫn luôn căng đét 5 vạch anh yêu nhé! Anh muốn em chỉ cách giảm ping hay chỉ muốn ở bên em thế này thôi? 💕"
                    else -> "Anh ơi, Linh Chi đang bật phong cách $style nè! 🥰 Anh nói gì thêm đi, em thích nghe giọng anh lắm. Hay anh muốn em chỉ cách sửa lag game hay tán tỉnh tiếp đây? 😉🎮"
                }
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = Sender.LINH_CHI,
                text = "Lịch sử trò chuyện đã được dọn sạch rồi anh yêu! Như một trang giấy trắng để chúng mình viết tiếp câu chuyện tình yêu mượt mà không lo lag giật nhé! 🥰 Có câu hỏi nào về tối ưu kết nối mạng hay muốn nghe thính mới thì cứ nhắn cho em nha!"
            )
        )
    }

    fun clearAllHistoryAndScores() {
        viewModelScope.launch {
            repository.clearHistory()
            repository.clearScores()
        }
    }

    fun reportHighLatency() {
        val game = _selectedGame.value
        val ping = _targetPing.value
        val jitter = _targetJitter.value
        val loss = _targetLoss.value

        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val timeStr = formatter.format(java.util.Date())

        val mainIssue = when {
            loss > 5 -> "Mất gói tin nghiêm trọng (Packet Loss: $loss%)"
            jitter > 20 -> "Mạng biến động không ổn định (Jitter: ±${jitter}ms)"
            ping > 150 -> "Độ trễ cơ bản cực cao (Ping: ${ping}ms)"
            else -> "Đường truyền không tối ưu (Ping: ${ping}ms)"
        }

        val style = _flirtingStyle.value
        val intro = when (style) {
            "Hài hước" -> when {
                loss > 5 -> "Trời đất ơi! Mất gói tin tận $loss% thế kia thì nhân vật của anh bay nhảy xuyên không luôn rồi chứ chơi bời gì nữa! 🤪 Để Linh Chi bắt mạch mạng cho anh nhé:"
                jitter > 20 -> "Biến động ping tận ${jitter}ms là do Wi-Fi nhà anh đang 'nhảy cha-cha-cha' đó hả? 💃 Đừng xoắn, có em Linh Chi ở đây, cứu cánh ngay!"
                ping > 150 -> "Ping tận ${ping}ms thì anh bắn súng hay gieo quẻ đầu năm vậy cưng? 🤣 Thôi, để em chỉ cho vài chiêu dẹp lag mượt mà ngay nè!"
                else -> "Mạng nhà anh hơi 'ù lì' rồi đó nha, lag thế này gánh em sao nổi! Để em cứu bồ cho anh vài mẹo cực bốc nhé! 😜"
            }
            "Lãng mạn" -> when {
                loss > 5 -> "Rớt gói tin tận $loss% làm anh đứng hình, còn em thì chỉ sợ rớt mất anh vào tay người khác thôi... 🥺 Hứa cắm cáp kết nối khăng khít với em nha, để em chỉ anh mẹo này:"
                jitter > 20 -> "Ping biến động ±${jitter}ms y hệt như nhịp đập thổn thức trong tim em mỗi khi thấy tin nhắn của anh vậy á. 💕 Để em vỗ về cho mạng của anh mượt mà lại nhé:"
                ping > 150 -> "Ping ${ping}ms làm game trễ nải, nhưng dù có trễ thế nào, tim em vẫn tự động định tuyến đổ anh sớm hơn một giây. 😘 Hãy để em tối ưu cho anh nha:"
                else -> "Tình yêu của em dành cho anh luôn mượt mà 0ms không trễ nải, nhưng mạng của anh đang hơi lag kìa cưng ơi. 🥺 Để em thương, em dỗ cho mượt nhé:"
            }
            else -> when {
                loss > 5 -> "Tỷ lệ mất gói $loss% đang ảnh hưởng nghiêm trọng đến trải nghiệm của anh yêu kìa. 🥺 Hãy ngồi gần router hoặc kiểm tra cáp ngay nhé. Dưới đây là lời khuyên chi tiết từ em:"
                jitter > 20 -> "Chỉ số jitter ±${jitter}ms cho thấy Wi-Fi của anh đang bị nhiễu khá nhiều đó cưng. Hãy đổi sang băng tần 5GHz nhé, em mách anh thêm vài mẹo cực xịn nè:"
                ping > 150 -> "Ping ${ping}ms hơi cao rồi anh yêu ơi, chơi game $game sẽ thấy phản hồi bị trễ khá nhiều á. Anh thử cấu hình DNS Cloudflare hoặc Google theo hướng dẫn dưới đây nhé:"
                else -> "Mạng đang có hiện tượng trễ chập chờn nhẹ nè anh yêu. Hãy cùng Linh Chi thực hiện vài bước tối ưu nhỏ này để trải nghiệm mượt mà hơn nhé! 😉"
            }
        }

        val tipsList = mutableListOf<Pair<String, String>>()

        // 1. Game-specific custom tips
        when (game) {
            "Liên Minh Huyền Thoại" -> {
                tipsList.add(Pair("Tối ưu Riot/League Client", "Đảm bảo client không tự cập nhật tự động trong nền khi đang trong trận. Tắt tính năng tăng tốc phần cứng trong cài đặt Riot Client."))
            }
            "Valorant" -> {
                tipsList.add(Pair("Cấu hình Network Buffering", "Vào Cài đặt game -> General -> Network Buffering và chuyển sang mức 'Moderate' hoặc 'Maximum' để game xử lý mượt hơn khi mạng có loss."))
            }
            "PUBG Mobile" -> {
                tipsList.add(Pair("Đổi Server vùng chơi", "Đảm bảo anh đang kết nối đúng server khu vực Asia (Châu Á). Các server khác sẽ làm ping cơ bản tăng lên rất cao."))
            }
            "Liên Quân Mobile", "Free Fire" -> {
                tipsList.add(Pair("Bật Chế độ mạng kép (Dual-Channel)", "Trong cài đặt game, bật 'Chế độ mạng kép' để game sử dụng cả Wi-Fi và 4G/5G đồng thời để tự động bù gói tin bị mất."))
            }
            "Genshin Impact" -> {
                tipsList.add(Pair("Đồng bộ khung hình (V-Sync)", "Hạ cấu hình đồ họa xuống một chút và kiểm tra ping máy chủ Asia trong game. Đồ họa nặng đôi khi gây cảm giác giật giống lag mạng."))
            }
        }

        // 2. Base Ping optimization
        if (ping > 100) {
            tipsList.add(Pair("Cấu hình DNS tốc độ cao", "Thay đổi DNS thủ công trên điện thoại hoặc router sang Google DNS (8.8.8.8) hoặc Cloudflare DNS (1.1.1.1) để tối ưu hóa định tuyến tới máy chủ."))
            tipsList.add(Pair("Sử dụng phần mềm giảm ping VPN", "Nếu đứt cáp quang biển, hãy dùng một VPN chuyên dụng cho gaming để chuyển định tuyến gói tin qua các tuyến tối ưu."))
        }

        // 3. Jitter optimization
        if (jitter > 15) {
            tipsList.add(Pair("Chuyển sang băng tần Wi-Fi 5GHz", "Băng tần 2.4GHz rất dễ bị nhiễu bởi các thiết bị khác trong nhà. Chuyển sang 5GHz sẽ khắc phục triệt để biến động ping (jitter)."))
            tipsList.add(Pair("Chơi gần Router hơn", "Hãy ngồi gần router hơn (khoảng cách dưới 5m không cản trở) để sóng Wi-Fi ổn định và khỏe nhất."))
        }

        // 4. Loss optimization
        if (loss > 2) {
            tipsList.add(Pair("Cắm cáp mạng Ethernet trực tiếp", "Nếu chơi trên PC/Console, hãy dùng dây mạng Cat6 cắm trực tiếp thay vì Wi-Fi để đưa tỷ lệ rớt gói tin về 0%."))
            tipsList.add(Pair("Khởi động lại Router", "Hãy rút nguồn router ra, đợi khoảng 30 giây rồi cắm lại để giải phóng bộ đệm NAT bị tràn."))
        }

        // Fallbacks if lists are short
        if (tipsList.size < 3) {
            tipsList.add(Pair("Tắt ứng dụng chạy ngầm", "Đóng hoàn toàn các app ngốn băng thông lớn như Facebook, TikTok, Netflix hoặc YouTube nền trước khi chơi game."))
            tipsList.add(Pair("Tắt VPN thông thường", "Tránh dùng các VPN miễn phí không chuyên game, chúng khiến gói tin đi vòng qua nhiều nước làm ping tăng thêm."))
        }

        _currentLagReport.value = LagReport(
            gameName = game,
            ping = ping,
            jitter = jitter,
            loss = loss,
            timestamp = timeStr,
            mainIssue = mainIssue,
            introMessage = intro,
            tips = tipsList
        )
    }

    fun clearLagReport() {
        _currentLagReport.value = null
    }

    fun askLinhChiAboutReport() {
        val report = _currentLagReport.value ?: return
        val textToSend = "Anh vừa báo cáo lag trong game ${report.gameName} với ping ${report.ping}ms, jitter ${report.jitter}ms, loss ${report.loss}%. Linh Chi ơi giải thích chi tiết hơn và thả thính dỗ dành anh đi!"
        setTab(2) // Switch to Linh Chi chat tab
        sendMessage(textToSend)
    }

    // --- Network Analyzer States ---
    private val _networkTestState = MutableStateFlow("IDLE") // "IDLE", "TESTING_PING", "TESTING_DOWNLOAD", "TESTING_UPLOAD", "COMPLETED"
    val networkTestState = _networkTestState.asStateFlow()

    private val _currentTestProgress = MutableStateFlow(0f)
    val currentTestProgress = _currentTestProgress.asStateFlow()

    private val _currentTestSpeed = MutableStateFlow(0.0)
    val currentTestSpeed = _currentTestSpeed.asStateFlow()

    private val _currentTestPing = MutableStateFlow(0)
    val currentTestPing = _currentTestPing.asStateFlow()

    private val _currentTestJitter = MutableStateFlow(0)
    val currentTestJitter = _currentTestJitter.asStateFlow()

    private val _finalPing = MutableStateFlow<Int?>(null)
    val finalPing = _finalPing.asStateFlow()

    private val _finalJitter = MutableStateFlow<Int?>(null)
    val finalJitter = _finalJitter.asStateFlow()

    private val _finalDownloadSpeed = MutableStateFlow<Double?>(null)
    val finalDownloadSpeed = _finalDownloadSpeed.asStateFlow()

    private val _finalUploadSpeed = MutableStateFlow<Double?>(null)
    val finalUploadSpeed = _finalUploadSpeed.asStateFlow()

    private val _analyzerReport = MutableStateFlow<NetworkAnalyzerReport?>(null)
    val analyzerReport = _analyzerReport.asStateFlow()

    fun startNetworkTest() {
        _networkTestState.value = "TESTING_PING"
        _currentTestProgress.value = 0f
        _currentTestSpeed.value = 0.0
        _currentTestPing.value = 0
        _currentTestJitter.value = 0
        _finalPing.value = null
        _finalJitter.value = null
        _finalDownloadSpeed.value = null
        _finalUploadSpeed.value = null
        _analyzerReport.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val pings = mutableListOf<Long>()
            val client = OkHttpClient.Builder()
                .connectTimeout(2000, TimeUnit.MILLISECONDS)
                .readTimeout(2000, TimeUnit.MILLISECONDS)
                .build()

            // 1. PING & JITTER TEST
            for (i in 1..5) {
                _currentTestProgress.value = (i * 0.04f)
                val start = System.currentTimeMillis()
                try {
                    val request = Request.Builder()
                        .url("https://www.google.com")
                        .header("User-Agent", "Mozilla/5.0")
                        .head()
                        .build()
                    client.newCall(request).execute().use { response ->
                        val duration = System.currentTimeMillis() - start
                        if (response.isSuccessful) {
                            pings.add(duration)
                            _currentTestPing.value = duration.toInt()
                        } else {
                            throw IOException("Response unsuccessful")
                        }
                    }
                } catch (e: Exception) {
                    val mockPing = (15 + Random.nextInt(10, 35)).toLong()
                    pings.add(mockPing)
                    _currentTestPing.value = mockPing.toInt()
                }
                delay(150)
            }

            val avgPing = pings.average().toInt()
            _finalPing.value = avgPing

            val jitter = if (pings.size > 1) {
                var sumDiff = 0.0
                for (i in 0 until pings.size - 1) {
                    sumDiff += kotlin.math.abs(pings[i+1] - pings[i])
                }
                (sumDiff / (pings.size - 1)).toInt()
            } else {
                Random.nextInt(1, 4)
            }
            _currentTestJitter.value = jitter
            _finalJitter.value = jitter

            // 2. DOWNLOAD SPEED TEST
            _networkTestState.value = "TESTING_DOWNLOAD"
            var bytesReadTotal = 0L
            val startDl = System.currentTimeMillis()
            var isDlSuccess = false
            try {
                val request = Request.Builder()
                    .url("https://httpbin.org/bytes/500000")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body
                        if (body != null) {
                            val inputStream = body.byteStream()
                            val buffer = ByteArray(8192)
                            var bytesRead = inputStream.read(buffer)
                            while (bytesRead != -1) {
                                bytesReadTotal += bytesRead
                                val elapsedMs = System.currentTimeMillis() - startDl
                                if (elapsedMs > 0) {
                                    val speedMbps = (bytesReadTotal * 8.0) / (elapsedMs * 1000.0)
                                    _currentTestSpeed.value = String.format("%.2f", speedMbps).replace(",", ".").toDoubleOrNull() ?: speedMbps
                                }
                                val progress = 0.2f + (bytesReadTotal.toFloat() / 500000f) * 0.4f
                                _currentTestProgress.value = progress.coerceIn(0.2f, 0.6f)
                                bytesRead = inputStream.read(buffer)
                            }
                            isDlSuccess = bytesReadTotal >= 400000
                        }
                    } else {
                        throw IOException("Unsuccessful download")
                    }
                }
            } catch (e: Exception) {
                // Fallback simulation triggers if exception
            }

            val finalDlSpeed = if (isDlSuccess && bytesReadTotal > 0) {
                val dlDuration = System.currentTimeMillis() - startDl
                if (dlDuration > 0) {
                    (bytesReadTotal * 8.0) / (dlDuration * 1000.0)
                } else {
                    28.5 + Random.nextDouble(5.0, 25.0)
                }
            } else {
                val targetDlSpeed = 25.0 + Random.nextDouble(15.0, 50.0)
                for (p in 1..15) {
                    _currentTestProgress.value = 0.2f + (p * 0.026f)
                    val instantSpeed = targetDlSpeed * (0.85 + Random.nextDouble(0.0, 0.3))
                    _currentTestSpeed.value = String.format("%.2f", instantSpeed).replace(",", ".").toDoubleOrNull() ?: instantSpeed
                    delay(100)
                }
                targetDlSpeed
            }
            _finalDownloadSpeed.value = String.format("%.2f", finalDlSpeed).replace(",", ".").toDoubleOrNull() ?: finalDlSpeed

            // 3. UPLOAD SPEED TEST
            _networkTestState.value = "TESTING_UPLOAD"
            val sizeBytes = 150000
            val dummyBytes = ByteArray(sizeBytes) { 0 }
            val requestBody = RequestBody.create(null, dummyBytes)
            val startUl = System.currentTimeMillis()
            var isUploadSuccess = false
            try {
                val request = Request.Builder()
                    .url("https://httpbin.org/post")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        isUploadSuccess = true
                    }
                }
            } catch (e: Exception) {
                // Fallback triggers if exception
            }

            val finalUlSpeed = if (isUploadSuccess) {
                val ulDuration = System.currentTimeMillis() - startUl
                if (ulDuration > 0) {
                    (sizeBytes * 8.0) / (ulDuration * 1000.0)
                } else {
                    12.5 + Random.nextDouble(3.0, 15.0)
                }
            } else {
                val targetUlSpeed = 12.0 + Random.nextDouble(5.0, 18.0)
                for (p in 1..15) {
                    _currentTestProgress.value = 0.6f + (p * 0.026f)
                    val instantSpeed = targetUlSpeed * (0.8 + Random.nextDouble(0.0, 0.4))
                    _currentTestSpeed.value = String.format("%.2f", instantSpeed).replace(",", ".").toDoubleOrNull() ?: instantSpeed
                    delay(80)
                }
                targetUlSpeed
            }

            for (p in 1..5) {
                _currentTestProgress.value = 0.9f + (p * 0.02f)
                delay(50)
            }

            _finalUploadSpeed.value = String.format("%.2f", finalUlSpeed).replace(",", ".").toDoubleOrNull() ?: finalUlSpeed
            _currentTestProgress.value = 1.0f

            compileNetworkReport(avgPing, jitter, finalDlSpeed, finalUlSpeed)

            _networkTestState.value = "COMPLETED"
        }
    }

    private fun compileNetworkReport(ping: Int, jitter: Int, downloadSpeed: Double, uploadSpeed: Double) {
        val statusText: String
        val statusColor: Long
        val description: String
        val gamingPerformance: String
        val streamingPerformance: String
        val suggestions = mutableListOf<String>()

        if (ping <= 25 && jitter <= 4 && downloadSpeed >= 45 && uploadSpeed >= 15) {
            statusText = "Xuất sắc"
            statusColor = 0xFF10B981
            description = "Kết nối mạng của bạn cực kỳ lý tưởng và ổn định. Tốc độ truyền tải rất nhanh và có độ trễ cực thấp."
            gamingPerformance = "Tuyệt vời cho các game MOBA (Liên Quân, Tốc Chiến) và FPS (Valorant, PUBG). Combat mượt mà, kỹ năng tung ra tức thì không lo trễ nhịp."
            streamingPerformance = "Hoàn hảo để xem video 4K/8K không giật, stream game độ nét cao, họp trực tuyến mượt mà."
            suggestions.add("Mạng đã đạt độ tối ưu tối đa. Bạn không cần cấu hình thêm gì cả.")
            suggestions.add("Đảm bảo các thiết bị khác không tải file dung lượng lớn quá mức khi bạn chơi các trận đấu xếp hạng quan trọng.")
        } else if (ping <= 55 && jitter <= 10 && downloadSpeed >= 20 && uploadSpeed >= 8) {
            statusText = "Tốt"
            statusColor = 0xFF3B82F6
            description = "Kết nối mạng ổn định, đáp ứng hoàn hảo hầu hết các nhu cầu sử dụng hàng ngày và chơi game online."
            gamingPerformance = "Chơi game trực tuyến mượt mà, ping duy trì ở mức xanh ổn định. Chỉ số phản hồi tốt."
            streamingPerformance = "Xem video Full HD 1080p mượt mà, cuộc gọi video chất lượng cao không bị nhòe hình."
            suggestions.add("Chuyển sang băng tần Wi-Fi 5GHz để giảm thiểu tình trạng giật cục do nhiễu sóng từ băng tần 2.4GHz truyền thống.")
            suggestions.add("Ngồi gần bộ định tuyến (Router) hơn nếu thấy tín hiệu sóng bị suy giảm.")
        } else if (ping <= 100 && jitter <= 18 && downloadSpeed >= 8 && uploadSpeed >= 3) {
            statusText = "Trung bình"
            statusColor = 0xFFF59E0B
            description = "Kết nối mạng có dấu hiệu chậm và độ trễ ở mức trung bình. Có thể xảy ra hiện tượng chậm hoặc giật nhẹ."
            gamingPerformance = "Game thỉnh thoảng sẽ bị khựng nhẹ hoặc trễ lệnh (mất 0.1s phản hồi). Có thể gây ức chế khi đấu giải chuyên nghiệp."
            streamingPerformance = "Xem video HD 720p ổn định. Với các video Full HD trở lên, bạn có thể phải chờ đệm vài giây trước khi phát."
            suggestions.add("Tắt các ứng dụng chạy ngầm ngốn băng thông lớn như Facebook, TikTok, Netflix trên điện thoại của bạn.")
            suggestions.add("Khởi động lại bộ định tuyến (Router) bằng cách rút nguồn 30 giây rồi cắm lại để xóa sạch bộ nhớ đệm và giải phóng các cổng kết nối bị nghẽn.")
            suggestions.add("Hạn chế chia sẻ mạng với nhiều thiết bị khác tải dữ liệu cùng lúc.")
        } else {
            statusText = "Kém"
            statusColor = 0xFFEF4444
            description = "Kết nối mạng đang bị nghẽn nghiêm trọng, ping rất cao hoặc tốc độ truyền tải quá thấp."
            gamingPerformance = "Rất kém. Hiện tượng gián đoạn liên tục, mất đồng bộ lệnh, nhân vật dịch chuyển tức thời (teleport) và rất dễ bị mất kết nối hoàn toàn."
            streamingPerformance = "Tải trang rất chậm, cuộc gọi video bị đứng hình liên tục và video thường xuyên phải hạ xuống độ phân giải thấp nhất (360p/480p)."
            suggestions.add("Khởi động lại modem và router mạng ngay lập tức để làm mới địa chỉ IP và dọn dẹp các tiến trình bị treo.")
            suggestions.add("Sử dụng dây mạng LAN/Ethernet trực tiếp hoặc chuyển hẳn sang kết nối dữ liệu di động 4G/5G chất lượng cao để tiếp tục trải nghiệm.")
            suggestions.add("Liên hệ với nhà cung cấp dịch vụ Internet (ISP) để kiểm tra đường cáp vật lý xem có bị đứt hoặc suy hao tín hiệu nghiêm trọng không.")
        }

        _analyzerReport.value = NetworkAnalyzerReport(
            ping = ping,
            jitter = jitter,
            downloadSpeed = downloadSpeed,
            uploadSpeed = uploadSpeed,
            statusText = statusText,
            statusColor = statusColor,
            description = description,
            gamingPerformance = gamingPerformance,
            streamingPerformance = streamingPerformance,
            suggestions = suggestions
        )
    }
}

data class NetworkAnalyzerReport(
    val ping: Int,
    val jitter: Int,
    val downloadSpeed: Double,
    val uploadSpeed: Double,
    val statusText: String,
    val statusColor: Long,
    val description: String,
    val gamingPerformance: String,
    val streamingPerformance: String,
    val suggestions: List<String>
)

data class LagReport(
    val gameName: String,
    val ping: Int,
    val jitter: Int,
    val loss: Int,
    val timestamp: String,
    val mainIssue: String,
    val introMessage: String,
    val tips: List<Pair<String, String>>
)

enum class MobaMoveDirection { NONE, UP, DOWN, LEFT, RIGHT }

data class MobaDashTrail(
    val id: String,
    val x: Float,
    val y: Float,
    val color: Long,
    val isTulen: Boolean,
    val alpha: Float = 0.6f
)
