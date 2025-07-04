package kr.apo2073.onAir.stream

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.events.ChzzkChatEvent
import kr.apo2073.onAir.events.ChzzkDonationEvent
import kr.apo2073.onAir.events.ChzzkMissionDonationEvent
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.entity.Player
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent
import xyz.r2turntrue.chzzk4j.chat.event.MissionDonationEvent
import xyz.r2turntrue.chzzk4j.chat.event.NormalDonationEvent
import xyz.r2turntrue.chzzk4j.exception.ChannelNotExistsException

class ChkConnect {
    private val plugin= OnAir.plugin
    companion object {
        @JvmStatic
        fun connect(player: Player, id: String) {
            try {
                val userdata= UserData(player)
                val file=userdata.getFile()
                val config=userdata.getConfig()
                val cic= ConnectionInfo.config
                if (cic.getString(id)!=null || cic.getString(id)!=player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.con"), true)
                    return
                }
                if (cic.getString(id)==player.uniqueId.toString()) {
                    player.sendMessage(translate("alert.already.u"), true)
                    return
                }

                val first=config.getBoolean("user.connection.chzzk.first", true)

                if (OnAir.cht[player.uniqueId]!=null) {
                    OnAir.cht[player.uniqueId]?.closeBlocking()
                    OnAir.cht[player.uniqueId]?.closeAsync()
                }
                OnAir.cht[player.uniqueId]= ChzzkChatBuilder(
                    OnAir.chzzkClient, id
                ).build()

                OnAir.cht[player.uniqueId]?.connectBlocking()
                if (first) {
                    OnAir.cht[player.uniqueId]?.on(ChatMessageEvent::class.java) { evt->
                        ChkConnect().plugin.server.scheduler.runTask(ChkConnect().plugin, Runnable {
                            val isSuc= ChzzkChatEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 채팅 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                    OnAir.cht[player.uniqueId]?.on(NormalDonationEvent::class.java) { evt->
                        ChkConnect().plugin.server.scheduler.runTask(ChkConnect().plugin, Runnable {
                            val isSuc= ChzzkDonationEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 후원 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                    OnAir.cht[player.uniqueId]?.on(MissionDonationEvent::class.java) { evt ->
                        ChkConnect().plugin.server.scheduler.runTask(ChkConnect().plugin, Runnable {
                            val isSuc= ChzzkMissionDonationEvent(
                                evt.message, evt.chat,
                                player
                            ).callEvent()
                            if (!isSuc) throw Exception("치지직 미션 후원 이벤트를 처리하던 중 오류가 발생했습니다.")
                        })
                    }
                }

            } catch (e: ChannelNotExistsException) {
                player.sendMessage(translate("alert.not.exist.channel"), true)
            }catch (e: Exception) {
                player.sendMessage(translate("command.got.problems")
                    .replace("{err}", e.message ?: "0"), true)
                e.printStackTrace()
            } finally {
                val userdata= UserData(player)
                val file=userdata.getFile()
                val config=userdata.getConfig()
                val CHANNEL_NAME= OnAir.chzzkClient.fetchChannel(id).channelName
                val CHANNEL_FOL= OnAir.chzzkClient.fetchChannel(id).followerCount.toString()

                ConnectionInfo.setValue(id, player.uniqueId.toString())

                config.apply {
                    set("user.connection.chzzk.first", false)
                    set("user.connection.chzzk.isConnected", false)
                    set("user.connection.chzzk.id", id)
                }.save(file)

                player.sendMessage(
                    translate("alert.connection.chzzk")
                        .replace("{name}", CHANNEL_NAME)
                        .replace("{fol}", CHANNEL_FOL),
                    true
                )
            }
        }
    }
}