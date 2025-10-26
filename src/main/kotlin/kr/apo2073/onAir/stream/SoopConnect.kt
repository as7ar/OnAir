package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.listeners.SoopListener
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.soopliv.soop.SoopApi
import kr.apo2073.soopliv.soop.SoopLiveInfo
import kr.apo2073.soopliv.soop.SoopWebSocket
import org.bukkit.entity.Player
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.protocols.Protocol
import java.util.*

class SoopConnect {
    companion object {
        private val plugin = OnAir.plugin

        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata = UserData(player)
                val config = userdata.getConfig()
                val file = userdata.getFile()

                if (!ConnectionManager.connectionCheck(player, id)) return

                val first = config.getBoolean("user.connection.soop.first", true)

                val liveInfo = SoopApi.getPlayerLive(id) ?: run {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }

                val ws = SoopWebSocket(
                    "wss://${liveInfo.CHDOMAIN()}:${liveInfo.CHPT()}/Websocket/${liveInfo.BJID()}",
                    Draft_6455(
                        emptyList(),
                        listOf(Protocol("chat"))
                    ),
                    liveInfo,
                    mapOf("nickname" to id, "tag" to player.name),
                    OnAir.plugin.config.getBoolean("soop.poong", false),
                    false,
                    SoopListener()
                )

                ws.connect()
                OnAir.sp[player.uniqueId] = ws

                config.apply {
                    set("user.connection.soop.first", false)
                    set("user.connection.soop.isConnected", true)
                    set("user.connection.soop.id", id)
                }.save(file)

                ConnectionManager.setConfigValue(id, player.uniqueId.toString())
                ConnectionManager.setConfigValue("${player.uniqueId}.soop", id)
                userdata.addConnection(Platforms.SOOP)

                player.sendMessage(
                    translate("alert.connection.soop", mapOf("name" to liveInfo.BJID)),
                    true
                )

            } catch (e: Exception) {
                player.sendMessage(
                    translate("command.got.problems", mapOf("err" to (e.message ?: "0"))),
                    true
                )
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun disconnect(player: Player) {
            val id = ConnectionManager.infoConfig.getString("${player.uniqueId}.soop") ?: return

            ConnectionManager.setConfigValue("${player.uniqueId}.soop", null)
            ConnectionManager.setConfigValue(id, null)

            OnAir.sp[player.uniqueId]?.close()
            OnAir.sp.remove(player.uniqueId)

            val userdata = UserData(player)
            userdata.removeConnection(Platforms.SOOP)
            userdata.getConfig().apply {
                set("user.connection.soop.first", null)
                set("user.connection.soop.isConnected", null)
                set("user.connection.soop.id", null)
            }.save(userdata.getFile())

            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}
