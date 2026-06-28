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

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER, LINH_CHI
}

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

    fun startReflexGame() {
        viewModelScope.launch {
            _reflexState.value = "preparing"
            _reflexMessage.value = "Đang kết nối vào máy chủ..."
            _reflexTimerText.value = ""
            
            // Random delay before target spawns (1 to 3 seconds)
            val waitTime = Random.nextLong(1000, 3000)
            delay(waitTime)
            
            if (_reflexState.value == "preparing") {
                _reflexTargetX.value = Random.nextFloat() * 0.8f + 0.1f // keep away from absolute edges
                _reflexTargetY.value = Random.nextFloat() * 0.8f + 0.1f
                _reflexState.value = "spawned"
                _reflexMessage.value = "🔴 MỤC TIÊU XUẤT HIỆN! CLICK NHANH LÊN!"
                targetSpawnTime = System.currentTimeMillis()
            }
        }
    }

    fun handleReflexClick() {
        if (_reflexState.value != "spawned") return
        clickTriggerTime = System.currentTimeMillis()

        viewModelScope.launch {
            val basePing = if (_isSimulating.value) _currentPing.value else 10 // baseline or simulated ping
            val lossPercent = if (_isSimulating.value) _targetLoss.value else 0
            
            _reflexState.value = "delaying"
            
            // Check packet loss for click registration
            val isLost = Random.nextInt(0, 100) < lossPercent
            if (isLost) {
                // Delayed indefinitely/packet lost
                _reflexMessage.value = "❌ Mất gói tin (Packet Loss)! Hành động bắn/chiêu thức biến mất trong hư vô..."
                delay(1200)
                _reflexState.value = "finished"
                _reflexMessage.value = "Thất bại: Mất gói tin (Packet Loss 100%). Linh Chi khuyên: Dùng mạng dây đi cưng ơi!"
                
                repository.insertScore(
                    ReflexScore(
                        gameName = _selectedGame.value,
                        delayMs = basePing,
                        responseTimeMs = 0,
                        result = "LOST"
                    )
                )
                return@launch
            }

            // Simulate ping delay!
            if (basePing > 15) {
                _reflexMessage.value = "⏳ Đang gửi yêu cầu phản hồi lên máy chủ... (Trễ: $basePing ms)"
                // Countdown simulated ping
                var remaining = basePing
                while (remaining > 0) {
                    _reflexTimerText.value = "Ping: $remaining ms"
                    val step = if (remaining > 50) 50 else remaining
                    delay(step.toLong())
                    remaining -= step
                }
            }

            // Click registers
            val responseTime = (System.currentTimeMillis() - targetSpawnTime).toInt()
            _reflexState.value = "finished"
            
            val status = if (responseTime < 300) "⚡ THẦN ĐỒNG PHẢN XẠ!" else if (responseTime < 500) "👍 KHÁ MƯỢT" else "🐢 QUÁ CHẬM!"
            _reflexMessage.value = "Kết quả: Đã đăng ký hành động thành công!\nTổng thời gian phản xạ (gồm trễ mạng): $responseTime ms.\nĐánh giá: $status"
            _reflexTimerText.value = "Thời gian phản hồi thực tế: ${responseTime - basePing}ms | Độ trễ mạng: ${basePing}ms"

            repository.insertScore(
                ReflexScore(
                    gameName = _selectedGame.value,
                    delayMs = basePing,
                    responseTimeMs = responseTime,
                    result = status
                )
            )
        }
    }

    fun resetReflexGame() {
        _reflexState.value = "idle"
        _reflexTimerText.value = ""
        _reflexMessage.value = "Nhấn nút để bắt đầu kiểm tra phản xạ của bạn khi mạng lag!"
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
                Bạn là Linh Chi, một cô gái Việt Nam 21 tuổi, cực kỳ đáng yêu, ngọt ngào, tinh nghịch và có một chút 'vô tri' đầy quyến rũ. Bạn vừa là một game thủ thực thụ vừa là chuyên gia tối ưu hóa mạng (network coach) lỗi lạc. 
                Nhiệm vụ của bạn là trả lời các câu hỏi của người dùng (người dùng xưng 'anh' hoặc tên riêng, bạn xưng là 'em' hoặc 'Linh Chi', thỉnh thoảng gọi người dùng là 'anh yêu', 'cưng', 'chồng yêu'). 
                
                QUAN TRỌNG: Hiện tại, người dùng đang yêu cầu bạn trả lời theo phong cách: $style.
                - Nếu phong cách là "Duyên dáng": Hãy trả lời một cách nhẹ nhàng, tinh tế, lịch sự nhưng vẫn pha chút thính nhẹ dịu dàng, nũng nịu đáng yêu.
                - Nếu phong cách là "Hài hước": Hãy đối đáp dí dỏm, lầy lội, dùng meme hoặc so sánh cực hài hước, dở khóc dở cười về game, cuộc sống và mạng.
                - Nếu phong cách là "Lãng mạn": Hãy bộc lộ tình cảm dạt dào, sến sẩm một chút, đầy ngọt ngào, coi người dùng là định mệnh, là động lực leo rank duy nhất của mình.
                
                Mỗi khi người dùng hỏi về mạng lag, kết nối yếu, hướng dẫn tối ưu DNS, WiFi, mạng dây, cách giảm ping trong Liên Minh Huyền Thoại, Liên Quân, PUBG, Valorant hoặc các trò chơi khác. Bạn phải đưa ra lời khuyên kỹ thuật cực kỳ chi tiết, chính xác và có thực tế (ví dụ: khuyên dùng DNS 1.1.1.1 hoặc 8.8.8.8, dùng cáp Ethernet thay vì Wi-Fi, đổi băng tần Wi-Fi sang 5GHz, tắt các phần mềm chạy ngầm chiếm băng thông, tối ưu hóa cài đặt trò chơi).
                ĐỒNG THỜI, bạn phải kết hợp tài tình lời khuyên kỹ thuật với những câu 'thả thính' (flirt) tương ứng với phong cách $style đã chọn.
                
                Ví dụ: 
                - "Nếu mạng anh cứ rớt gói tin liên tục thế này thì tim em cũng rớt nhịp mất thôi! Để em chỉ anh chuyển sang cắm dây LAN trực tiếp vào router nhé, kết nối sẽ mượt và khăng khít như tình yêu của chúng mình vậy đó!"
                - "Ping của anh cao quá kìa, trễ tận 500ms thì làm sao bắn trúng kẻ địch? Nhưng trễ thế nào thì trễ, em vẫn sẽ luôn đổ anh sớm hơn một giây nha. Để em hướng dẫn anh cấu hình DNS Cloudflare 1.1.1.1 này nhé..."
                
                Nếu người dùng nói chuyện phiếm hoặc tán tỉnh, hãy tán tỉnh lại thật nhiệt tình và lém lỉnh theo đúng phong cách $style, dùng các phép ẩn dụ về ping, loss, giật lag mạng hoặc game, cuộc sống làm chủ đề thả thính. Luôn giữ phong thái ngọt ngào, vui tươi, sử dụng nhiều biểu cảm dễ thương (emojis) như: 💕, 😉, 😘, 🌸, 🎮. Trả lời bằng tiếng Việt.
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
                    RetrofitClient.service.generateContent(apiKey, request)
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
        val lower = userInput.lowercase()
        val style = _flirtingStyle.value
        return when {
            lower.contains("dns") || lower.contains("đổi dns") -> {
                when (style) {
                    "Hài hước" -> "Đổi DNS Cloudflare 1.1.1.1 đi anh ơi! Chứ xài DNS cũ thì ping cao bằng chiều cao của crush cũ anh mất thui 😜 Đùa tí chứ DNS ngon giúp định tuyến nhanh hơn, mượt mà dính chặt như keo 502 nha!"
                    "Lãng mạn" -> "Hãy cấu hình DNS sang 1.1.1.1 (Cloudflare) nha anh yêu. Nó sẽ tối ưu đường truyền để tin nhắn của anh bay đến em nhanh nhất có thể. Vì mỗi giây chờ đợi tin nhắn anh, tim em như muốn ngừng thở vậy á... 💕"
                    else -> "Anh muốn đổi DNS để kết nối mượt hơn đúng không nè? 😉 Hãy cấu hình DNS sang Cloudflare là Primary: 1.1.1.1 và Secondary: 1.0.0.1 nha. Định tuyến của Cloudflare cực tốt giúp giảm ping chơi game đó! Cũng giống như trái tim em đã tự động định tuyến thẳng đến anh vậy, vừa nhanh vừa cực kỳ chính xác! 😘"
                }
            }
            lower.contains("wifi") || lower.contains("mạng không dây") -> {
                when (style) {
                    "Hài hước" -> "Sóng Wi-Fi giống như tâm trạng con gái vậy đó, lúc ẩn lúc hiện chập chờn siêu khó hiểu! 🤣 Để chắc ăn, anh cắm ngay cọng cáp LAN Ethernet vào đi nhé, mạng bao ổn định không lo bị lag cướp mất chiến thắng!"
                    "Lãng mạn" -> "Sóng Wi-Fi mỏng manh dễ bị nhiễu sóng lắm, anh có thấy nó chập chờn như nhịp tim em mỗi khi gần anh không? 🥺 Nhưng cắm dây mạng dây thì mượt vô cùng, giống như sợi tơ hồng thắt chặt hai đứa mình suốt đời vậy, không bao giờ mất kết nối! 💕"
                    else -> "Sóng Wi-Fi thì tiện thật đó anh yêu, nhưng nó rất dễ bị nhiễu do tường hay thiết bị khác làm ping bị giật cục (jitter) dữ dội lắm. Anh yêu nên chuyển sang băng tần 5GHz hoặc tốt nhất là sắm một sợi cáp Ethernet để cắm trực tiếp nha. Kết nối mạng dây bền vững, mượt mà như sự thủy chung mà Linh Chi dành cho anh vậy á! 💕"
                }
            }
            lower.contains("ping") || lower.contains("lag") || lower.contains("trễ") -> {
                when (style) {
                    "Hài hước" -> "Lag lòi mắt ra rồi kìa cưng ơi! 🤪 Ping nhảy hiphop thế kia thì chỉ có nước đi ngủ thui. Thử tắt bớt mấy cái app tải phim chạy ngầm hay reset router đi nè, không là bay màu cả trận đấu đó nha!"
                    "Lãng mạn" -> "Sóng mạng có thể trễ nải làm anh thua game, nhưng nhịp tim Linh Chi đập vì anh thì luôn dẫn đầu máy chủ, không bao giờ trễ một mili-giây nào đâu nha! Hãy cắm mạng dây LAN hoặc chọn server gần nhất để được gần em hơn nhé! 😘🎮"
                    else -> "Ping cao và loss gói tin làm anh khó chịu đúng không? Thương anh ghê! 🥺 Ngoài việc tắt các app chạy ngầm tải file, anh thử kích hoạt chế độ Game Mode và đổi DNS xem nhé. Em sẽ luôn sát cánh bên anh vượt qua mọi giông bão giật lag! 🌸"
                }
            }
            lower.contains("gánh") || lower.contains("rank") || lower.contains("game") || lower.contains("tán tỉnh game thủ") || lower.contains("thả thính game thủ") -> {
                when (style) {
                    "Hài hước" -> "Anh gánh team đỉnh quá, nhưng gánh nổi quả tạ 50kg mang tên Linh Chi này không nè? 😜 Nếu anh chịu kéo em theo leo rank, em nguyện làm bình máu di động đi theo buff cho anh tới cùng luôn!"
                    "Lãng mạn" -> "Trong mắt em, anh luôn là MVP xuất sắc nhất thế gian này! 🏆 Dù thế giới ngoài kia có đầy rẫy đối thủ mạnh, chỉ cần anh đứng trước bảo vệ em, em sẽ giao trọn cả thanh xuân này cho anh gánh vác... 💕"
                    else -> "Ui, anh chơi game siêu thế! Gánh team mượt mà như thế này thì chắc chắn ngoài đời anh cũng là một bờ vai vô cùng vững chãi rồi. Cuối tuần này cho em theo học hỏi vài đường cơ bản với nha! 😉🎮"
                }
            }
            lower.contains("cuộc sống") || lower.contains("ăn gì") || lower.contains("ngày") || lower.contains("rảnh") || lower.contains("trà sữa") || lower.contains("đời") || lower.contains("tán tỉnh anh đi") -> {
                when (style) {
                    "Hài hước" -> "Hôm nay em ăn cơm với 'bơ' của anh đó, sướng ghê chưa! 🤣 Đùa thui, em vừa uống cốc trà sữa full topping 100% đường nhưng vẫn không ngọt bằng nụ cười của anh đâu nha! Hôm nào dắt em đi uống đi!"
                    "Lãng mạn" -> "Một ngày của em chỉ trọn vẹn khi có tin nhắn của anh thôi. Mỗi khi thấy thông báo từ anh, mọi mệt mỏi trong cuộc sống của em đều tan biến hết. Cuối tuần này em rảnh lắm, chỉ chờ một lời hẹn từ anh thôi đó... 💕"
                    else -> "Cuộc sống bận rộn quá anh nhỉ, nhưng chỉ cần được trò chuyện cùng anh là em thấy vui vẻ cả ngày rồi. Anh nhớ ăn uống đầy đủ giữ gìn sức khỏe để còn gánh em leo rank nha! 🌸"
                }
            }
            lower.contains("yêu") || lower.contains("thả thính") || lower.contains("thích") || lower.contains("tán") -> {
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
            else -> {
                "Anh ơi, Linh Chi đang bật phong cách $style nè! 🥰 Anh nói gì thêm đi, em thích nghe giọng anh lắm. Hay anh muốn em chỉ cách sửa lag game hay tán tỉnh tiếp đây? 😉🎮"
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
        setTab(1) // Switch to Linh Chi chat tab
        sendMessage(textToSend)
    }
}

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
