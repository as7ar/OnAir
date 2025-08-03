package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.ChzzkChatEvent
import kr.apo2073.onAir.events.ChzzkDonationEvent
import kr.apo2073.onAir.events.ChzzkMissionDonationEvent
import kr.apo2073.onAir.events.PlayerStreamingConnectionEvent
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.entity.Player
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent
import xyz.r2turntrue.chzzk4j.chat.event.MissionDonationEvent
import xyz.r2turntrue.chzzk4j.chat.event.NormalDonationEvent
import xyz.r2turntrue.chzzk4j.exception.ChannelNotExistsException

class ChkConnect {
    companion object {
        private val plugin= OnAir.plugin

        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata= UserData(player)
                val config=userdata.getConfig()
                val file=userdata.getFile()
                val cic= ConnectionInfo.config
                val cht=OnAir.cht[player.uniqueId]

                if (cic.getString(id)!=null && cic.getString(id)!=player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.con"), true)
                    return
                }
                if (cic.getString(id)==player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.u"), true)
                    return
                }

                val first=config.getBoolean("user.connection.chzzk.first", true)

                if (cht!=null) {
                    cht.closeBlocking()
                    cht.closeAsync()
                }
                OnAir.cht[player.uniqueId]= ChzzkChatBuilder(
                    OnAir.chzzkClient, id
                ).build()

                OnAir.cht[player.uniqueId]?.connectBlocking()
                if (first) {
                    OnAir.cht[player.uniqueId]?.on(ChatMessageEvent::class.java) { evt->
                        plugin.server.scheduler.runTask(plugin, Runnable {
                            val isSuc= ChzzkChatEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 채팅 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                    OnAir.cht[player.uniqueId]?.on(NormalDonationEvent::class.java) { evt->
                        plugin.server.scheduler.runTask(plugin, Runnable {
                            val isSuc= ChzzkDonationEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 후원 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                    OnAir.cht[player.uniqueId]?.on(MissionDonationEvent::class.java) { evt ->
                        plugin.server.scheduler.runTask(plugin, Runnable {
                            val isSuc= ChzzkMissionDonationEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 미션 후원 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                }

                val fetchChannel=OnAir.chzzkClient.fetchChannel(id)
                val CHANNEL_NAME= fetchChannel.channelName
                val CHANNEL_FOL= fetchChannel.followerCount.toString()

                ConnectionInfo.setValue(id, player.uniqueId.toString())
                ConnectionInfo.setValue("${player.uniqueId}.chzzk", id)
                userdata.addConnection(Platforms.CHZZK)

                config.apply {
                    set("user.connection.chzzk.first", false)
                    set("user.connection.chzzk.isConnected", true)
                    set("user.connection.chzzk.id", id)
                }.save(file)

                player.sendMessage(
                    translate("alert.connection.chzzk", mapOf(
                        "name" to CHANNEL_NAME,
                        "fol" to CHANNEL_FOL
                    )),
                    true
                )
            } catch (e: ChannelNotExistsException) {
                player.sendMessage(translate(
                    "alert.not.exist.channel",
                    mapOf("err" to (e.message ?: "0"))
                ), true)
            }catch (e: Exception) {
                player.sendMessage(translate("command.got.problems",
                    mapOf("err" to (e.message ?: "0"))
                ), true)
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun disconnect(player: Player) {
            val id= ConnectionInfo.config.getString("${player.uniqueId}.chzzk") ?: return

            ConnectionInfo.setValue(id, null)
            ConnectionInfo.setValue("${player.uniqueId}.chzzk", null)

            OnAir.cht[player.uniqueId]?.closeBlocking()
            OnAir.cht[player.uniqueId]?.closeAsync()
            OnAir.cht.remove(player.uniqueId)

            UserData(player).removeConnection(Platforms.CHZZK)
            UserData(player).getConfig().apply {
                set("user.connection.chzzk.first", null)
                set("user.connection.chzzk.isConnected", null)
                set("user.connection.chzzk.id", null)
            }.save(UserData(player).getFile())
            player.sendMessage(translate("alert.disconnect"), true)
        }
    }
}