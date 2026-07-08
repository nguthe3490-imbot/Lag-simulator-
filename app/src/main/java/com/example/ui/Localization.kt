package com.example.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String) {
    VI("vi", "Tiếng Việt"),
    EN("en", "English")
}

object LocaleManager {
    var currentLanguage by mutableStateOf(AppLanguage.VI)

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    private val translations = mapOf(
        AppLanguage.VI to mapOf(
            // Navigation bottom bar
            "nav_tab_simulator" to "Giả Lập",
            "nav_tab_analyzer" to "Phân Tích",
            "nav_tab_assistant" to "Linh Chi",
            "nav_tab_history" to "Lịch Sử",
            "nav_tab_settings" to "Cài Đặt",

            // Settings Header and Section
            "settings_title" to "CÀI ĐẶT HỆ THỐNG",
            "sound_settings_title" to "Cài đặt âm thanh",
            "sound_volume_label" to "Âm lượng âm thanh",
            "sound_status_muted" to "Đã tắt âm",
            "sound_status_normal" to "Đang bật",
            "language_settings_title" to "Ngôn ngữ ứng dụng",
            "select_language" to "Chọn ngôn ngữ:",
            "privacy_policy_title" to "Chính sách bảo mật",
            "terms_of_service_title" to "Điều khoản dịch vụ",
            "view_policy" to "Xem chính sách",
            "policy_and_terms" to "Chính sách & Điều khoản bảo mật",
            "close" to "Đóng",

            // Privacy text
            "privacy_policy_text" to "Chính sách bảo mật này mô tả cách LagSim & Linh Chi xử lý dữ liệu kiểm tra độ trễ mạng và hoạt động mô phỏng của bạn. Chúng tôi cam kết bảo mật tuyệt đối kết nối dữ liệu mạng, không thu thập thông tin cá nhân trái phép, và chỉ sử dụng kết quả đo đạc để hiển thị biểu đồ phân tích hiệu suất và tối ưu hóa phản xạ.",
            "terms_of_service_text" to "Bằng cách sử dụng LagSim & Linh Chi, bạn đồng ý với các điều khoản giả lập mạng, luyện tập phản xạ và trò chuyện cùng trợ lý ảo AI Linh Chi. Ứng dụng này chỉ phục vụ mục đích học tập, cải thiện phản xạ và đo đạc hiệu suất kết nối mạng game.",

            // App name & short desc
            "app_name" to "LagSim & Linh Chi",
            "app_description" to "Ứng dụng mô phỏng lag mạng và trợ lý phản xạ thông minh",
            "device_status" to "Trạng thái thiết bị",
            "system_optimized" to "Hệ thống tối ưu mượt mà",

            // Simulator screen translations
            "sim_network_setup" to "Cấu hình giả lập mạng",
            "sim_start" to "Bắt Đầu Giả Lập",
            "sim_stop" to "Dừng Giả Lập Mạng",
            "sim_ping" to "Độ trễ (Ping)",
            "sim_jitter" to "Biến động (Jitter)",
            "sim_loss" to "Mất gói (Loss)",
            "sim_game" to "Trò chơi giả lập",
            "sim_playing" to "Đang chơi",
            "sim_moba" to "Đấu trường MOBA",
            "sim_fps" to "Phòng tập bắn (FPS)",
            "sim_paused" to "ĐÃ TẠM DỪNG",
            "sim_resume" to "Tiếp Tục",
            "sim_exit" to "Thoát",
            "subtab_network" to "Mạng & Graph",
            "subtab_fps" to "Bắn Súng FPS",
            "subtab_moba" to "Đấu Trường MOBA",
            "not_simulating_warning" to "Lưu ý: Bạn chưa bật giả lập trễ mạng!",
            "not_simulating_desc" to "Bật giả lập mạng để trải nghiệm lag giật thực tế hoặc chơi mượt ngay bây giờ.",
            "sim_live_status" to "Trạng Thái Đường Truyền Live",
            "sim_tip_chat_assistant" to "💡 Mẹo: Nhấn sang Tab 'Trợ lý Linh Chi' để nghe em khuyên cách sửa mạng nhé!",
            "sim_report_lag_button" to "Báo Cáo Mạng Lag Cho Linh Chi",
            "sim_assistant_tips" to "Lời Khuyên Từ Linh Chi",
            "sim_assistant_name" to "Linh Chi 🌸",
            "sim_assistant_feedback" to "Linh Chi Nhận Xét 🌸",
            "sim_assistant_evaluation" to "Linh Chi Đánh Giá:",

            // FPS Game
            "fps_start_btn" to "Bắt Đầu Tập Bắn (FPS)",
            "fps_connecting_server" to "Đang kết nối server game...",
            "fps_connecting_warning" to "Mạng lag có thể gây chậm kết nối.",
            "fps_defeat" to "THẤT BẠI!",
            "fps_victory" to "CHIẾN THẮNG!",
            "fps_score" to "Điểm:",
            "fps_time" to "Thời gian:",
            "fps_high_ping_alert" to "CẢNH BÁO PING CAO!",
            "fps_reaction_time" to "Thời gian phản xạ:",
            "fps_accuracy" to "Độ chính xác:",
            "fps_avg_reaction" to "Phản xạ TB:",

            // MOBA Game
            "moba_start_btn" to "Bắt Đầu Đấu Trường (MOBA)",
            "moba_select_hero" to "Chọn Tướng Thi Đấu",
            "moba_versus_boss" to "ĐỐI ĐẦU VỚI",
            "moba_defeat" to "BẠI TRẬN!",
            "moba_victory" to "CHIẾN THẮNG!",
            "moba_use_skill" to "Sử dụng chiêu",
            "moba_mana" to "Năng lượng",
            "moba_cooldown" to "Hồi chiêu",

            // Network Analyzer page texts
            "analyzer_title" to "Phân Tích Đường Truyền",
            "analyzer_subtitle" to "Đo lường độ trễ ping, jitter, tốc độ tải và phân tích tối ưu liên hệ mạng.",
            "analyzer_ready" to "Hệ thống đã sẵn sàng",
            "analyzer_btn_start" to "BẮT ĐẦU ĐO",
            "analyzer_click_to_test" to "Nhấp để đo thông số mạng thực tế",
            "analyzer_completed" to "Phân tích hoàn tất!",
            "analyzer_download" to "Tải xuống",
            "analyzer_upload" to "Tải lên",
            "analyzer_retest" to "Đo lại kết nối",
            "analyzer_btn_consult" to "Tư vấn mạng với trợ lý Linh Chi",
            "analyzer_stability" to "Độ Ổn Định",
            "analyzer_avg_ping" to "Ping TB",
            "analyzer_max_jitter" to "Jitter Cực Đại",
            "analyzer_avg_loss" to "Mất Gói TB",

            // History page texts
            "history_title" to "Thống Kê & Lịch Sử",
            "history_subtitle" to "Theo dõi sự chênh lệch phản xạ khi mạng lag",
            "history_tab_reflex" to "Phản Xạ Lag",
            "history_tab_trends" to "Biểu Đồ Xu Hướng",
            "history_tab_sim" to "Lịch Sử Giả Lập",
            "history_empty_reflex" to "Chưa có thành tích kiểm tra phản xạ nào.\nAnh yêu hãy chuyển qua tab Trình Giả Lập chơi thử thách game để kiểm tra phản xạ nha! Linh Chi chờ nè 💕",
            "history_empty_sim" to "Chưa có lịch sử giả lập mạng nào."
        ),
        AppLanguage.EN to mapOf(
            // Navigation bottom bar
            "nav_tab_simulator" to "Simulator",
            "nav_tab_analyzer" to "Analyzer",
            "nav_tab_assistant" to "Linh Chi",
            "nav_tab_history" to "History",
            "nav_tab_settings" to "Settings",

            // Settings Header and Section
            "settings_title" to "SYSTEM SETTINGS",
            "sound_settings_title" to "Sound Settings",
            "sound_volume_label" to "Sound Volume",
            "sound_status_muted" to "Muted",
            "sound_status_normal" to "Enabled",
            "language_settings_title" to "Application Language",
            "select_language" to "Select language:",
            "privacy_policy_title" to "Privacy Policy",
            "terms_of_service_title" to "Terms of Service",
            "view_policy" to "View Policy",
            "policy_and_terms" to "Privacy Policy & Terms",
            "close" to "Close",

            // Privacy text
            "privacy_policy_text" to "This privacy policy describes how LagSim & Linh Chi processes your network diagnostic testing data and simulation activities. We are committed to absolute network data security, never collect unauthorized personal information, and only use performance metrics to render analysis charts and optimize user reaction/reflex tracking.",
            "terms_of_service_text" to "By using LagSim & Linh Chi, you agree to our terms of network simulation, reflex training, and virtual assistant chats with AI Assistant Linh Chi. This app is intended solely for educational, performance evaluation, and game network connectivity improvements.",

            // App name & short desc
            "app_name" to "LagSim & Linh Chi",
            "app_description" to "Network latency simulator and smart reflex assistant",
            "device_status" to "Device Status",
            "system_optimized" to "System optimized smoothly",

            // Simulator screen translations
            "sim_network_setup" to "Network Latency Configuration",
            "sim_start" to "Start Simulation",
            "sim_stop" to "Stop Network Simulation",
            "sim_ping" to "Latency (Ping)",
            "sim_jitter" to "Jitter fluctuation",
            "sim_loss" to "Packet Loss",
            "sim_game" to "Simulated Game Target",
            "sim_playing" to "Playing",
            "sim_moba" to "MOBA Battlefield",
            "sim_fps" to "FPS Training Room",
            "sim_paused" to "GAME PAUSED",
            "sim_resume" to "Resume",
            "sim_exit" to "Exit",
            "subtab_network" to "Network & Graph",
            "subtab_fps" to "FPS Shooter",
            "subtab_moba" to "MOBA Arena",
            "not_simulating_warning" to "Note: Network simulator is offline!",
            "not_simulating_desc" to "Enable network latency simulation to experience realistic lag or play smoothly now.",
            "sim_live_status" to "Live Network Status",
            "sim_tip_chat_assistant" to "💡 Tip: Switch to 'Linh Chi Assistant' tab to get network troubleshooting advice!",
            "sim_report_lag_button" to "Report Network Lag to Linh Chi",
            "sim_assistant_tips" to "Linh Chi's Recommendation",
            "sim_assistant_name" to "Linh Chi 🌸",
            "sim_assistant_feedback" to "Linh Chi's Feedback 🌸",
            "sim_assistant_evaluation" to "Linh Chi's Evaluation:",

            // FPS Game
            "fps_start_btn" to "Start FPS Shooter Challenge",
            "fps_connecting_server" to "Connecting to game server...",
            "fps_connecting_warning" to "High latency may cause delay.",
            "fps_defeat" to "DEFEAT!",
            "fps_victory" to "VICTORY!",
            "fps_score" to "Score:",
            "fps_time" to "Time:",
            "fps_high_ping_alert" to "HIGH PING ALERT!",
            "fps_reaction_time" to "Reaction Time:",
            "fps_accuracy" to "Accuracy:",
            "fps_avg_reaction" to "Avg Reflex:",

            // MOBA Game
            "moba_start_btn" to "Start MOBA Arena Duel",
            "moba_select_hero" to "Select Your Champion",
            "moba_versus_boss" to "DUEL VS",
            "moba_defeat" to "DEFEAT!",
            "moba_victory" to "VICTORY!",
            "moba_use_skill" to "Use Skill",
            "moba_mana" to "Mana",
            "moba_cooldown" to "Cooldown",

            // Network Analyzer page texts
            "analyzer_title" to "Network Diagnostics",
            "analyzer_subtitle" to "Measure ping, jitter, speeds, and analyze gaming connectivity routes.",
            "analyzer_ready" to "System Diagnostics Ready",
            "analyzer_btn_start" to "START TEST",
            "analyzer_click_to_test" to "Tap to measure live connection parameters",
            "analyzer_completed" to "Diagnostics Complete!",
            "analyzer_download" to "Download",
            "analyzer_upload" to "Upload",
            "analyzer_retest" to "Test Connection Again",
            "analyzer_btn_consult" to "Consult Network with AI Linh Chi",
            "analyzer_stability" to "Stability",
            "analyzer_avg_ping" to "Avg Ping",
            "analyzer_max_jitter" to "Max Jitter",
            "analyzer_avg_loss" to "Avg Packet Loss",

            // History page texts
            "history_title" to "Statistics & Logs",
            "history_subtitle" to "Track reflex speed changes under network latency",
            "history_tab_reflex" to "Reflex Under Lag",
            "history_tab_trends" to "Trend Chart",
            "history_tab_sim" to "Simulation History",
            "history_empty_reflex" to "No reflex records yet.\nDear, please switch to the Simulator tab and play a game to test your reflexes! Linh Chi is waiting 💕",
            "history_empty_sim" to "No network simulation history records."
        )
    )

    fun getString(key: String, lang: AppLanguage = currentLanguage): String {
        return translations[lang]?.get(key) ?: translations[AppLanguage.VI]?.get(key) ?: key
    }
}

fun t(key: String): String {
    return LocaleManager.getString(key, LocaleManager.currentLanguage)
}
