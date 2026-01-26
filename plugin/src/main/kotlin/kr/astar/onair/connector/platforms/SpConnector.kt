package kr.astar.onair.connector.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.api.soopliv.SoopBuilder
import kr.astar.onair.api.soopliv.soop.SoopApi
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.platforms.SoopListener
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
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