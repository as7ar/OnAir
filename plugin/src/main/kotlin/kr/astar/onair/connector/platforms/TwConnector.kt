package kr.astar.onair.connector.platforms

import kr.astar.onair.api.twitchLiv.TwitchBuilder
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.platforms.TwitchListener
import kr.astar.onair.OnAir
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
import org.bukkit.entity.Player

class TwConnector: AbstractConnector(Platforms.TWITCH) {
    override fun connect(player: Player, id: String) = safeRun(player) {
        withUserData(player) { user, config->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first = config.getBoolean("user.connection.chzzk.first", true)

            OnAir.tw[player.uniqueId]?.shutdown()

            val builder= TwitchBuilder()
            builder.id= id
            builder.TOKEN= ConfigSet.twitch.TOKEN
            builder.clientId= ConfigSet.twitch.CLIENT_ID
            builder.clientSecret= ConfigSet.twitch.CLIENT_SECRET
            if (first) builder.addListener(TwitchListener())

            val twitch= builder.build()

            OnAir.tw[player.uniqueId]= twitch

            val name= twitch.fetchChannelName()
            val fol= twitch.fetchChannelFollowers()

            user.connect(platform, id)
            player.sendMessage(translate("alert.connection.chzzk", mapOf(
                "name" to name,
                "fol" to fol.toString()
            )), true)
        }
    }

    override fun disconnect(player: Player) = disconnect(player, platform)
}