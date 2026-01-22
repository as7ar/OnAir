package kr.astar.onair.connector.platforms

import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.enums.Platforms
import org.bukkit.entity.Player

class WfConnector: AbstractConnector(Platforms.WEFLAB) {
    override fun connect(player: Player, id: String) {
        TODO("Not yet implemented")
    }

    override fun disconnect(player: Player) = disconnect(player, platform)

}