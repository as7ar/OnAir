package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.listeners.YoutubeListener
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.ytliv.YouTubeBuilder
import kr.apo2073.ytliv.YouTubeInfo
import org.bukkit.entity.Player

class YtConnect {
    companion object {
        lateinit var youtubeInfo: YouTubeInfo
        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata= UserData(player)
                val file=userdata.getFile()
                val config=userdata.getConfig()
                val cic= ConnectionManager.infoConfig

                if (!ConnectionManager.connectionCheck(player, id)) return

                val first=config.getBoolean("user.connection.chzzk.first", true)

                val builder = YouTubeBuilder()
                    .setApiKey("AIzaSyBpMcjduOo5VbaWa-ptNGuGsG323gaop60")
                    .setVideoId(id)

                val channelInfo = builder.build()?.channelInfo()
                if (channelInfo == null) {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }

                if (first) {
                    builder.addListener(YoutubeListener())
                }

                val ytInstance = builder.build()
                OnAir.yt[player.uniqueId] = ytInstance

                youtubeInfo= OnAir.yt[player.uniqueId]?.channelInfo() ?: run {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }

                config.apply {
                    set("user.connection.youtube.first", false)
                    set("user.connection.youtube.isConnected", true)
                    set("user.connection.youtube.id", id)
                }.save(file)
                ConnectionManager.setConfigValue(id, player.uniqueId.toString())
                ConnectionManager.setConfigValue("${player.uniqueId}.youtube", id)
                userdata.addConnection(Platforms.YOUTUBE)

                if (::youtubeInfo.isInitialized) {
                    player.sendMessage(translate(
                        "alert.connection.youtube", mapOf(
                            "name" to youtubeInfo.channelName,
                            "sub" to youtubeInfo.subscriptionCount
                    )), true)
                }
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

            ConnectionManager.setConfigValue("${player.uniqueId}.youtube", null)
            ConnectionManager.setConfigValue(id, null)

            OnAir.yt[player.uniqueId]?.stop()
            OnAir.yt.remove(player.uniqueId)

            UserData(player).removeConnection(Platforms.YOUTUBE)
            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}