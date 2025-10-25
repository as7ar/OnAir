package kr.apo2073.onAir.papi

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.utils.Streamer
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
        if (params.contains("")) {} // todo:getter of player's platform key
        return params
    }
}