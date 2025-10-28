package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.enums.Platforms
import org.bukkit.entity.Player

class TwConnector: AbstractConnector(Platforms.TWITCH) {
    override fun connect(player: Player, id: String) = safeRun(player) {
        withUserData(player) { user, config->

        }
    }

    override fun disconnect(player: Player) = safeRun(player) {

    }
}