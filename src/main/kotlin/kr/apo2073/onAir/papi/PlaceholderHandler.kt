package kr.apo2073.onAir.papi

import kr.apo2073.onAir.OnAir
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceholderHandler: PlaceholderExpansion() {
    private val plugin= OnAir.plugin

    override fun getIdentifier(): String = "onair"
    override fun getAuthor(): String = plugin.pluginMeta.authors.joinToString(", ")
    override fun getVersion(): String = plugin.pluginMeta.version

    override fun onPlaceholderRequest(player: Player, params: String): String? {

        return null
    }
}