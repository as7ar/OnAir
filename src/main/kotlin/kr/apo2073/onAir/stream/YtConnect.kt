package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.listeners.YoutubeListener
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.utubeLiv.YouTubeBuilder
import kr.apo2073.utubeLiv.YouTubeInfo
import org.bukkit.entity.Player

class YtConnect {
    companion object {
        lateinit var youtubeInfo: YouTubeInfo
        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata = UserData(player)
                val file = userdata.getFile()
                val config = userdata.getConfig()

                if (!ConnectionManager.connectionCheck(player, id)) return

                val first = config.getBoolean("user.connection.youtube.first", true)
                Debugger.debug("First connect? ${config.getBoolean("user.connection.youtube.first")}")

                val builder = YouTubeBuilder()
                    .setApiKey(OnAir.plugin.config.getString("youtube.key"))
                    .setVideoId(id)

                if (first) builder.addListener(YoutubeListener())

                Debugger.debug("Building YouTube instance for videoId=$id")
                val ytInstance = builder.build() ?: run {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }
                Debugger.debug("YouTube instance built: $ytInstance")

                val channelInfo = ytInstance.channelInfo()
                if (channelInfo == null) {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    Debugger.debug("channelInfo is null for videoId=$id")
                    return
                }

                OnAir.yt[player.uniqueId] = ytInstance
                youtubeInfo = channelInfo
                Debugger.debug("youtubeInfo loaded: ${youtubeInfo?.channelName}")

                config.apply {
                    set("user.connection.youtube.first", false)
                    set("user.connection.youtube.isConnected", true)
                    set("user.connection.youtube.id", id)
                }.save(file)
                Debugger.debug("Config after save: ${config.getBoolean("user.connection.youtube.isConnected")}")

                ConnectionManager.setConfigValue(id, player.uniqueId.toString())
                ConnectionManager.setConfigValue("${player.uniqueId}.youtube", id)
                userdata.addConnection(Platforms.YOUTUBE)

                player.sendMessage(translate(
                    "alert.connection.youtube", mapOf(
                        "name" to channelInfo.channelName,
                        "sub" to channelInfo.subscriptionCount
                    )), true)

            } catch (e: Exception) {
                player.sendMessage(translate(
                    "command.got.problems",
                    mapOf("err" to (e.message ?: "0"))
                ), true)
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun disconnect(player: Player) {
            val id= ConnectionManager.infoConfig.getString("${player.uniqueId}.youtube") ?: return
            Debugger.debug("Disconnecting player=${player.name}, videoId=$id")

            ConnectionManager.setConfigValue("${player.uniqueId}.youtube", null)
            ConnectionManager.setConfigValue(id, null)

            OnAir.yt[player.uniqueId]?.stop()
            OnAir.yt.remove(player.uniqueId)
            Debugger.debug("YouTube instance stopped and removed")

            UserData(player).removeConnection(Platforms.YOUTUBE)
            Debugger.debug("Connection removed from UserData")
            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}