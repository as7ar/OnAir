package kr.apo2073.onair.data

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.PlayerStreamingConnectionEvent
import kr.apo2073.onair.events.PlayerStreamingDisconnectionEvent
import kr.apo2073.onair.stream.ChkConnect
import kr.apo2073.onair.stream.SpConnect
import kr.apo2073.onair.stream.TnConnect
import kr.apo2073.onair.stream.YtConnect
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object ConnectionManager {
    private val plugin= OnAir.plugin

    internal var infoFile= File(plugin.dataFolder, "connection")
    internal var infoConfig = YamlConfiguration.loadConfiguration(infoFile)

    fun setConfigValue(path:String, value: Any?) {
        infoFile= File(plugin.dataFolder, "connection")
        infoConfig= YamlConfiguration.loadConfiguration(infoFile)
        infoConfig.apply {
            set(path, value)
        }.save(infoFile)
    }

    fun connectionCheck(player: Player, id: String): Boolean {
        val cic=this.infoConfig
        if (cic.getString(id)!=null && cic.getString(id)!=player.uniqueId.toString()) {
            player.sendMessage(translate("alert.already.con"), true)
            return false
        }
        if (cic.getString(id)==player.uniqueId.toString()) {
            player.sendMessage(translate("alert.already.u"), true)
            return false
        }
        return true
    }

    fun isConnected(player: Player, platforms: Platforms): Boolean {
        val userData= UserData(player)
        val config=userData.getConfig()
        return when(platforms) {
            Platforms.CHZZK -> config.getBoolean("user.connection.chzzk.isConnected", false)
            Platforms.YOUTUBE -> config.getBoolean("user.connection.youtube.isConnected", false)
            Platforms.TOONATION -> config.getBoolean("user.connection.toonation.isConnected", false)
            else-> false
        }
    }

    class Manager(private val player:Player) {
        private val plugin=OnAir.plugin
        private val userData=UserData(player)

        fun connect(platforms: Platforms, channelName:String, id:String) {
            when(platforms) {
                Platforms.CHZZK -> {
                    userData.getConfig().apply {
                        set("user.connection.chzzk.display", channelName)
                    }.save(userData.getFile())
                    ChkConnect.connect(player, id)
                }
                Platforms.YOUTUBE -> {
                    userData.getConfig().apply {
                        set("user.connection.youtube.display", channelName)
                    }.save(userData.getFile())
                    YtConnect.connect(player, id)
                }
                Platforms.TOONATION -> {
                    userData.getConfig().apply {
                        set("user.connection.toonation.display", channelName)
                    }.save(userData.getFile())
                    TnConnect.connect(player, id, channelName)
                }
                Platforms.SOOP -> {
                    userData.getConfig().apply {
                        set("user.connection.toonation.display", channelName)
                    }.save(userData.getFile())
                    SpConnect.connect(player, id)
                }
                else-> return
            }
            val suc= PlayerStreamingConnectionEvent(player, platforms, id, channelName).callEvent()
            if (!suc) plugin.log.warning(translate(
                "system.boom",
                mapOf("err" to "calling event failure (PlayerStreamingConnectionEvent)")
            ))
        }

        fun disconnect(platforms: Platforms) {
            when(platforms) {
                Platforms.CHZZK -> ChkConnect.disconnect(player)
                Platforms.YOUTUBE -> YtConnect.disconnect(player)
                Platforms.TOONATION -> TnConnect.disconnect(player)
                Platforms.SOOP -> SpConnect.disconnect(player)
                else-> return
            }
            val suc= PlayerStreamingDisconnectionEvent(player, platforms).callEvent()
            if (!suc) plugin.log.warning(translate(
                "system.boom",
                mapOf("err" to "calling event failure (PlayerStreamingDisConnectionEvent)")
            ))
        }
    }
}