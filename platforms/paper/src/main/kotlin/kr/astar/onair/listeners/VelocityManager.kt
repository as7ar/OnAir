package kr.astar.onair.listeners

import kr.astar.onair.velocity.VeloChannel
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class VelocityManager: PluginMessageListener {
    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray
    ) {
        if (channel != VeloChannel.CHANNEL) return
        val input = DataInputStream(ByteArrayInputStream(message))

        try {
            val type = input.readUTF()
            when(type) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}