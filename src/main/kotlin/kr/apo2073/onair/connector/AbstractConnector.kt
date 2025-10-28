package kr.apo2073.onair.connector

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
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
}