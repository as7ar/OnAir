package kr.apo2073.onair.data.event

import org.bukkit.OfflinePlayer

data class ChatContent(
    val offlinePlayer: OfflinePlayer,
    val nickname: String?,
    val content: String
)

data class DonateContent(
    val offlinePlayer: OfflinePlayer,
    val nickname: String?,
    val payAmount: Double,
    val content: String?
)
