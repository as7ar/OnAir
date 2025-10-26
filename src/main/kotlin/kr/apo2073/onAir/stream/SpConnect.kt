package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.listeners.SoopListener
import kr.apo2073.onAir.utils.ConfigSet
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.soopliv.SoopBuilder
import kr.apo2073.soopliv.soop.SoopApi
import org.bukkit.entity.Player

class SpConnect {
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

                val builder=SoopBuilder()
                    .setID(id).enableBalloon(ConfigSet.balloon)
                    .setUser(mapOf("nickname" to id, "tag" to player.name))

                val ws= if (first) {
                    builder.setListener(SoopListener()).build(ConfigSet.debug)
                } else { builder.build(ConfigSet.debug) }

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
