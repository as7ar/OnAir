package kr.astar.onair

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import kr.astar.onair.utils.VeloUtils
import kr.astar.onair.velocity.ConnectionData
import kr.astar.onair.velocity.VeloChannel.CHANNEL
import org.slf4j.Logger
import templates.BuildConstants
import java.nio.charset.StandardCharsets
import java.util.*

@Plugin(
    id = "OnAir",
    name = "OnAir-Velo",
    version = BuildConstants.VERSION,
    url = "https://grassproject.github.io",
    authors = ["AS7AR"]
)
class OnAir @Inject constructor(
    val server: ProxyServer,
    val logger: Logger
) {
    val identifier = MinecraftChannelIdentifier.from(CHANNEL)
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        server.channelRegistrar.register(identifier)
    }

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        val id = event.identifier as? MinecraftChannelIdentifier ?: return
        if (id.id != CHANNEL) return

        val player = event.source as? Player ?: return

        val msg = String(event.data, StandardCharsets.UTF_8)
        val parts = msg.split(";")

        if (parts.isEmpty()) return

        when (parts[0]) {
            "connection" -> {
                if (parts.size < 4) return

                val platform = parts[1]
                val display = parts[2]
                val pid = parts[3]

                val map = VeloUtils.connectionInfo.computeIfAbsent(player.uniqueId) { mutableMapOf() }
                map[platform] = ConnectionData(display, pid)
            }

            "disconnection" -> {
                if (parts.size < 2) return
                val platform = parts[1]

                VeloUtils.connectionInfo[player.uniqueId]?.remove(platform)
                if (VeloUtils.connectionInfo[player.uniqueId]?.isEmpty() == true) {
                    VeloUtils.connectionInfo.remove(player.uniqueId)
                }
            }
        }
    }

    @Subscribe
    fun onServerChange(event: ServerConnectedEvent) {
        val player = event.player
        val connections = VeloUtils.connectionInfo[player.uniqueId] ?: return

        event.previousServer.ifPresent { prev ->
            connections.forEach { (platform, _) ->
                val msg = "disconnection;${player.uniqueId};$platform"
                prev.sendPluginMessage(identifier, msg.toByteArray(StandardCharsets.UTF_8))
            }
        }

        connections.forEach { (platform, data) ->
            val msg = "connection;${player.uniqueId};${data.display};$platform;${data.id}"
            event.server.sendPluginMessage(identifier, msg.toByteArray(StandardCharsets.UTF_8))
        }
    }
}