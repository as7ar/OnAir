package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.listeners.YoutubeListener
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.ytliv.YouTubeBuilder
import kr.apo2073.ytliv.YouTubeInfo
import org.bukkit.entity.Player

class YtConnect {
    private val plugin= OnAir.plugin
    companion object {
        lateinit var youtubeInfo: YouTubeInfo
        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata= UserData(player)
                val file=userdata.getFile()
                val config=userdata.getConfig()
                val cic= ConnectionInfo.config
                if (cic.getString(id)==player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.u"), true)
                    return
                }
                if (cic.getString(id)!=null || cic.getString(id)!=player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.con"), true)
                    return
                }

                val first=config.getBoolean("user.connection.chzzk.first", true)

                val tempYt= YouTubeBuilder()
                    .setApiKey("AIzaSyBpMcjduOo5VbaWa-ptNGuGsG323gaop60")
                    .setVideoId(id).build()
                val channelInfo=tempYt?.channelInfo()
                if (channelInfo==null) {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }

                if (first) {
                    OnAir.yt[player.uniqueId]=YouTubeBuilder()
                        .setApiKey("AIzaSyBpMcjduOo5VbaWa-ptNGuGsG323gaop60")
                        .addListener(YoutubeListener())
                        .setVideoId(id).build()
                } else {
                    OnAir.yt[player.uniqueId]=tempYt
                }
                youtubeInfo= OnAir.yt[player.uniqueId]?.channelInfo() ?: run {
                    player.sendMessage(translate("alert.not.exist.channel"), true)
                    return
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                val userdata= UserData(player)
                val file=userdata.getFile()
                val config=userdata.getConfig()

                config.apply {
                    set("user.connection.youtube.first", false)
                    set("user.connection.youtube.isConnected", false)
                    set("user.connection.youtube.id", id)
                }.save(file)
                ConnectionInfo.setValue(id, player.uniqueId.toString())

                player.sendMessage(
                    translate("alert.connection.chzzk")
                        .replace("{name}", youtubeInfo.channelName)
                        .replace("{fol}", youtubeInfo.subscriptionCount),
                    true
                )
            }
        }
    }
}