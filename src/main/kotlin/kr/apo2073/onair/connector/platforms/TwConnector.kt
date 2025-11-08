package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.platforms.TwitchListener
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.api.twitchLiv.TwitchBuilder
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