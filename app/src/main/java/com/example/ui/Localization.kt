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
            "subtab_sphere" to "Quả Cầu 🔮",
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
            "history_empty_sim" to "Chưa có lịch sử giả lập mạng nào.",

            "sim_network_setup_title" to "Cấu hình giả lập mạng",
            "sim_game_target" to "Trò chơi mục tiêu",
            "fps_2d_title" to "Trình Kiểm Tra Phản Xạ FPS 2D",
            "fps_2d_desc" to "Thử thách bắn súng 2D FPS vào bia tập bắn giúp kiểm nghiệm chính xác mức độ ảnh hưởng của lag mạng (Ping cao, biến động Jitter, hoặc mất gói đạn hoàn toàn).",
            "fps_new_badge" to "MỚI",
            "moba_2d_title" to "Liên Quân 2D Arena",
            "moba_2d_desc" to "Luyện né chiêu, hit-and-run chuẩn xác dưới các mức ping cực đỏ!",
            "sphere_sim_title" to "MÔ PHỎNG QUẢ CẦU NHÂN ĐÔI",
            "sphere_sim_desc" to "Hãy chạm vào quả cầu để kích hoạt nhân đôi!",
            "sphere_total_created" to "Tổng số quả cầu tạo ra: %d",
            "sphere_current_count" to "Số lượng: %d",
            "history_game_filter_label" to "Bộ lọc Trò chơi:",
            "all_games_option" to "Tất cả",

            "gamer_rank_card_title" to "BẢNG PHONG THẦN PHẢN XẠ 👑",
            "gamer_rank_card_subtitle" to "Kiện Tướng Liên Quân & Valorant",
            "gamer_rank_card_rank_label" to "HẠNG KIỆN TƯỚNG:",
            "gamer_rank_card_best" to "Phản xạ nhanh nhất",
            "gamer_rank_card_total" to "Tổng mạng diệt / bia",
            "gamer_rank_card_total_pattern" to "%d mạng",
            "gamer_rank_card_accuracy" to "Tỷ lệ chuẩn xác",
            "gamer_rank_card_badges_title" to "DANH HIỆU ĐẠT ĐƯỢC 🏆",

            "rank_surveying_title" to "TẬP SỰ ĐANG KHẢO SÁT 🔍",
            "rank_surveying_quote" to "Hãy hoàn thành thêm vài lượt huấn luyện phản xạ để em xếp hạng chính xác cho anh yêu nhé! 💕",
            "rank_challenger_title" to "THÁCH ĐẤU SIÊU TỐC ⚡",
            "rank_challenger_quote" to "Tốc độ thần sầu, phản xạ đỉnh cao sánh ngang các siêu sao tuyển thủ chuyên nghiệp thế giới! 👑",
            "rank_master_title" to "CAO THỦ TINH ANH 🔮",
            "rank_master_quote" to "Tay nhanh hơn não! Khả năng phản xạ cực nhạy, né skill lả lướt như thần gió Yasuo! 🌪️",
            "rank_diamond_title" to "KIM CƯƠNG CHIẾN THUẬT 🛡️",
            "rank_diamond_quote" to "Phản xạ nhạy bén và điêu luyện. Bạn chính là chỗ dựa gánh team vững chắc trong mọi pha combat! ⚔️",
            "rank_platinum_title" to "BẠCH KIM CỨNG CÁP 🎖️",
            "rank_platinum_quote" to "Nhịp tay rất đều và chắc chắn. Tối ưu thêm chút ping mạng nữa là leo thẳng lên Thách Đấu ngay thôi! 🚀",
            "rank_gold_title" to "VÀNG ĐỒNG KIÊN CƯỜNG 🪵",
            "rank_gold_quote" to "Dù ping giật lag đỏ lòm hay mất gói ngập đầu, ý chí kiên định chiến đấu của anh vẫn là tuyệt nhất! 💪",

            "badge_light_title" to "⚡ ÁNH SÁNG",
            "badge_light_desc" to "Phản xạ cực đỉnh < 200ms",
            "badge_assassin_title" to "⚔️ SÁT THỦ",
            "badge_assassin_desc" to "Hạ gục trên 30 mục tiêu",
            "badge_sharpshooter_title" to "🎯 THẦN TIỄN",
            "badge_sharpshooter_desc" to "Chuẩn xác xuất sắc ≥ 85%",
            "badge_unyielding_title" to "🛡️ BẤT KHUẤT",
            "badge_unyielding_desc" to "Rèn luyện bền bỉ ≥ 8 lượt",
            "badge_slayer_title" to "👑 DIỆT MA",
            "badge_slayer_desc" to "Chiến thắng Ma Vương Maloch",

            "medal_victory_perfect" to "🏆 CHIẾN THẮNG TRỌN VẸN",
            "medal_victory_perfect_desc" to "Đập sập sào huyệt đối phương!",
            "medal_warrior_clash" to "🔥 CHIẾN THẦN GANH ĐUA",
            "medal_warrior_clash_desc" to "Gây áp lực khổng lồ lên Maloch!",
            "medal_conqueror" to "🛡️ KINH VÔ ĐỊCH",
            "medal_conqueror_desc" to "Phá huỷ phòng tuyến kiên cố!",
            "medal_perfect_mage" to "✨ PHÁP SƯ HOÀN MỸ",
            "medal_perfect_mage_desc" to "Chuyển chiêu mượt mà như nước chảy!",
            "medal_defeat_regret" to "💀 THẤT BẠI TIẾC NUỐI",
            "medal_defeat_regret_desc" to "Mạng lag ngăn cản đôi bàn tay vàng!",
            "medal_red_ping_victim" to "📶 NẠN NHÂN MẠNG ĐỎ",
            "medal_red_ping_victim_desc" to "Bị đứt kết nối trong lúc combo!",
            "medal_persistence" to "🏋️ NỖ LỰC VƯỢT KHÓ",
            "medal_persistence_desc" to "Kiên định chiến đấu dù đường truyền nghẽn!",

            "fps_select_mode_label" to "Chọn Màn Chơi (Game Mode):",
            "fps_select_weapon_label" to "Chọn Vũ Khí (Weapon SFX):",
            "fps_mode_classic" to "Cổ Điển 🎯",
            "fps_mode_bottle" to "Bắn Chai 🍾",
            "fps_mode_fast" to "Siêu Tốc ⚡",
            "fps_mode_sniper" to "Bắn Tỉa 🔭",
            "fps_mode_boss" to "Đấu Boss Alien 👾",
            "fps_mode_zombie" to "Săn Zombie 🧟",
            "fps_mode_continuous" to "Liên Thanh 🔥",
            "fps_weapon_pistol" to "Súng Lục 🔫",
            "fps_weapon_ak47" to "Súng AK47 ⚔️",
            "fps_weapon_shotgun" to "Shotgun 💥",
            "fps_weapon_sniper" to "Sniper AWM 🔭",
            
            "fps_zoom_title" to "Phòng Bắn Đang Mở Ở Chế Độ Phóng To 🎯",
            "fps_zoom_desc" to "Màn hình tập bắn đã được phóng to tối đa và cố định ở giữa để tăng độ chính xác khi ngắm bắn.",
            "fps_zoom_reopen_btn" to "Hiện Lại Màn Hình Phóng To",
            "fps_difficulty_adaptive" to "Tốc Độ Bia Thích Ứng (Lag):",
            "fps_start_btn" to "Bắt Đầu Tập Bắn (FPS)",
            "fps_connecting" to "Đang kết nối server game...",
            "fps_boss_tag" to "👑 TRÙM 👑",
            "fps_finished_title" to "🎮 HOÀN THÀNH VÒNG BẮN!",
            "fps_retry_btn" to "Tập Bắn Lại",
            "fps_pause_desc" to "Tạm Dừng FPS",
            "fps_paused_overlay_title" to "BẮN SÚNG TẠM DỪNG",
            "fps_paused_current_config" to "Cấu hình mạng hiện tại:",
            "fps_paused_reset_network_btn" to "Cài Lại Mạng Mượt (10ms Ping)",
            "fps_paused_resume_btn" to "Tiếp Tục",
            "fps_paused_exit_btn" to "Thoát",
            "fps_report_title" to "Báo Cáo Phân Tích Toàn Diện 📊",
            "fps_report_status_smooth" to "SẠCH MƯỢT",
            "fps_report_status_affected" to "BỊ ẢNH HƯỞNG",
            "fps_report_primary_diagnosis" to "Chẩn Đoán Kết Nối Chính:",
            "fps_report_accuracy_label" to "Chính Xác",
            "fps_report_physical_reflex_label" to "Phản Xạ Tay",
            "fps_report_total_response_label" to "Tổng Phản Hồi",
            "fps_report_pure_physical" to "Cơ học thuần",
            "fps_report_includes_lag" to "Gồm lag mạng",
            "fps_report_packet_loss_warn" to "Mất gói mạng: Hỏng %d cú bóp cò trúng (đạn ảo / chênh mạng)!",
            "fps_report_packet_loss_warn_short" to "⚠️ Mất gói mạng: Hỏng %d cú bóp cò trúng!",
            "fps_report_solutions_title" to "💡 Giải Pháp Tối Ưu Kết Nối:",
            "fps_report_target_fraction" to "%d/%d bia",
            "sim_enable_now" to "Bật Ngay",
            "fps_report_linh_chi_assessment" to "Linh Chi Nhận Xét 🌸",
            "fps_zoom_minimize" to "Thu Nhỏ",
            "fps_zoom_maximize" to "Phóng To 🎯",
            "fps_practice_again" to "Tập Bắn Lại",
            "fps_paused_current_config" to "Cấu hình mạng hiện tại:",
            "fps_paused_latency" to "Độ trễ (Ping):",
            "fps_paused_jitter" to "Biến động (Jitter):",
            "fps_paused_loss" to "Mất gói (Loss):",
            "fps_report_optimization_solutions" to "💡 Giải Pháp Tối Ưu Kết Nối:",
            "sphere_start_sim" to "Bắt đầu mô phỏng 🔮",
            "sphere_max_latency_reached" to "💥 ĐÃ ĐẠT ĐỘ TRỄ CỰC HẠN! 💥",
            "sphere_system_overloaded" to "Hệ thống quá tải khi FPS giảm còn 5!",
            "sphere_try_again" to "Thử Lại 🔄",
            "moba_phone_keys" to "📱 Phím Điện Thoại",
            "moba_classic_keys" to "💻 Phím Cổ Điển",
            "moba_surrender" to "🏳️ Đầu Hàng",
            "moba_seal_locked" to " (Ấn 🔒)",
            "moba_attack_btn" to "ĐÁNH",
            "moba_big_victory" to "CHIẾN THẮNG",
            "moba_big_defeat" to "THẤT BẠI",
            "moba_congrats_victory" to "CHÚC MỪNG CHIẾN THẮNG! 🎉",
            "moba_congrats_defeat" to "BẠN ĐÃ BỊ ĐÁNH BẠI! 💀",
            "moba_view_summary" to "XEM TỔNG KẾT TRẬN ĐẤU 📊",
            "moba_loading_game" to "Đang tải Đột Kích Vô Tận...",
            "moba_loading_desc" to "Đồng bộ gói tin máy chủ, định cấu hình độ trễ mạng...",
            "moba_arena_paused" to "ĐẤU TRƯỜNG TẠM DỪNG",
            "moba_select_champion_for_battle" to "CHỌN TƯỚNG XUẤT TRẬN:",
            "moba_launch_combat" to "XUẤT KÍCH TRẬN ĐẤU (Chọn %s) ⚔️",
            "moba_enemy_castle" to "L.Đài Địch",
            "moba_my_castle" to "L.Đài Ta",
            "moba_combat_statistics" to "📊 SỐ LIỆU ĐỐI KHÁNG:",
            "moba_kills_deaths" to "Hạ gục / Hy sinh",
            "moba_accuracy_rate" to "Tỉ lệ chính xác",
            "moba_interrupted_casts" to "Tung chiêu bị rớt mạng",
            "moba_interrupted_times" to "%d lần",
            "moba_recommended_solutions" to "💡 Giải pháp khuyên dùng:",
            "moba_rematch_revenge" to "Tái đấu phục thù ⚔️",
            "moba_select_another_hero" to "Chọn tướng khác",
            "moba_linh_chi_evaluation" to "Linh Chi Đánh Giá:",
            "moba_lag_diagnostics" to "📶 ĐỊNH CHẨN THÔNG SỐ LỆNH (LAG DIAGNOSTICS):",
            "moba_main_cause" to "Nguyên nhân chính: ",
            "moba_select_enemy_for_battle" to "CHỌN KẺ ĐỊCH SONG HÀNH:",
            "moba_unlocked" to "Đã mở khóa 🔓",
            "moba_locked" to "Chưa mở khóa 🔒 (Hạ Maloch để mở)",
            "moba_boss_warning" to "👹 PHỤ BẢN CUỒNG BẠO: TRÙM MALOCH XUẤT THẾ!",
            "moba_boss_mode_desc" to "Anh yêu đã chiến thắng 4 trận liên tiếp! Trận tiếp theo kẻ địch bắt buộc là TRÙM MALOCH với sức mạnh x1.5 lần! Hãy gánh em nha! 💕",
            "moba_wins_towards_boss" to "Tiến trình khiêu chiến Trùm: %d/4 trận thắng",
            "moba_reset_boss" to "Đặt Lại Tiến Trình Boss / Đấu Thường 🎯"
        ),
        AppLanguage.EN to mapOf(
            "sim_enable_now" to "Enable Now",
            "fps_report_linh_chi_assessment" to "Linh Chi Assessment 🌸",
            "fps_zoom_minimize" to "Minimize",
            "fps_zoom_maximize" to "Maximize 🎯",
            "fps_practice_again" to "Practice Again",
            "fps_paused_current_config" to "Current Network Configuration:",
            "fps_paused_latency" to "Latency (Ping):",
            "fps_paused_jitter" to "Fluctuation (Jitter):",
            "fps_paused_loss" to "Packet Loss (Loss):",
            "fps_report_optimization_solutions" to "💡 Connection Optimization Solutions:",
            "sphere_start_sim" to "Start Simulation 🔮",
            "sphere_max_latency_reached" to "💥 MAXIMUM LATENCY REACHED! 💥",
            "sphere_system_overloaded" to "System overloaded as FPS dropped to 5!",
            "sphere_try_again" to "Try Again 🔄",
            "moba_phone_keys" to "📱 Phone Keys",
            "moba_classic_keys" to "💻 Classic Keys",
            "moba_surrender" to "🏳️ Surrender",
            "moba_seal_locked" to " (Seal 🔒)",
            "moba_attack_btn" to "ATTACK",
            "moba_big_victory" to "VICTORY",
            "moba_big_defeat" to "DEFEAT",
            "moba_congrats_victory" to "CONGRATULATIONS ON VICTORY! 🎉",
            "moba_congrats_defeat" to "YOU HAVE BEEN DEFEATED! 💀",
            "moba_view_summary" to "VIEW MATCH SUMMARY 📊",
            "moba_loading_game" to "Loading Infinite Assault...",
            "moba_loading_desc" to "Synchronizing server packets, configuring network latency...",
            "moba_arena_paused" to "ARENA DUEL PAUSED",
            "moba_select_champion_for_battle" to "SELECT CHAMPION FOR BATTLE:",
            "moba_launch_combat" to "LAUNCH COMBAT (Select %s) ⚔️",
            "moba_enemy_castle" to "En. Castle",
            "moba_my_castle" to "My Castle",
            "moba_combat_statistics" to "📊 COMBAT STATISTICS:",
            "moba_kills_deaths" to "Kills / Deaths",
            "moba_accuracy_rate" to "Accuracy Rate",
            "moba_interrupted_casts" to "Interrupted casts (Packet Loss)",
            "moba_interrupted_times" to "%d times",
            "moba_recommended_solutions" to "💡 Recommended solutions:",
            "moba_rematch_revenge" to "Rematch & Revenge ⚔️",
            "moba_select_another_hero" to "Select another hero",
            "moba_linh_chi_evaluation" to "Linh Chi's Evaluation:",
            "moba_lag_diagnostics" to "📶 LAG DIAGNOSTICS:",
            "moba_main_cause" to "Main cause: ",
            "moba_select_enemy_for_battle" to "SELECT ENEMY FOR BATTLE:",
            "moba_unlocked" to "Unlocked 🔓",
            "moba_locked" to "Locked 🔒 (Defeat Maloch to unlock)",
            "moba_boss_warning" to "👹 FRENZIED BOSS CHALLENGE: MALOCH AWAKENS!",
            "moba_boss_mode_desc" to "You have won 4 matches! The next match is forced against BOSS MALOCH with 1.5x stats! Carry me dear! 💕",
            "moba_wins_towards_boss" to "Boss Challenge Progress: %d/4 wins",
            "moba_reset_boss" to "Reset Boss Progress / Normal Match 🎯",

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
            "subtab_sphere" to "Sphere Sim 🔮",
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
            "history_empty_sim" to "No network simulation history records.",

            "sim_network_setup_title" to "Network Latency Configuration",
            "sim_game_target" to "Target Game",
            "fps_2d_title" to "2D FPS Reflex Trainer",
            "fps_2d_desc" to "2D FPS target shooter training to evaluate the exact impact of network latency (high ping, jitter fluctuations, or total packet loss).",
            "fps_new_badge" to "NEW",
            "moba_2d_title" to "Arena of Valor 2D",
            "moba_2d_desc" to "Practice skill dodging and high-precision hit-and-run under extremely high/red ping!",
            "sphere_sim_title" to "DOUBLE SPHERE SIMULATION",
            "sphere_sim_desc" to "Tap on the sphere to trigger duplication!",
            "sphere_total_created" to "Total spheres created: %d",
            "sphere_current_count" to "Count: %d",
            "history_game_filter_label" to "Game Filter:",
            "all_games_option" to "All",

            "gamer_rank_card_title" to "REFLEX LEADERBOARD 👑",
            "gamer_rank_card_subtitle" to "Arena of Valor & Valorant Grandmaster",
            "gamer_rank_card_rank_label" to "GRANDMASTER RANK:",
            "gamer_rank_card_best" to "Fastest reaction",
            "gamer_rank_card_total" to "Total Kills / Targets",
            "gamer_rank_card_total_pattern" to "%d kills",
            "gamer_rank_card_accuracy" to "Accuracy rate",
            "gamer_rank_card_badges_title" to "ACHIEVED TITLES 🏆",

            "rank_surveying_title" to "SURVEYING APPRENTICE 🔍",
            "rank_surveying_quote" to "Please complete a few more reflex training rounds so I can rank you accurately, my dear! 💕",
            "rank_challenger_title" to "SPEED CHALLENGER ⚡",
            "rank_challenger_quote" to "Insane speed, top-tier reflexes matching the world's best professional esports players! 👑",
            "rank_master_title" to "ELITE MASTER 🔮",
            "rank_master_quote" to "Faster than thought! Ultra-fast reflexes, dodging skills like wind god Yasuo! 🌪️",
            "rank_diamond_title" to "TACTICAL DIAMOND 🛡️",
            "rank_diamond_quote" to "Sharp and skilled reflexes. You are the rock carrying the team in every combat! ⚔️",
            "rank_platinum_title" to "SOLID PLATINUM 🎖️",
            "rank_platinum_quote" to "Very consistent hand rhythm. Optimize your ping just a bit and you'll climb straight to Challenger! 🚀",
            "rank_gold_title" to "RESILIENT GOLD/BRONZE 🪵",
            "rank_gold_quote" to "Even with red laggy ping or extreme packet loss, your resolute fighting spirit is still the absolute best! 💪",

            "badge_light_title" to "⚡ LIGHT SPEED",
            "badge_light_desc" to "Insane reflex < 200ms",
            "badge_assassin_title" to "⚔️ ASSASSIN",
            "badge_assassin_desc" to "Defeated over 30 targets",
            "badge_sharpshooter_title" to "🎯 SHARPSHOOTER",
            "badge_sharpshooter_desc" to "Excellent accuracy ≥ 85%",
            "badge_unyielding_title" to "🛡️ BẤT KHUẤT",
            "badge_unyielding_desc" to "Trained diligently ≥ 8 rounds",
            "badge_slayer_title" to "👑 DEMON SLAYER",
            "badge_slayer_desc" to "Defeated Demon Lord Maloch",

            "medal_victory_perfect" to "🏆 PERFECT VICTORY",
            "medal_victory_perfect_desc" to "Smashed the enemy base!",
            "medal_warrior_clash" to "🔥 COMPETITIVE FIGHTER",
            "medal_warrior_clash_desc" to "Put massive pressure on Maloch!",
            "medal_conqueror" to "🛡️ UNBEATABLE",
            "medal_conqueror_desc" to "Broke through solid defense lines!",
            "medal_perfect_mage" to "✨ FLAWLESS MAGE",
            "medal_perfect_mage_desc" to "Casted skills smoothly like flowing water!",
            "medal_defeat_regret" to "💀 REGRETFUL DEFEAT",
            "medal_defeat_regret_desc" to "Ping spikes hindered golden hands!",
            "medal_red_ping_victim" to "📶 RED PING VICTIM",
            "medal_red_ping_victim_desc" to "Disconnected during a critical combo!",
            "medal_persistence" to "🏋️ RESILIENT EFFORT",
            "medal_persistence_desc" to "Kept fighting resolutely despite high latency!",

            "fps_select_mode_label" to "Select Game Mode:",
            "fps_select_weapon_label" to "Select Weapon (SFX):",
            "fps_mode_classic" to "Classic 🎯",
            "fps_mode_bottle" to "Bottle Shooting 🍾",
            "fps_mode_fast" to "Light Speed ⚡",
            "fps_mode_sniper" to "Sniper Scope 🔭",
            "fps_mode_boss" to "Alien Boss Battle 👾",
            "fps_mode_zombie" to "Zombie Hunt 🧟",
            "fps_mode_continuous" to "Continuous Fire 🔥",
            "fps_weapon_pistol" to "Pistol 🔫",
            "fps_weapon_ak47" to "AK-47 Rifle ⚔️",
            "fps_weapon_shotgun" to "Shotgun 💥",
            "fps_weapon_sniper" to "AWM Sniper 🔭",
            
            "fps_zoom_title" to "Shooting Range Opened in Zoom Mode 🎯",
            "fps_zoom_desc" to "The shooting screen has been fully zoomed and centered to increase aiming accuracy.",
            "fps_zoom_reopen_btn" to "Show Zoom Screen Again",
            "fps_difficulty_adaptive" to "Adaptive Target Speed (Lag):",
            "fps_start_btn" to "Start FPS Practice",
            "fps_connecting" to "Connecting to game server...",
            "fps_boss_tag" to "👑 BOSS 👑",
            "fps_finished_title" to "🎮 SHOOTING ROUND COMPLETED!",
            "fps_retry_btn" to "Practice Again",
            "fps_pause_desc" to "Pause FPS Game",
            "fps_paused_overlay_title" to "SHOOTING PAUSED",
            "fps_paused_current_config" to "Current network configuration:",
            "fps_paused_reset_network_btn" to "Reset Smooth Network (10ms Ping)",
            "fps_paused_resume_btn" to "Resume",
            "fps_paused_exit_btn" to "Exit",
            "fps_report_title" to "Comprehensive Diagnostic Report 📊",
            "fps_report_status_smooth" to "SMOOTH & CLEAN",
            "fps_report_status_affected" to "AFFECTED BY LAG",
            "fps_report_primary_diagnosis" to "Primary Network Diagnosis:",
            "fps_report_accuracy_label" to "Accuracy",
            "fps_report_physical_reflex_label" to "Physical Reflex",
            "fps_report_total_response_label" to "Total Response",
            "fps_report_pure_physical" to "Pure physical",
            "fps_report_includes_lag" to "Includes network lag",
            "fps_report_packet_loss_warn" to "Packet loss: Failed %d hit triggers (virtual bullet / hit registration delay)!",
            "fps_report_packet_loss_warn_short" to "⚠️ Packet loss: Failed %d hit triggers!",
            "fps_report_solutions_title" to "💡 Connection Optimization Solutions:",
            "fps_report_target_fraction" to "%d/%d targets"
        )
    )

    fun getString(key: String, lang: AppLanguage = currentLanguage): String {
        return translations[lang]?.get(key) ?: translations[AppLanguage.VI]?.get(key) ?: key
    }
}

