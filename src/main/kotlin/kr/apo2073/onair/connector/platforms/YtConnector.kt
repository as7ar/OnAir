package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.platforms.YoutubeListener
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.utubeLiv.YouTubeBuilder
import kr.apo2073.utubeLiv.YouTubeInfo
import org.bukkit.entity.Player

class YtConnector: AbstractConnector(Platforms.YOUTUBE) {
    private lateinit var youtubeInfo: YouTubeInfo

    override fun connect(player: Player, id: String) = safeRun(player) {
        withUserData(player) { user, config->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first = config.getBoolean("user.connection.youtube.first", true)

            val builder = YouTubeBuilder()
                .setApiKey(plugin.config.getString("youtube.key"))
                .setVideoId(id)
            if (first) builder.addListener(YoutubeListener())

            val ytInstance = builder.build() ?: run {
                player.sendMessage(translate("alert.not.exist.channel"), true)
                return@withUserData
            }

            val channelInfo = ytInstance.channelInfo()
            if (channelInfo == null) {
                player.sendMessage(translate("alert.not.exist.channel"), true)
                return@withUserData
            }

            OnAir.yt[player.uniqueId] = ytInstance
            youtubeInfo = channelInfo

            user.connect(platform, id)

            player.sendMessage(translate("alert.connection.youtube", mapOf(
                "name" to channelInfo.channelName,
                "sub" to channelInfo.subscriptionCount
            )), true)
        }
    }

    override fun disconnect(player: Player) = safeRun(player) {
        val id = ConnectionManager.infoConfig
            .getString("${player.uniqueId}.youtube") ?: return@safeRun

        OnAir.yt[player.uniqueId]?.close()
        OnAir.yt.remove(player.uniqueId)

        UserData(player).disconnect(platform, id)
        player.sendMessage(translate("alert.disconnect"), true)
    }
}