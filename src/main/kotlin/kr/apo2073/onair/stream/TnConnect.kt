package kr.apo2073.onair.stream

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.platforms.ToonationListener
import kr.apo2073.onair.utils.Temp
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.toonLiv.Toonation
import kr.apo2073.toonLiv.ToonationBuilder
import kr.apo2073.toonLiv.exception.TokenNotFound
import org.bukkit.Bukkit
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
//                    .setDebug(true)
                    .setKey(id)

                if (first) {
                    OnAir.tn[player.uniqueId]= builder.addListener(ToonationListener()).build()
                } else {
                    OnAir.tn[player.uniqueId]= builder.build()
                }
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    try {
                        var channel: String?

                        if (Temp.getTempAsString(channelId)==null) {
                            val streamer = Toonation.getStreamerAsync(channelId).get()
                            channel = streamer.nickname
                            if (streamer == null) channel = null
                            else Temp.addTemp(channelId  , channel)
                        } else {
                            channel= Temp.getTempAsString(channelId)
                        }

                        Bukkit.getScheduler().runTask(plugin, Runnable {
                            if (channel==null) {
                                player.sendMessage(translate("command.oa.connection.tn.channelid"),true)
                                return@Runnable
                            }

                            config.apply {
                                set("user.connection.toonation.display", channel)
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
                        })
                    } catch (e: TokenNotFound) {
                        player.sendMessage(translate("alert.not.exist.channel"), true)
                        return@Runnable
                    } catch (e: Exception) {
                        player.sendMessage(translate(
                            "command.got.problems",
                            mapOf("err" to (e.message ?: "0"))
                        ), true)
                        e.printStackTrace()
                    }
                })
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
            val id= ConnectionManager.infoConfig.getString("${player.uniqueId}.toonation") ?: return

            ConnectionManager.setConfigValue("${player.uniqueId}.toonation", null)
            ConnectionManager.setConfigValue(id, null)

            OnAir.tn[player.uniqueId]?.close()
            OnAir.tn.remove(player.uniqueId)

            UserData(player).removeConnection(Platforms.TOONATION)
            UserData(player).getConfig().apply {
                set("user.connection.toonation.display", null)
                set("user.connection.toonation.first", null)
                set("user.connection.toonation.isConnected", null)
                set("user.connection.toonation.id", null)
            }.save(UserData(player).getFile())
            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}