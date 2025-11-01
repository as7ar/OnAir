package kr.apo2073.onair.connector

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.utils.Debugger
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.onair.utils.Utils.sendMessage
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

abstract class AbstractConnector(
    protected val platform: Platforms
) {
    protected val plugin = OnAir.plugin

    /**
    * @param player the player to connect
     * @param id the id of channel or video
    * */
    abstract fun connect(player: Player, id: String)

    /**
     * @param player the player to disconnect
     * */
    abstract fun disconnect(player: Player)

    protected fun withUserData(player: Player, action: (UserData, FileConfiguration) -> Unit) {
        val user = UserData(player)
        val config = user.getConfig()
        action(user, config)
    }

    protected fun safeRun(player: Player, action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            player.sendMessage(translate("command.got.problems", mapOf(
                "err" to (e.message ?: "0")
            )), true)
            e.printStackTrace()
        }
    }

    protected fun disconnect(player: Player, platforms: Platforms) = safeRun(player) {
        val id = ConnectionManager.infoConfig
            .getString("${player.uniqueId}.${platforms.name.lowercase()}") ?: run {
            Debugger.debug("No Connection Info for ${player.uniqueId}: ${player.uniqueId}.${platforms.name.lowercase()} ")
            player.sendMessage(translate("alert.not.connected"), true)
            return@safeRun
        }

        when (platforms) {
            Platforms.TOONATION -> {
                OnAir.tn[player.uniqueId]?.close()

                OnAir.tn.remove(player.uniqueId)
            }
            Platforms.YOUTUBE -> {
                OnAir.yt[player.uniqueId]?.close()

                OnAir.yt.remove(player.uniqueId)
            }
            Platforms.SOOP -> {
                OnAir.sp[player.uniqueId]?.close()
                OnAir.sp[player.uniqueId]?.closeBlocking()
                OnAir.sp[player.uniqueId]?.kill()

                OnAir.sp.remove(player.uniqueId)
            }
            Platforms.CHZZK -> {
                OnAir.cht[player.uniqueId]?.closeBlocking()

                OnAir.cht.remove(player.uniqueId)
            }
            Platforms.TWITCH -> {
                OnAir.tw[player.uniqueId]?.shutdown()
                OnAir.tw.remove(player.uniqueId)
            }
            Platforms.UNKNOWN -> {
                player.sendMessage(translate("alert.not.connected"), true)
            }
        }

        UserData(player).disconnect(platforms, id)
        player.sendMessage(translate("alert.disconnect"), true)
    }
}