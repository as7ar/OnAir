package kr.astar.onair.connector.platforms

import kr.astar.api.weflabLiv.WeflabBuilder
import kr.astar.onair.OnAir
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.UserData
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.platforms.WeflabListener
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class WfConnector: AbstractConnector(Platforms.WEFLAB) {

    override fun connect(player: Player, id: String) = safeRun(player) {
        withUserData(player) { user, config ->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first = config.getBoolean("user.connection.weflab.first", true)

            val builder = WeflabBuilder(id)
            val ws = if (first) builder.addListener(WeflabListener()).build() else builder.build()

            OnAir.wf[player.uniqueId] = ws
            user.connect(platform, ws.idx)

            player.sendMessage(translate("alert.connection.weflab", mapOf(
                "name" to ws.streamerData.id,
                "fol" to ""
            )), true)
    } }

    override fun disconnect(player: Player) = disconnect(player, platform)

}