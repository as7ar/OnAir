package kr.astar.onair.data

import kr.astar.onair.OnAir
import kr.astar.onair.connector.platforms.*
import kr.astar.onair.enums.Platforms
import kr.astar.onair.events.PlayerStreamingConnectionEvent
import kr.astar.onair.events.PlayerStreamingDisconnectionEvent
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object ConnectionManager {
    private val plugin= OnAir.plugin

    internal var infoFile= File(plugin.dataFolder, "connection")
    var infoConfig = YamlConfiguration.loadConfiguration(infoFile)

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
        return config.getBoolean("user.connection.${
            platforms.name.lowercase()
        }.isConnected", false)
    }

    class Manager(private val player:Player) {
        private val plugin=OnAir.plugin
        private val userData=UserData(player)

        fun connect(platforms: Platforms, channelName:String, id:String) {
            userData.getConfig().apply {
                set("user.connection.${platforms.name.lowercase()}.display", channelName)
            }.save(userData.getFile())
            when(platforms) {
                Platforms.CHZZK -> {
                    val liveStatus= OnAir.chzzkClient.fetchLiveStatus(id).get()
                    val tags=liveStatus.tags
                    val title=liveStatus.title
                    if (ConfigSet.chzzk.requiredTag.enabled && !(tags.contains(ConfigSet.chzzk.requiredTag.keyword))) {
                        player.sendMessage(translate(
                            "alert.required.keyword.not.use.tags",
                            mapOf("current" to tags.joinToString(", "))
                        ), true)
                        return
                    }
                    if (ConfigSet.chzzk.requiredTitle.enabled && !(title.contains(ConfigSet.chzzk.requiredTitle.keyword))) {
                        player.sendMessage(translate(
                            "alert.required.keyword.not.use.title",
                            mapOf("current" to title)

                        ), true)
                        return
                    }
                    ChkConnector().connect(player, id)
                }
                Platforms.YOUTUBE -> YtConnector().connect(player, id)
                Platforms.TOONATION -> TnConnector(channelName).connect(player, id)
                Platforms.SOOP -> SpConnector().connect(player, id)
                Platforms.TWITCH -> TwConnector().connect(player, id)
                Platforms.WEFLAB -> WfConnector().connect(player, id)
                else-> return
            }
            val suc= PlayerStreamingConnectionEvent(
                player, platforms, id, channelName
            ).callEvent()
            if (!suc) plugin.log.warning(translate(
                "system.boom",
                mapOf("err" to "calling event failure (PlayerStreamingConnectionEvent)")
            ))
        }

        fun disconnect(platforms: Platforms) {
            when(platforms) {
                Platforms.CHZZK -> ChkConnector().disconnect(player)
                Platforms.YOUTUBE -> YtConnector().disconnect(player)
                Platforms.TOONATION -> TnConnector("").disconnect(player)
                Platforms.SOOP -> SpConnector().disconnect(player)
                Platforms.TWITCH -> TwConnector().disconnect(player)
                Platforms.WEFLAB -> WfConnector().disconnect(player)
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