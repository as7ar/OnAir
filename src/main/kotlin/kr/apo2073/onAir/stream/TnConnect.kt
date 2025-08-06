package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.listeners.ToonationListener
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.tnliv.Toonation
import kr.apo2073.tnliv.ToonationBuilder
import kr.apo2073.tnliv.exception.TokenNotFound
import org.bukkit.entity.Player

class TnConnect {
    companion object {
        private val plugin= OnAir.plugin
        @JvmStatic
        fun connect(player: Player, id: String, channelId:String) {
            val userdata= UserData(player)
            val file=userdata.getFile()
            val config=userdata.getConfig()
            val cic= ConnectionManager.infoConfig

            if (!ConnectionManager.connectionCheck(player, id)) return

            try {
                val first=config.getBoolean("user.connection.chzzk.first", true)

                val builder= ToonationBuilder()
                    .setKey(id)

                if (first) {
                    OnAir.tn[player.uniqueId]= builder.addListener(ToonationListener()).build()
                } else {
                    OnAir.tn[player.uniqueId]= builder.build()
                }

                val streamer=Toonation.getStreamer(channelId)
                var channel=streamer.nickname
                if (streamer==null) channel=null
                if (channel==null) {
                    player.sendMessage(translate("command.oa.connection.tn.channelid"),true)
                    return
                }

                config.apply {
                    set("user.connection.toonation.first", false)
                    set("user.connection.toonation.isConnected", true)
                    set("user.connection.toonation.id", id)
                }.save(file)
                ConnectionManager.setConfigValue(id, player.uniqueId.toString())
                ConnectionManager.setConfigValue("${player.uniqueId}.toonation", id)
                userdata.addConnection(Platforms.TOONATION)

                player.sendMessage(translate(
                    "alert.connection.toonation", mapOf(
                        "name" to channel,)),
                    true
                )
            } catch (e: TokenNotFound) {
                player.sendMessage(translate("alert.not.exist.channel"), true)
                return
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

            OnAir.tn[player.uniqueId]?.close()
            OnAir.tn.remove(player.uniqueId)

            UserData(player).removeConnection(Platforms.TOONATION)
            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}