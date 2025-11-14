package kr.astar.onair.connector.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.platforms.YoutubeListener
import kr.astar.onair.utils.Utils.translate
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.api.utubeLiv.YouTubeBuilder
import kr.astar.api.utubeLiv.YouTubeInfo
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

    override fun disconnect(player: Player) = disconnect(player, platform)
}