fun t(key: String): String {
    return LocaleManager.getString(key, LocaleManager.currentLanguage)
}

fun getLocalizedText(text: String): String {
    if (LocaleManager.currentLanguage != AppLanguage.EN) return text
    
    val trimmed = text.trim()
    
    // Static strings
    when {
        trimmed == "Nhấn nút để bắt đầu kiểm tra phản xạ của bạn khi mạng lag!" -> 
            return "Press the button to start testing your reflexes under network lag!"
        
        // MOBA General & Skills
        trimmed == "Chiêu 1: Lôi Quang" -> return "Skill 1: Thunder Light"
        trimmed == "⚡ Sát thương fan-shape" -> return "⚡ Fan-shaped damage"
        trimmed == "Chiêu 2: Lôi Động" -> return "Skill 2: Thunder Movement"
        trimmed == "✨ Blink dịch chuyển" -> return "✨ Blink teleportation"
        trimmed == "Ult: Lôi Điểu" -> return "Ult: Thunderbird"
        trimmed == "🔥 Kết liễu cực đau" -> return "🔥 Highly lethal finisher"

        trimmed == "Chiêu 1: Vô Ảnh Vực" -> return "Skill 1: Phantom Domain"
        trimmed == "⚔️ Lướt & Bóng ảo (Stun)" -> return "⚔️ Dash & Shadow Clone (Stun)"
        trimmed == "Chiêu 2: Vô Ảnh Trận" -> return "Skill 2: Phantom Field"
        trimmed == "🛡️ Né đòn & Chậm rìa" -> return "🛡️ Dodge & Slow at edge"
        trimmed == "Ult: Ảo Ảnh Trảm" -> return "Ult: Phantom Slash"
        trimmed == "🔥 Trảm liên hoàn (Cần 4 Ấn)" -> return "🔥 Continuous Slashes (Needs 4 Seals)"

        trimmed == "Chiêu 1: Bão Kiếm" -> return "Skill 1: Steel Tempest"
        trimmed == "⚔️ Đâm kiếm tích lũy tụ bão phóng lốc xoáy" -> return "⚔️ Thrust sword to stack Steel Tempest & launch tornado"
        trimmed == "Chiêu 2: Tường Gió" -> return "Skill 2: Wind Wall"
        trimmed == "🌪️ Dựng tường chắn mọi chiêu thức địch" -> return "🌪️ Build wall to block all enemy projectiles"
        trimmed == "Ult: Trăn Trối" -> return "Ult: Last Breath"
        trimmed == "⚡ Bay chém địch bị hất tung (Cần khống chế)" -> return "⚡ Strike airborne enemies (Needs knockup control)"

        trimmed == "Chiêu 1: Đao Quét Thăng Hoa" -> return "Skill 1: Rotary Sweep"
        trimmed == "🤖 Quét đao thăng hoa & Beta phụ kích" -> return "🤖 Sword sweep & Beta support fire"
        trimmed == "Chiêu 2: Đao Quét Năng Lượng" -> return "Skill 2: Force Sweep"
        trimmed == "🛡️ Vung thương quét tròn & Hồi HP" -> return "🛡️ Sweep spear in circle & restore HP"
        trimmed == "Ult: Mũi Giáo Alpha" -> return "Ult: Spear of Alpha"
        trimmed == "🔥 Lao thẳng hất tung & Beta xả siêu Orbital Laser" -> return "🔥 Charge to knock up & Beta fires Orbital Laser"

        trimmed == "Chiêu 1: Vũ Điệu Chinh Phục" -> return "Skill 1: Conquest Dance"
        trimmed == "🟢 Chém 3 đường Gió Xanh phong ấn & Hồi 10% HP" -> return "🟢 3 Green Wind slash seals & heals 10% HP"
        trimmed == "Chiêu 2: Gió Tung Hoành" -> return "Skill 2: Roaring Wind"
        trimmed == "💨 Lướt kép 2 lần né tránh đột kích" -> return "💨 Double dash to evade and strike"
        trimmed == "Ult: Vũ Điệu Đại Thánh" -> return "Ult: Great Sage Dance"
        trimmed == "🔥 Nhảy vút lên không & Đâm plunge 2 lần chấn động" -> return "🔥 Leap high & plunge twice to create shockwaves"

        trimmed == "Chiêu 1: Chuyến Săn" -> return "Skill 1: Hunting Season"
        trimmed == "🏹 Tiêu đỏ nổ lan" -> return "🏹 Red glaive splash explosion"
        trimmed == "Chiêu 2: Lời Nguyền" -> return "Skill 2: Death Curse"
        trimmed == "🌀 Tiêu vàng gây choáng" -> return "🌀 Yellow glaive stuns"
        trimmed == "Ult: Bão Đạn" -> return "Ult: Bullet Storm"
        trimmed == "🔥 Xả bão 6 đạn bạc" -> return "🔥 Discharge 6 silver bullets"

        // Hero Cards Description Info
        trimmed == "Lôi Điện Pháp Sư" -> return "Thunder Spellcaster"
        trimmed == "• Nội tại sét quay quanh sát thương tự động" -> return "• Passive lightning orbits and attacks automatically"
        trimmed == "• Cơ động cực cao, có kĩ năng dịch chuyển" -> return "• Extremely high mobility with blink skill"

        trimmed == "Xạ Thủ Ám Khí" -> return "Demon Hunter"
        trimmed == "• Đòn đánh thường 3 đổi phi tiêu ngẫu nhiên" -> return "• 3rd basic attack throws a random glaive"
        trimmed == "• Đòn khống chế choáng cực mạnh từ phi tiêu vàng" -> return "• Strong stun control from golden glaive"

        trimmed == "Sát Thủ Lãng Khách" -> return "The Wanderer"
        trimmed == "• Đòn đánh thường đủ 4 lần giải phong ấn" -> return "• 4 basic attacks unlock ultimate seal"
        trimmed == "• Lước ảo ảnh giật bóng và vô ảnh trận né đòn cực ảo" -> return "• Dash with shadow clone, dodge with phantom field"

        trimmed == "Kiếm Sĩ Gió Phương Bắc" -> return "Northern Wind Swordsman"
        trimmed == "• Đâm kiếm tích lũy Bão Kiếm và phóng Lốc Xoáy" -> return "• Thrust sword to stack Steel Tempest & launch tornado"
        trimmed == "• Không dùng mana, dựng Tường Gió chặn mọi chiêu thức" -> return "• No mana cost, cast Wind Wall to block all projectiles"

        trimmed == "Kẻ Đi Săn Tương Lai" -> return "Future Hunter"
        trimmed == "• Sát thương công nghệ cybernetic chân thật từ drone Beta" -> return "• Cybernetic technology damage supported by Beta drone"
        trimmed == "• Phóng Mũi Giáo Alpha cực đại quét laser hủy diệt" -> return "• Launch Spear of Alpha for devastating laser strike"

        trimmed == "Hộ Pháp Dạ Xoa" -> return "Yaksha Guardian"
        trimmed == "• Gió Xanh chém phong ấn hồi phục 10% HP" -> return "• Green Wind slashes target to heal 10% HP"
        trimmed == "• Lướt dạ xoa liên tục hai lần né tránh và đột kích" -> return "• Dash twice consecutively to dodge and strike"
        trimmed == "• Vũ Điệu Đại Thánh nhảy cao plunged liên tục hai lần" -> return "• Great Sage Dance jumps high and plunges twice"

        // MOBA Live Logs & Game Engine Updates
        trimmed == "🎯 Maloch chạm rìa VÔ ẢNH TRẬN! Bị giảm giáp và bị Choáng mạnh!" -> return "🎯 Maloch touched the edge of PHANTOM FIELD! Armor reduced and heavily Stunned!"
        trimmed == "🎯 Maloch nằm trong VÔ ẢNH TRẬN! Bị sát thương cát vàng bào mòn!" -> return "🎯 Maloch is inside PHANTOM FIELD! Corroded by golden sand storm damage!"
        trimmed == "⚔️ Murad tung ẢO ẢNH TRẢM! Hóa thành 5 luồng kiếm khí chém nát đội hình địch!" -> return "⚔️ Murad casts PHANTOM SLASH! Transforms into 5 sword waves to shred enemy ranks!"
        trimmed == "⚡ Tulen tung LÔI QUANG! Phóng 3 tia điện càn quét kẻ địch!" -> return "⚡ Tulen casts THUNDERBOLT! Launches 3 electric beams to sweep enemies!"
        trimmed == "⚠️ Tulen: LÔI ĐIỂU cần mục tiêu để khóa!" -> return "⚠️ Tulen: THUNDERBIRD requires a locked target!"
        trimmed == "🎯 Tulen phát tia Laser định vị khóa mục tiêu Lôi Điểu!" -> return "🎯 Tulen emits a locating Laser to lock the Thunderbird target!"
        trimmed == "⚡ SIÊU PHẨM LÔI ĐIỂU! Phóng đại điểu truy sát mục tiêu bị khóa!" -> return "⚡ ULTIMATE THUNDERBIRD! Launches a giant bird to hunt down locked targets!"
        trimmed == "🟢 Xiao tung Vũ Điệu Chinh Phục: Tạo ra 3 đường chém Gió Xanh phong ấn và hồi phục 17% HP!" -> return "🟢 Xiao casts Conquest Dance: Creates 3 Green Wind slash seals and heals 17% HP!"
        trimmed == "👺 Xiao đeo Mặt Nạ Dạ Xoa và tung Vũ Điệu Đại Thánh! Nhảy vút lên không trung thực hiện 4 cú Plunge chấn động liên tục!" -> return "👺 Xiao wears Yaksha Mask and casts Great Sage Dance! Jumps high to perform 4 consecutive shockwave Plunges!"
        trimmed == "🎯 Xiao nhảy định vị và đâm xuống kẻ địch gần nhất!" -> return "🎯 Xiao jumps, locates and plunges down on the nearest enemy!"
        trimmed == "🟢 Không có mục tiêu! Xiao tự do nhảy đáp xuống vị trí chỉ định!" -> return "🟢 No target! Xiao leaps and lands freely at the designated spot!"
        trimmed == "🤖 Alpha phóng sóng năng lượng QUÉT ĐAO THĂNG HOA cực đẹp!" -> return "🤖 Alpha launches an energy wave for a stunning ROTARY SWEEP!"
        trimmed == "🛸 Beta phụ kích: Khai hỏa súng laser dọc đường quét!" -> return "🛸 Beta supports: Fires laser guns along the sweep path!"
        trimmed == "🤖 Alpha vung thương ĐAO QUÉT NĂNG LƯỢNG vòng tròn và phục hồi sinh lực!" -> return "🤖 Alpha sweeps spear for ENERGY CIRCLE SWEEP and restores HP!"
        trimmed == "🛸 Beta khai hỏa loạt súng đạn năng lượng hỗ trợ!" -> return "🛸 Beta fires a burst of energy bullets to assist!"
        trimmed == "🤖 SIÊU PHẨM MŨI GIÁO ALPHA! Lao thẳng hất tung và phóng Orbital Laser hủy diệt!" -> return "🤖 ULTIMATE SPEAR OF ALPHA! Charges straight to knock up and launches a devastating Orbital Laser!"
        trimmed == "🏹 Valhein tung CHUYẾN SĂN ÁM ẢNH! Phi tiêu đỏ nổ lan diện rộng!" -> return "🏹 Valhein casts HUNTING SEASON! Explodes red glaive in a wide area!"
        trimmed == "🏹 Valhein tung LỜI NGUYỀN TỬ VONG! Phi tiêu vàng gây choáng cực lâu!" -> return "🏹 Valhein casts DEATH CURSE! Yellow glaive stuns for a long duration!"
        trimmed == "🏹 Valhein kích hoạt BÃO ĐẠN! Xả 6 đạn bạc hủy diệt mục tiêu cận kề!" -> return "🏹 Valhein activates BULLET STORM! Discharges 6 silver bullets at nearby targets!"
        trimmed == "⚠️ Bạn bị trúng QUỶ KIẾM của Maloch! Bị Chậm di chuyển 50%!" -> return "⚠️ You are hit by Maloch's CLEAVE! Slowed by 50%!"
        trimmed == "🌪️ LUYỆN NGỤC! Maloch hất tung bạn lên không trung!" -> return "🌪️ UNDERWORLD! Maloch knocks you up into the air!"
        trimmed == "😈 QUYẾT ĐỊNH QUỶ KIẾM! Kiếm của Maloch được LUYỆN KIẾM (Sát Thương Chuẩn & Hồi HP)!" -> return "😈 DEMON DECISION! Maloch's sword is ENCHANTED (True Damage & HP Regen)!"
        trimmed == "⚡ Tulen tích tụ 3 Quả Cầu Điện tích bao quanh bản thân! ⚡" -> return "⚡ Tulen accumulates 3 Charge Orbs around himself! ⚡"
        trimmed == "⚡ Tulen kích hoạt NỘI TẠI LÔI ĐIỆN! 5 luồng sét bao quanh sấm sét càn quét!" -> return "⚡ Tulen activates LIGHTNING PASSIVE! 5 lightning bolts orbit to sweep!"
        trimmed == "⚔️ Murad: GIẢI ẤN PHONG ẤN! ẢO ẢNH TRẢM ĐÃ KHÓA SẴN SÀNG! 🔥" -> return "⚔️ Murad: SEAL UNLOCKED! PHANTOM SLASH IS READY! 🔥"
        trimmed == "🛸 Beta oanh tạc: Bắn Laser Công Nghệ gây Sát Thương Chuẩn & hồi máu!" -> return "🛸 Beta bombardment: Fires Tech Laser dealing True Damage & healing HP!"
        trimmed == "👿 Kiếm của Maloch đã hết trạng thái LUYỆN KIẾM." -> return "👿 Maloch's sword enchantment has ended."
        trimmed == "⚔️ Vô Ảnh Trận của Murad đã kết thúc." -> return "⚔️ Murad's Phantom Field has ended."
        trimmed == "👺 HP xuống dưới 50%! Xiao tự động tháo Mặt Nạ Dạ Xoa để bảo vệ sinh mệnh!" -> return "👺 HP fell below 50%! Xiao automatically removes Yaksha Mask to protect life!"
        trimmed == "🟢 Mặt nạ Dạ Xoa của Xiao đã hết tác dụng." -> return "🟢 Xiao's Yaksha Mask has worn off."
        trimmed == "🌪️ Tường Gió của Yasuo đã biến tan." -> return "🌪️ Yasuo's Wind Wall has vanished."
        trimmed == "🌪️ Yasuo đi qua Tường Gió! Kỹ năng thứ hai chuyển thành chiêu Lướt Kép hai lần (Double Dash) cực đỉnh!" -> return "🌪️ Yasuo went through Wind Wall! Skill 2 upgraded to Double Dash!"
        trimmed == "⚔️ Yasuo: Cộng dồn Tụ Bão đã hết thời gian duy trì!" -> return "⚔️ Yasuo: Steel Tempest stacks expired!"
        trimmed == "⚔️ Murad: Phong ấn cổ đã tan biến hoàn toàn. Hãy đánh thường để tích lại!" -> return "⚔️ Murad: Ancient seal has faded. Attack to stack again!"
        trimmed == "🔥 LÍNH SIÊU CẤP ĐÃ XUẤT TRẬN! Kích hoạt đường tiến công trực tiếp dẫn thẳng đến lâu đài!" -> return "🔥 SUPER MINIONS HAVE SPAWNED! Activating direct lane straight to the Castle!"
        trimmed == "🛡️ Lính 3 đường (Kinh Thống, Giữa, Rồng) đã xuất trận!" -> return "🛡️ Minions have spawned on all 3 lanes (Slayer, Mid, Dragon)!"
        trimmed == "⚡ Hồi phục! Quả cầu điện chạm địch hồi 5% HP!" -> return "⚡ Recovered! Electric orb hit enemies to heal 5% HP!"
        trimmed == "🌪️ Tường Gió của Yasuo đã cản thành công đòn đánh của kẻ địch!" -> return "🌪️ Yasuo's Wind Wall successfully blocked enemy projectile!"
        trimmed == "🌪️ TỤ BÃO SẴN SÀNG! Đợt Bão Kiếm tiếp theo sẽ phóng Lốc Xoáy hất tung!" -> return "🌪️ STEEL TEMPEST READY! Next Steel Tempest will launch a knockup Tornado!"
        trimmed == "🌪️ Lốc Xoáy hất tung Maloch cực mạnh!" -> return "🌪️ Tornado knocked up Maloch powerfully!"
        trimmed == "⚠️ Bạn bị Maloch chém rìu gây trì hoãn di chuyển (Chậm 50%!)" -> return "⚠️ You are hit by Maloch's cleave, slowing movement by 50%!"
        trimmed == "🎯 Choáng! Maloch bị Valhein hóa đá găm phi tiêu vàng (Choáng 2s)!" -> return "🎯 Stunned! Maloch got stunned by Valhein's yellow glaive (2s Stun)!"
        trimmed == "⚡ SIÊU PHẨM LÔI ĐIỂU! Oanh tạc dứt điểm cực đau lên Maloch!" -> return "⚡ ULTIMATE THUNDERBIRD! Deals massive finishing blast to Maloch!"
        trimmed == "💚 HASAGI! Đòn đánh thường nảy trúng 5 mục tiêu, tung Bão Kiếm Vòng Cung quét sạch và kích hoạt Tụ Bão!" -> return "💚 HASAGI! Basic attack hit 5 targets, swept Sweep Tempest and activated Storm!"
        trimmed == "👿 Maloch đã hồi sinh từ Tế Đàn Địch và tiến ra Đại Lộ!" -> return "👿 Maloch has respawned at Enemy Altar and is heading down the Avenue!"
        trimmed == "👿 Maloch tụ lực phóng lên không trung thi triển LUYỆN NGỤC! 🌪️" -> return "👿 Maloch charges up, leaping into the air to cast UNDERWORLD! 🌪️"
        trimmed == "💥 LUYỆN NGỤC giáng lâm! Maloch giẫm nát mặt đất hất tung kẻ địch trong vùng!" -> return "💥 UNDERWORLD landed! Maloch slams the ground and knocks up enemies in the area!"
        trimmed == "👿 Maloch thi triển ĐOẠT HỒN! Tước đoạt sinh hồn đối thủ tạo lá chắn cực lớn!" -> return "👿 Maloch casts SOUL SNATCH! Deprives opponent souls to create a massive shield!"
        trimmed == "👿 Maloch tụ lực vung QUỶ KIẾM càn quét cực rộng!" -> return "👿 Maloch charges up to swing CLEAVE in a huge sweep!"
        trimmed == "⚠️ CẢNH BÁO: BẠN ĐANG ĐI VÀO TẦM BẮN TRỤ ĐỊCH MÀ KHÔNG CÓ LÍNH TANK! ⚠️" -> return "⚠️ WARNING: ENTERING ENEMY TURRET RANGE WITHOUT TANK MINIONS! ⚠️"
        trimmed == "💀 Bạn đã hy sinh! Hồi sinh tại Tế Đàn sau 4 giây..." -> return "💀 You have fallen! Respawning at Altar in 4 seconds..."
        trimmed == "🛡️ Bạn đã hồi sinh! Hãy cẩn thận và tiến lên đẩy nát lâu đài địch!" -> return "🛡️ You have respawned! Be careful and advance to crush the enemy Castle!"

        // Evaluations & Diagnostic Reports
        trimmed == "Linh Chi Đánh Giá:" -> return "Linh Chi's Evaluation:"
        trimmed == "Kết nối mạng tuyệt vời, không phát hiện lag cơ sở." -> return "Excellent network connection, no base lag detected."
        trimmed == "Relatively stable connection, the main issue is unsynchronized coordination with minions." -> return "Relatively stable connection, the main issue is unsynchronized coordination with minions."
        trimmed == "Kết nối tương đối ổn định, sự cố chính là do phối hợp lính chưa nhịp nhàng." -> return "Relatively stable connection, the main issue is unsynchronized coordination with minions."
        trimmed == "Chiến Thắng (Sập Trụ Địch) 🎉" -> return "Victory (Enemy Turret Destroyed) 🎉"
        trimmed == "Thất Bại (Sập Trụ Ta) 💀" -> return "Defeat (Allied Turret Destroyed) 💀"
        trimmed == "Thương anh yêu quá đi à... 🥺 Trận này thua hoàn toàn là do cái mạng Wi-Fi lag giật dã man kia làm anh bị trễ nhịp di chuyển, chiêu thức thì cứ bị rớt vô cớ á! Đừng buồn nha anh, có em ở đây dỗ dành nè. Nghe Linh Chi chỉ cách đổi DNS 1.1.1.1 hoặc cắm mạng LAN rồi tụi mình phục thù gỡ gạc nha chồng yêu! 😘" ->
            return "I feel so bad for you, my dear... 🥺 This defeat was entirely due to that terrible lagging Wi-Fi network, which delayed your movement and dropped your skills! Don't be sad, I'm here to comfort you. Let's follow Linh Chi's advice to change DNS to 1.1.1.1 or use a LAN cable, and then we'll get our revenge, my beloved husband! 😘"
        trimmed == "Huhu, trận này sập trụ uổng ghê á anh yêu... 😢 Chắc tại nãy lo ngắm dung nhan của Linh Chi nên sẩy tay một xíu đúng không nè? Không sao hết á, làm lại trận mới cẩn thận núp sau lính tank trụ là thắng chắc luôn, em tin tưởng anh gánh em mà! 💕" ->
            return "Huhu, what a regretful defeat, my love... 😢 Maybe you were too busy staring at Linh Chi's face that you made a little slip, right? It's okay, let's play another round, be careful to hide behind minions to tank the turret, and we'll definitely win. I believe you can carry me! 💕"
        trimmed == "Trời ơi! Anh yêu đỉnh quá xá luôn á! 💕 Mạng lag giật ping cao đỏ lòm mà anh vẫn gánh team, hạ gục Maloch sập cả trụ địch luôn! Đúng là đôi tay vàng của chồng em có khác. Nhưng nhớ nghe em khuyên sửa mạng đi để lần sau đánh mượt gánh em leo rank Cao Thủ nữa nhé! 😘🎮" ->
            return "Oh my god! You are absolutely amazing, my love! 💕 Even with laggy, high red ping, you still carried the team, defeated Maloch, and destroyed the enemy turret! You truly have golden hands. But remember to fix your network so we can play smoothly next time and carry me to Master rank! 😘🎮"

        // Dynamic Logs
        trimmed.startsWith("⚡ Tulen lướt LÔI ĐỘNG (Lần") -> {
            val times = trimmed.substringAfter("Lần ").substringBefore("/3")
            return "⚡ Tulen dashes THUNDER MOVE (Time $times/3)! Teleports instantly dealing damage!"
        }
        trimmed.startsWith("🟢 Xiao tung Gió Tung Hoành (Lần") -> {
            val times = trimmed.substringAfter("Lần ").substringBefore("/2")
            return "🟢 Xiao casts Roaring Wind (Time $times/2)! Restores 15% HP and sweeps in a wide dash!"
        }
        trimmed.startsWith("🛡️ ĐOẠT HỒN! Maloch hút hồn") -> {
            val count = trimmed.substringAfter("hút hồn ").substringBefore(" mục tiêu")
            return "🛡️ SOUL SNATCH! Maloch steals souls of $count target(s) and gains a massive shield!"
        }
        trimmed.startsWith("⚔️ Murad tích ấn Phong Ấn:") -> {
            val count = trimmed.substringAfter("Phong Ấn: ")
            return "⚔️ Murad accumulates Seal stacks: $count"
        }
        trimmed.startsWith("🤖 Alpha: Tích điện công nghệ") -> {
            val count = trimmed.substringAfter("công nghệ (")
            return "🤖 Alpha: Cyber charging ($count"
        }
        trimmed.startsWith("⚔️ Murad: Các luồng Phong Ấn cổ đang dần biến mất...") -> {
            val nextVal = trimmed.substringAfter("(").substringBefore(")")
            return "⚔️ Murad: Ancient seal stacks are fading... ($nextVal)"
        }
        trimmed.startsWith("⚔️ Yasuo tích lũy Bão Kiếm:") -> {
            val count = trimmed.substringAfter("Bão Kiếm: ")
            return "⚔️ Yasuo stacks Steel Tempest: $count"
        }
        trimmed.startsWith("🌪️ Kiếm khí của Yasuo nảy ngẫu nhiên sang mục tiêu tiếp theo!") -> {
            val count = trimmed.substringAfter("tiếp theo! (").substringBefore(")")
            return "🌪️ Yasuo's sword wave bounced randomly to next target! ($count)"
        }
        trimmed.startsWith("TRẬN ĐẤU KẾT THÚC!") -> {
            val status = trimmed.substringAfter("KẾT THÚC! ").substringBefore(". Xem")
            val enStatus = if (status.contains("Chiến Thắng")) "VICTORY" else "DEFEAT"
            return "MATCH ENDED! $enStatus. See the detailed analysis report below!"
        }
        trimmed.startsWith("Chiến thắng tưng bừng luôn anh ơi!") -> {
            val hero = trimmed.substringAfter("tung combo ").substringBefore(" chuẩn")
            return "Glorious victory, my dear! 😍 Smooth network connection helped you cast $hero's combo with absolute precision. I rate you 100/100! Will you carry me forever? 💕"
        }
        trimmed.startsWith("Trễ ping cực kỳ cao") && trimmed.contains("Lệnh di chuyển bị chậm nhịp khiến bạn không thể né quỷ kiếm của Maloch.") -> {
            val ping = trimmed.substringAfter("cực kỳ cao (").substringBefore("ms)")
            return "Extremely high ping delay (${ping}ms). Movement commands are delayed, making it impossible to dodge Maloch's cleave."
        }
        trimmed.startsWith("Tỉ lệ rụng gói tin nghiêm trọng") && trimmed.contains("Rất nhiều kỹ năng tung ra của") -> {
            val loss = trimmed.substringAfter("nghiêm trọng (").substringBefore("% Packet")
            val hero = trimmed.substringAfter("tung ra của ").substringBefore(" bị")
            return "Severe packet loss rate (${loss}% Packet Loss). Many of ${hero}'s casted skills disappeared abnormally."
        }
        trimmed.startsWith("Ping biến động giật giật") && trimmed.contains("Game giật lag cục bộ, khó phán đoán hướng lướt lính.") -> {
            val jitter = trimmed.substringAfter("Jitter ").substringBefore("ms")
            return "Fluctuating ping (Jitter ${jitter}ms). Localized lagging, making it hard to predict minion dash directions."
        }
        trimmed.startsWith("Giúp ổn định thời gian phản hồi máy chủ game từ") -> {
            val ping = trimmed.substringAfter("máy chủ game từ ").substringBefore("ms")
            return "Helps stabilize game server response times from ${ping}ms down to < 15ms."
        }

        trimmed == "Nhấn nút để bắt đầu kiểm tra phản xạ của bạn khi mạng lag!" -> 
            return "Press the button to start testing your reflexes under network lag!"
        trimmed == "🧟 ĐẠI DỊCH ZOMBIE! Tiêu diệt toàn bộ zombie đang tiến từ trên xuống! Sau 30s Trùm Zombie sẽ xuất hiện!" ->
            return "🧟 ZOMBIE APOCALYPSE! Destroy all zombies coming from above! After 30s, Zombie Boss will appear!"
        trimmed == "🚨 CẢNH BÁO! TRÙM ZOMBIE KHỔNG LỒ ĐÃ XUẤT HIỆN! TIÊU DIỆT HẮN ĐỂ KẾT THÚC MÀN CHƠI!" ->
            return "🚨 WARNING! GIANT ZOMBIE BOSS HAS SPAWNED! DESTROY HIM TO END THE GAME!"
        trimmed == "⚠️ Zombie đã vượt qua ranh giới và cắn bạn! Mất 1 HP!" ->
            return "⚠️ A zombie crossed the line and bit you! Lost 1 HP!"
        trimmed == "👹 TRÙM CUỐI ĐANG DI CHUYỂN! Đừng vội bắn nhé, đợi hắn đứng im ra chiêu rồi hãy xả đạn!" ->
            return "👹 ULTIMATE BOSS IS MOVING! Don't shoot yet, wait until he is still to cast skills, then fire!"
        trimmed == "🌀 BOSS dịch chuyển tức thời! Hãy cẩn thận!" ->
            return "🌀 BOSS teleported! Watch out!"
        trimmed == "⚠️ BOSS ĐANG ĐỨNG IM TẬP TRUNG RA CHIÊU! BẮN HẮN NGAY!!!" ->
            return "⚠️ BOSS IS CONCENTRATING TO CAST SKILLS! SHOOT HIM NOW!!!"
        trimmed == "💥 CHÍNH XÁC! Boss trúng đạn và mất 1 HP!" ->
            return "💥 HIT! The boss got shot and lost 1 HP!"
        trimmed == "⚡ ĐOÀNG!!! BOSS ĐÃ BẮN TRÚNG BẠN! Bạn mất 1 mạng!" ->
            return "⚡ BANG!!! THE BOSS HIT YOU! You lost 1 life!"
        trimmed == "🎉 CHIẾN THẮNG!!! BẠN ĐÃ TIÊU DIỆT BOSS THÀNH CÔNG!" ->
            return "🎉 VICTORY!!! YOU HAVE SUCCESSFULLY DEFEATED THE BOSS!"
        trimmed == "💀 THẤT BẠI!!! BẠN ĐÃ BỊ BOSS HẠ GỤC!" ->
            return "💀 DEFEAT!!! YOU HAVE BEEN DEFEATED BY THE BOSS!"
        trimmed == "🛡️ Trùm đang né tránh! Hãy đợi hắn đứng yên chuẩn bị tấn công rồi mới bắn!" ->
            return "🛡️ Boss is dodging! Wait until he stands still preparing to attack, then shoot!"
        trimmed == "💨 Bắn hụt rồi! Hãy ngắm chính xác vào tên Boss ác độc nhé!" ->
            return "💨 Missed! Aim precisely at the evil Boss!"
        trimmed == "🎉 THỬ THÁCH HOÀN THÀNH! Hãy xem bảng đánh giá toàn diện bên dưới nha anh!" ->
            return "🎉 CHALLENGE COMPLETED! See the comprehensive evaluation report below, dear!"
        trimmed == "💨 Bắn hụt rồi! Hãy cố gắng nhắm trúng chai thủy tinh nhé!" ->
            return "💨 Missed! Try your best to hit the glass bottle!"
        trimmed == "💨 Đĩa bay nhanh quá! Hãy vuốt nhanh và bóp cò dứt khoát!" ->
            return "💨 The flying saucer is too fast! Swipe fast and trigger decisively!"
        trimmed == "💨 Lệch tâm kính ngắm! Hãy nín thở và bóp cò thật nhẹ nhàng!" ->
            return "💨 Reticle misaligned! Hold your breath and pull the trigger gently!"
        trimmed == "💨 Bắn trượt rồi anh ơi! Tập trung ngắm bắn vào tâm bia đỏ nhé!" ->
            return "💨 Shot missed! Focus and aim at the center of the red target!"
            
        // Dynamic ones
        trimmed.startsWith("⏳ Đạn đang bay") -> {
            val pingVal = trimmed.substringAfter("Trễ: ").substringBefore(" ms").trim()
            return "⏳ Bullet traveling... Synchronizing shot with server (Lag: $pingVal ms)"
        }
        trimmed.startsWith("❌ RỤNG MẠNG") -> {
            val targetNum = trimmed.substringAfter("bia ").substringBefore(" đã").trim()
            return "❌ PACKET LOSS! Shot hitting target $targetNum was lost!"
        }
        trimmed.startsWith("🎯 Hãy bắn lại") -> {
            val targetFraction = trimmed.substringAfter("số ").substringBefore("!").trim()
            return "🎯 Please re-shoot target $targetFraction! The previous shot was lost due to packet loss!"
        }
        trimmed.startsWith("💨 Chai thủy tinh") && trimmed.endsWith("rơi xuống đất vỡ vụn!") -> {
            val targetFraction = trimmed.substringAfter("Chai thủy tinh ").substringBefore(" rơi").trim()
            return "💨 Glass bottle $targetFraction fell to the ground and shattered!"
        }
        trimmed.startsWith("💨 Đĩa bay") && trimmed.endsWith("đã bay quá nhanh mất hút!") -> {
            val targetFraction = trimmed.substringAfter("Đĩa bay ").substringBefore(" đã").trim()
            return "💨 UFO $targetFraction flew away too fast and disappeared!"
        }
        trimmed.startsWith("💨 Mục tiêu siêu nhỏ") && trimmed.endsWith("lẩn trốn khỏi tầm ngắm!") -> {
            val targetFraction = trimmed.substringAfter("Mục tiêu siêu nhỏ ").substringBefore(" lẩn").trim()
            return "💨 Tiny target $targetFraction hid from your scope!"
        }
        trimmed.startsWith("💨 Bia số") && trimmed.endsWith("(Hết thời gian nhắm bắn)!") -> {
            val targetFraction = trimmed.substringAfter("Bia số ").substringBefore(" đã").trim()
            return "💨 Target $targetFraction disappeared (Aim timeout)!"
        }
        trimmed.startsWith("💥 XOẢNG! Chai thủy tinh") -> {
            val targetFraction = trimmed.substringAfter("Chai thủy tinh ").substringBefore(" đã").trim()
            val msVal = trimmed.substringAfter("Phản hồi thực tế: ").trim()
            return "💥 CLINK! Glass bottle $targetFraction shattered! Response: $msVal"
        }
        trimmed.startsWith("⚡ TUYỆT VỜI! Bắn hạ đĩa siêu tốc") -> {
            val targetFraction = trimmed.substringAfter("đĩa siêu tốc ").substringBefore(" thành").trim()
            val msVal = trimmed.substringAfter("Phản hồi: ").trim()
            return "⚡ EXCELLENT! Destroyed speed saucer $targetFraction! Response: $msVal"
        }
        trimmed.startsWith("🔭 HEADSHOT! Bắn tỉa cực chất vào mục tiêu") -> {
            val targetFraction = trimmed.substringAfter("mục tiêu ").substringBefore("!").trim()
            val msVal = trimmed.substringAfter("Phản hồi: ").trim()
            return "🔭 HEADSHOT! Splendid sniper shot on target $targetFraction! Response: $msVal"
        }
        trimmed.startsWith("💥 ĐÃ TIÊU DIỆT BIA SỐ") -> {
            val targetFraction = trimmed.substringAfter("BIA SỐ ").substringBefore("!").trim()
            val msVal = trimmed.substringAfter("Phản hồi thực tế: ").trim()
            return "💥 DESTROYED TARGET $targetFraction! Response: $msVal"
        }
        trimmed.endsWith("XUẤT HIỆN! NGẮM BẮN!") -> {
            val name = trimmed.substringBefore(" XUẤT").trim()
            val enName = when (name) {
                "BIA BẮN 🎯" -> "TARGET 🎯"
                "CHAI BIA 🍾" -> "BOTTLE 🍾"
                "BIA SIÊU TỐC ⚡" -> "SPEED TARGET ⚡"
                "BIA NGẮM BẮN 🔭" -> "SNIPER TARGET 🔭"
                else -> name
            }
            return "$enName SPAWNED! AIM AND FIRE!"
        }
        
        // Detailed Tips & Evaluations
        trimmed.contains("bóp cò liên thanh") && trimmed.contains("Rụng tận") -> {
            val lossCount = trimmed.substringAfter("Rụng tận ").substringBefore(" viên").trim()
            return "Dear, you pulled the trigger rapidly but the server ignored you! 😭 Lost $lossCount bullets, network lag really strains our chemistry!"
        }
        trimmed.contains("Viên đạn em gửi trao anh") && trimmed.contains("đau nhói") -> {
            return "The bullet I sent you drifted into the void... Packet loss makes Linh Chi's heart ache. Please fix your Wi-Fi so I can see you shoot straight into my heart! 🌸"
        }
        trimmed.startsWith("Mất gói tin nghiêm trọng") -> {
            val loss = trimmed.substringAfter("nghiêm trọng (").substringBefore("%)").trim()
            return "Severe packet loss ($loss%) made many of your hits disappear on the way to the server. Gun jams repeatedly!"
        }
        trimmed.contains("Bắn trúng bia xong đi pha cốc trà sữa") -> {
            val ping = trimmed.substringAfter("Trễ tận ").substringBefore(" ms").trim()
            return "You hit the target, went to make a boba milk tea, and came back only to see the target fall! 😂 With a delay of $ping ms, the enemy has already ran to another map, dear!"
        }
        trimmed.contains("Dù khoảng cách mạng xa xôi") && trimmed.contains("nguyên vẹn") -> {
            val ping = trimmed.substringAfter("tận ").substringBefore(" ms trễ").trim()
            return "Though the network distance is far at $ping ms delay, Linh Chi's love for you remains intact. But this ping makes you miss shots! 💕"
        }
        trimmed.startsWith("Độ trễ ping rất cao") -> {
            val ping = trimmed.substringAfter("rất cao (").substringBefore(" ms)").trim()
            return "Very high ping ($ping ms) creates a noticeable delay in shooting, bullets take too long to register hits."
        }
        trimmed.contains("Mạng nhảy Lambada lúc nhanh") -> {
            val jit = trimmed.substringAfter("Jitter ±").substringBefore("ms").trim()
            return "The network is dancing Lambada, fast and slow! Jitter ±$jit ms makes the gun stutter. 😵"
        }
        trimmed.contains("Nhịp tim em loạn nhịp vì anh") && trimmed.contains("lo lắng lắm") -> {
            return "My heart is skipping a beat for you, but this network jitter makes me worried. Fluctuating connection makes it hard to aim precisely, right?"
        }
        trimmed.startsWith("Độ biến động Jitter cao làm trễ mạng thay đổi") -> {
            return "High Jitter fluctuation causes the latency to change constantly, making it extremely difficult to time shots accurately."
        }
        trimmed.contains("Mạng ngon ping mượt mà anh yêu bắn thong thả") -> {
            val react = trimmed.substringAfter("Tận ").substringBefore("ms phản hồi").trim()
            return "Excellent network and smooth ping, but you shot as leisurely as walking in a park, dear! 😂 With a ${react}ms response, even a rabbit would have run away!"
        }
        trimmed.contains("Chắc anh đang mải ngắm dung nhan của Linh Chi") -> {
            return "Are you busy staring at Linh Chi's face that you forgot to shoot? I adore you, but remember to shoot targets faster next time! 😘"
        }
        trimmed.startsWith("Đường truyền mạng cực tốt nhưng tốc độ nhấp") -> {
            val react = trimmed.substringAfter("trung bình ").substringBefore("ms). Cần").trim()
            return "Excellent network connection but your physical click speed is a bit slow (average ${react}ms). Need to speed up your fingers!"
        }
        trimmed.contains("Trời ơi anh bắn như hack ấy") -> {
            val acc = trimmed.substringAfter("chính xác ").substringBefore("% mà").trim()
            val react = trimmed.substringAfter("phản xạ chỉ ").substringBefore("ms. Để").trim()
            return "Oh my god, you shot like a hacker! 10 out of 10! 😍 Accuracy $acc% and reflex only ${react}ms. Let me be your cheerleader forever!"
        }
        trimmed.contains("Cú bắn của anh đã găm thẳng vào trái tim") -> {
            return "Your shot pierced straight through Linh Chi's heart! 💓 Smooth ping and your talented hands made a shooting masterpiece!"
        }
        trimmed.startsWith("Kết nối cực kỳ ổn định, tốc độ phản hồi vật lý") -> {
            return "Extremely stable connection, excellent physical response speed. You are fully ready for the most intense ranked matches!"
        }
        trimmed.contains("Quá đẳng cấp anh yêu ơi!") -> {
            return "So classy, my love! 🧟‍♂️ Defeated the zombie pandemic and saved the beauty! Rewarding you with a thousand kisses! 😘"
        }
        trimmed.contains("Người hùng dũng cảm của em đã vượt qua đại lộ thây ma!") -> {
            return "My brave hero survived the zombie boulevard! Seeing you firmly protecting me from the monsters makes my heart flutter... 💕"
        }
        trimmed.contains("Đẩy lùi đợt tấn công thảm khốc của đại dịch Zombie") -> {
            return "Congratulations, hero! You repelled the catastrophic attack of the Zombie pandemic and successfully defeated the Zombie Lord!"
        }
        trimmed.contains("bị zombie cạp mất tiêu rồi anh iu ơi") -> {
            return "Ouch, got bitten by a zombie, my love! 😭 Wake up and drink boba milk tea to revive before it gets cold! 🥤"
        }
        trimmed.contains("Lũ thây ma thật đáng ghét khi làm anh mỏi mệt") -> {
            return "Those hateful zombies made you weary... Let me hold you tight, warming your soul. Let's do it again! 🌸"
        }
        trimmed.contains("Lũ Zombie đã vượt qua phòng tuyến") -> {
            return "Regrettably, the Zombies have breached our defense. Try to take them down before they reach the bottom of the screen!"
        }
        trimmed.contains("Anh yêu đỉnh chóp thực sự") -> {
            return "My love is truly the best! 👑 Defeated the evil boss and saved the entire server! From now on, I only adore you! 😍"
        }
        trimmed.contains("Chiến binh anh dũng của Linh Chi") -> {
            return "Linh Chi's brave warrior won! 🌸 Seeing you resiliently defeating the evil demon, my heart is overflowing with happiness. You are forever the only hero protecting my life! 💕"
        }
        trimmed.contains("xuất sắc đánh bại Boss tàn ác") -> {
            return "Congratulations on brilliantly defeating the cruel Boss! You have amazing reflexes and highly precise aiming."
        }
        trimmed.contains("trà sữa nguội hết rồi") -> {
            return "Hello my love, wake up! The boba tea is getting cold! 😭 The boss hit you and bruised your head, let me rub it for you, take revenge next time! 😘"
        }
        trimmed.contains("Linh Chi đau lòng quá khi thấy anh ngã xuống") -> {
            return "Linh Chi is so heartbroken to see you fall... Don't be sad, I'm always here to soothe you and give you strength. Stand up and fight again with me! 🌸"
        }
        trimmed.contains("Boss đã tiêu diệt bạn. Hãy chú ý canh lúc Boss đứng im") -> {
            return "Unfortunately, the Boss defeated you. Pay attention to when the Boss stands still concentrating energy to counterattack quickly!"
        }
        trimmed == "Quét Sạch Đại Dịch Zombie! 🎉" -> return "Wiped Out the Zombie Pandemic! 🎉"
        trimmed == "Bị Zombie Vượt Qua Phòng Tuyến 💀" -> return "Zombies Breached the Defense Line 💀"
        trimmed == "Đại Thắng Boss Trùm! 🎉" -> return "Grand Victory Over Ultimate Boss! 🎉"
        trimmed == "Thất Bại Trước Sức Mạnh Boss 💀" -> return "Defeated by Boss Power 💀"
        trimmed == "Xạ thủ vô song" -> return "Peerless Marksman"
        trimmed == "Kỹ năng ghìm tâm di chuột xuất sắc của bạn đã dọn sạch bầy zombie." -> 
            return "Your outstanding recoil control and mouse aim cleared the zombie swarm."
        trimmed == "Trùm cuối bị hạ" -> return "Ultimate Boss Defeated"
        trimmed == "Sức chịu đựng đáng kinh ngạc của Zombie Chúa cũng không chống đỡ nổi sát thương của bạn!" -> 
            return "Even the incredible endurance of the Zombie Lord couldn't withstand your damage!"
        trimmed == "Ưu tiên zombie đi nhanh" -> return "Prioritize Fast Zombies"
        trimmed == "Hãy hạ gục những con zombie di chuyển nhanh trước khi chúng kịp tiếp cận bạn." -> 
            return "Take down fast-moving zombies before they can reach you."
        trimmed == "Canh bắn trùm" -> return "Aim at Boss"
        trimmed == "Zombie Chúa có lượng máu cực lớn, cần tập trung xả đạn liên tục!" -> 
            return "The Zombie Lord has massive HP, continuous fire is required!"
        trimmed == "Đẳng cấp tuyển thủ" -> return "Pro Player Class"
        trimmed == "Anh giữ nguyên phong độ để bóp nghẹt mọi đối thủ." -> 
            return "Maintain your form to choke out all opponents."
        trimmed == "Thử thách khó hơn" -> return "Harder Challenge"
        trimmed == "Thử mô phỏng ping cao hơn để tăng độ khó khi đối đầu Boss!" -> 
            return "Try simulating higher ping to increase difficulty against the Boss!"
        trimmed == "Nhắm bắn khi Boss đứng im" -> return "Aim When Boss Stands Still"
        trimmed == "Chỉ bắn khi Boss chuyển sang trạng thái chuẩn bị ra chiêu (màu hổ phách)." -> 
            return "Only fire when the Boss transitions to preparing state (amber color)."
        trimmed == "Độ trễ thấp giúp ích" -> return "Low Latency Helps"
        trimmed == "Nếu ping cao quá bạn sẽ không kịp ngắt chiêu của Boss." -> 
            return "If ping is too high, you won't interrupt the Boss's cast in time."

        // Playable Maloch strings
        trimmed == "Ma Vương Hủy Diệt" -> return "Destructive Demon King"
        trimmed == "• Sát thương chuẩn cực rát từ chiêu Quỷ Kiếm" -> return "• Highly painful true damage from Cleave skill"
        trimmed == "• Luyện Ngục giáng xuống chấn động hất tung diện rộng" -> return "• Underworld leaps and knocks up wide area with high damage"
        trimmed == "Chiêu 1: Quỷ Kiếm" -> return "Skill 1: Cleave"
        trimmed == "😈 Vung đao chém quét & Luyện Kiếm hồi HP" -> return "😈 Swing sword to cleave & Enchant sword to restore HP"
        trimmed == "Chiêu 2: Đoạt Hồn" -> return "Skill 2: Soul Snatch"
        trimmed == "🛡️ Tước hồn giật giáp tạo khiên cực dày" -> return "🛡️ Deprive souls to gain a massive shield"
        trimmed == "Ult: Luyện Ngục" -> return "Ult: Underworld"
        trimmed == "🔥 Nhảy đáp hất tung diện rộng dồn sát thương" -> return "🔥 Leap and plunge to knock up wide area & deal damage"

        // Enemy S3 Impact explosions
        trimmed == "💥 Sét giáng! Lôi Điểu phát nổ cực mạnh dồn điện hất tung!" -> return "💥 Lightning strikes! Thunderbird explodes with massive force, knocking up targets!"
        trimmed == "💥 Bão đạn oanh tạc hất tung toàn diện!" -> return "💥 Bullet storm bombardment knocks up everything!"
        trimmed == "💥 Kiếm trận loé sáng cắt nát hất tung mặt đất!" -> return "💥 Sword formation shines bright, slicing and knocking up the ground!"
        trimmed == "💥 Bão cát hất tung giáng xuống chấn động!" -> return "💥 Sand storm strikes, landing a powerful ground knockup!"
        trimmed == "💥 Chùm laser huỷ diệt quét sạch hất tung!" -> return "💥 Destructive laser beam sweeps and knocks up targets!"
        trimmed == "💥 Trấn thiên hất tung kinh hồn!" -> return "💥 Ground-shaking plunge knocks up with terrifying impact!"

        // Dynamic Enemy prefix logs translator
        trimmed.startsWith("👿") -> {
            var res = trimmed
            res = res.replace("TRÙM Maloch", "BOSS Maloch")
            res = res.replace("đã hồi sinh từ Tế Đàn Địch và tiến ra Đại Lộ!", "has respawned at Enemy Altar and is heading down the Avenue!")
            
            // S3 descriptions
            res = res.replace("tụ tụ Lôi Quang phóng LÔI ĐIỂU sấm sét cực mạnh! ⚡", "accumulates Lightning to cast thunderous THUNDERBIRD! ⚡")
            res = res.replace("ném bão Phi Tiêu thi triển BÃO ĐẠN cực rát! 🏹", "throws a storm of Glaives casting highly painful BULLET STORM! 🏹")
            res = res.replace("biến ảo ảnh tung chiêu liên hoàn ẢO ẢNH TRẢM! 🗡️", "transforms into shadow clones to cast continuous PHANTOM SLASH! 🗡️")
            res = res.replace("lướt gió phóng lốc xoáy thi triển TRĂN TRỐI! 🌪️", "dashes with wind to release tornado casting LAST BREATH! 🌪️")
            res = res.replace("laser nạp đầy phát động HỦY DIỆT TOÀN DIỆN! 🤖", "charges full laser launching TOTAL DESTRUCTION! 🤖")
            res = res.replace("vung gậy giáng VŨ ĐIỆU ĐẠI THÁNH rung chuyển! 🟢", "swings staff unleashing ground-shaking GREAT SAGE DANCE! 🟢")
            res = res.replace("tụ lực phóng lên không trung thi triển LUYỆN NGỤC! 🌪️", "charges up, leaping into the air to cast UNDERWORLD! 🌪️")
            
            // S2 descriptions
            res = res.replace("thi triển Lôi Động giật điện tước đoạt sinh lực!", "casts Thunder Movement to zap and steal life!")
            res = res.replace("phóng Phi Tiêu Vàng khống chế tước hồn sinh giáp!", "launches Golden Glaive to stun, tarnish souls and gain shield!")
            res = res.replace("thi triển Vô Ảnh Trận tước hồn hồi giáp!", "casts Phantom Field to siphon souls and restore armor!")
            res = res.replace("dựng Phong Shield nhận khiên gió cực dày!", "builds Wind Shield to receive an extremely thick wind barrier!")
            res = res.replace("thi triển Lá Chắn Từ Trường hút hồn tạo lá chắn!", "casts Magnetic Shield to drain souls and create barrier!")
            res = res.replace("thi triển Giáp Diệp Dạ Xoa hồi phục năng lượng tạo khiên!", "casts Yaksha Leaf Armor to restore mana and create shield!")
            res = res.replace("thi triển ĐOẠT HỒN! Tước đoạt sinh hồn đối thủ tạo lá chắn cực lớn!", "casts SOUL SNATCH! Deprives opponent souls to create a massive shield!")
            
            // S1 descriptions
            res = res.replace("tụ lực vung Lôi Quang điện quét cực rộng!", "charges up to swing thunderous Rotary Sweep in a wide radius!")
            res = res.replace("vung tay ném Phi Tiêu Đỏ sát thương chí mạng cực lớn!", "swings hands to throw Red Glaive dealing massive critical damage!")
            res = res.replace("vút kiếm quét Vô Ảnh Vực càn quét diện rộng!", "swings sword sweeping Phantom Domain in a wide area!")
            res = res.replace("tụ gió vung Bão Kiếm quét cực rộng!", "gathers wind swinging Steel Tempest in a huge sweep!")
            res = res.replace("quét Mũi Giáo Cyber laser cực rộng!", "sweeps Cyber Spear launching a massive wide laser!")
            res = res.replace("vung chém Gió Xanh Dạ Xoa càn quét diện rộng!", "swings sweeping Green Wind of Yaksha in a wide radius!")
            res = res.replace("tụ lực vung QUỶ KIẾM càn quét cực rộng!", "charges up to swing CLEAVE in a huge sweep!")

            res = res.replace("Kiếm của Maloch đã hết trạng thái LUYỆN KIẾM.", "Maloch's sword enchantment has ended.")
            return res
        }
    }
    
    // Fallback dictionary for small phrases
    val wordMap = mapOf(
        "Mạng Mất Gói Nghiêm Trọng (Packet Loss" to "Severe Network Packet Loss (Packet Loss",
        "Độ Trễ Đường Truyền Cao (Ping Cao" to "High Connection Latency (High Ping",
        "Mạng Nhấp Nhô Biến Động (Jitter" to "Fluctuating Network Jitter (Jitter",
        "Phản Xạ Cơ Thể Chậm (Mạng Mượt Nhưng Tay Chậm)" to "Slow Physical Reflexes (Smooth Network, Slow Hands)",
        "Đường Truyền Hoàn Hảo - Phản Xạ Thần Sầu! ⚡" to "Perfect Connection - Godlike Reflexes! ⚡",
        "Cắm dây mạng LAN" to "Use Ethernet Cable",
        "Hạn chế dùng Wi-Fi bắt sóng yếu hoặc chập chờn." to "Avoid using Wi-Fi with weak or unstable signals.",
        "Đổi DNS chơi game" to "Change Gaming DNS",
        "Sử dụng DNS của Cloudflare (1.1.1.1) hoặc Google (8.8.8.8) để tối ưu định tuyến." to "Use Cloudflare DNS (1.1.1.1) or Google (8.8.8.8) to optimize routing.",
        "Đổi Server / VPN" to "Switch Server / VPN",
        "Kiểm tra xem anh có đang chọn nhầm server khu vực khác không, hoặc bật VPN giảm ping." to "Check if you selected the wrong regional server, or enable VPN to reduce ping.",
        "Tắt ứng dụng chạy ngầm" to "Close Background Apps",
        "Đóng Chrome, Torrent hoặc các ứng dụng ngầm ngốn băng thông tải về." to "Close Chrome, Torrent or background apps consuming download bandwidth.",
        "Khởi động lại Router" to "Restart Router",
        "Tắt nguồn router modem mạng, chờ 30 giây rồi bật lại để dọn dẹp bộ nhớ đệm." to "Power off your router/modem, wait 30 seconds and turn it back on to clear buffer cache.",
        "Tránh giờ cao điểm" to "Avoid Peak Hours",
        "Giờ cao điểm tối thường bị bóp băng thông hoặc nghẽn cục bộ đường truyền khu phố." to "Evening peak hours often suffer from bandwidth throttling or local congestion.",
        "Luyện tập cơ ngón tay" to "Train Finger Muscles",
        "Chơi các game click chuột nhanh hoặc tập trung cao độ hơn khi bia xuất hiện." to "Play fast clicker games or concentrate higher when target spawns.",
        "Tăng nhạy cảm ứng" to "Increase Touch Sensitivity",
        "Tinh chỉnh độ nhạy vuốt chạm trong cài đặt hệ thoại để xoay tâm nhanh hơn." to "Fine-tune swipe sensitivity in system settings to move reticle faster.",
        "Giữ vững phong độ" to "Maintain Form",
        "Cấu hình mạng này là niềm mơ ước của mọi game thủ chuyên nghiệp." to "This network configuration is a dream for every professional gamer.",
        "Tham gia leo rank ngay" to "Join Ranked Match Now",
        "Đường truyền hoàn hảo, không lo tụt rank do mạng." to "Flawless connection, no need to worry about dropping ranks due to lag."
    )
    
    for ((key, value) in wordMap) {
        if (trimmed.contains(key)) {
            return trimmed.replace(key, value)
        }
    }
    
    return text
}

fun getLocalizedGameName(game: String): String {
    return when (game) {
        "Liên Minh Huyền Thoại" -> if (LocaleManager.currentLanguage == AppLanguage.EN) "League of Legends" else "Liên Minh Huyền Thoại"
        "Liên Quân Mobile" -> if (LocaleManager.currentLanguage == AppLanguage.EN) "Arena of Valor" else "Liên Quân Mobile"
        "Đấu Trường MOBA" -> if (LocaleManager.currentLanguage == AppLanguage.EN) "MOBA Arena" else "Đấu Trường MOBA"
        "Bắn Súng FPS" -> if (LocaleManager.currentLanguage == AppLanguage.EN) "FPS Shooter" else "Bắn Súng FPS"
        "Quả Cầu 🔮" -> if (LocaleManager.currentLanguage == AppLanguage.EN) "Sphere Sim 🔮" else "Quả Cầu 🔮"
        else -> game
    }
}
