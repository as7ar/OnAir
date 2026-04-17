package kr.astar.onair.listeners

import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.velocity.ConnectionData
import kr.astar.onair.velocity.VeloChannel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.nio.charset.StandardCharsets
import java.util.UUID

class VelocityManager : PluginMessageListener {

    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray
    ) {
        if (channel != VeloChannel.CHANNEL) return

        val msg = String(message, StandardCharsets.UTF_8)
        val parts = msg.split(";")

        if (parts.isEmpty()) return

        when (parts[0]) {
            "connection" -> {
                if (parts.size < 5) return

                val uuid = parts[1]
                val display = parts[2]
                val platform = parts[3]
                val id = parts[4]

                val target = Bukkit.getPlayer(UUID.fromString(uuid)) ?: return

                val platformEnum = try {
                    Platforms.valueOf(platform)
                } catch (e: Exception) {
                    return
                }

                val data = ConnectionData(display, id)

                ConnectionManager.Manager(target)
                    .connect(platformEnum, data.display, data.id)
            }

            "disconnection" -> {
                if (parts.size < 3) return

                val uuid = parts[1]
                val platform = parts[2]

                val target = Bukkit.getPlayer(UUID.fromString(uuid)) ?: return

                val platformEnum = try {
                    Platforms.valueOf(platform)
                } catch (e: Exception) {
                    return
                }

                ConnectionManager.Manager(target)
                    .disconnect(platformEnum)
            }
        }
    }
}