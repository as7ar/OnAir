package kr.astar.onair.velocity

import kr.astar.onair.OnAir
import kr.astar.onair.velocity.VeloChannel.CHANNEL
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object VelocityManager {
    fun Player.sendVeloMessage(vararg messages: String) {
        val out = ByteArrayOutputStream().let { baos ->
            DataOutputStream(baos).apply {
                messages.forEach { writeUTF(it) }
            }
            baos.toByteArray()
        }

        player?.sendPluginMessage(OnAir.plugin, CHANNEL, out)
    }
}