package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.platforms.SoopListener
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Debugger
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.soopliv.SoopBuilder
import kr.apo2073.soopliv.soop.SoopApi
import org.bukkit.entity.Player

class SpConnector: AbstractConnector(Platforms.SOOP) {
    override fun connect(player: Player, id: String) = safeRun(player) {
        withUserData(player) { user, config->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first = config.getBoolean("user.connection.soop.first", true)

            val liveInfo = SoopApi.getPlayerLive(id, ConfigSet.debug) ?: run {
                player.sendMessage(translate("alert.not.exist.channel"), true)
                return@withUserData
            }

            val builder= SoopBuilder()
                .setID(id).enableBalloon(ConfigSet.balloon)
                .setUser(mapOf("nickname" to id, "tag" to player.name))

            val ws= if (first) {
                builder.setListener(SoopListener()).build(ConfigSet.debug)
            } else { builder.build(ConfigSet.debug) }

            ws.connect()
            OnAir.sp[player.uniqueId] = ws

            user.connect(platform, id)

            player.sendMessage(translate("alert.connection.soop", mapOf(
                "name" to liveInfo.BJNICK,
                "fol" to ""
            )), true)
        }
    }

    override fun disconnect(player: Player) = disconnect(player, platform)
}