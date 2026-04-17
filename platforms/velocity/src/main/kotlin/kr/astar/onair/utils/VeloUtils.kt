package kr.astar.onair.utils

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import kr.astar.onair.velocity.ConnectionData
import java.util.*

object VeloUtils {
    val connectionInfo: MutableMap<UUID, MutableMap<String, ConnectionData>> = mutableMapOf()

    fun sendToServer(
        server: RegisteredServer,
        identifier: ChannelIdentifier,
        data: ByteArray
    ): Boolean {
        return server.sendPluginMessage(identifier, data)
    }

    fun sendToPlayer(
        player: Player,
        identifier: ChannelIdentifier,
        data: ByteArray
    ): Boolean {
        return player.sendPluginMessage(identifier, data)
    }

    fun sendToCurrentServer(
        player: Player,
        identifier: ChannelIdentifier,
        data: ByteArray
    ): Boolean {
        val connection = player.currentServer.orElse(null) ?: return false
        return connection.sendPluginMessage(identifier, data)
    }
}