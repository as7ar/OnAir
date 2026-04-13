package kr.astar.onair.utils

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import java.util.Optional

object VeloUtils {
    fun sendPluginMessage(server: RegisteredServer, identifier: ChannelIdentifier, data: ByteArray): Boolean {
        return server.sendPluginMessage(identifier, data)
    }
    fun sendPluginMessageToPlayer(
        player: Player, identifier: ChannelIdentifier, data: ByteArray
    ): Boolean {
        return player.sendPluginMessage(identifier, data)
    }

    fun sendPluginMessage(
        player: Player,
        identifier: ChannelIdentifier?,
        data: ByteArray?
    ): Boolean {
        val connection: Optional<ServerConnection?> = player.currentServer
        if (connection.isPresent) return connection.get().sendPluginMessage(
            identifier ?: return false,
            data ?: return false
        )
        return false
    }
}