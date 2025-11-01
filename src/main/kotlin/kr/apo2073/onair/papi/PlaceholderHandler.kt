package kr.apo2073.onair.papi

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.player.Streamer
import kr.apo2073.onair.utils.Utils.toPlatform
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceholderHandler: PlaceholderExpansion() {
    private val plugin= OnAir.plugin

    override fun getIdentifier(): String = "onair"
    override fun getAuthor(): String = plugin.pluginMeta.authors.joinToString(", ")
    override fun getVersion(): String = plugin.pluginMeta.version

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player==null) return null
        val streamer= Streamer(player)
        val userData=streamer.getUserdata()

        if (params=="connection") {
            return userData.getConnections().joinToString(",") { it.name }
        }

        if (params.contains("check_connection_")) {
            val id=params.replace("check_connection_", "")
            return ConnectionManager.infoConfig.getString(id) ?: "false"
        }

        val plats="(youtube|toonation|chzzk|twitch|soop)"
        if (params.matches(Regex("${plats}_id"))) {
            val platform = params.replace("_key", "").toPlatform()
            return userData.getChannelData(platform).id
        }

        if (params.matches(Regex("${plats}_display"))) {
            val platform = params.replace("_key", "").toPlatform()
            return userData.getChannelData(platform).display
        }

        return params
    }
}