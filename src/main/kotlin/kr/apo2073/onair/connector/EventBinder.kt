package kr.apo2073.onair.connector

import kr.apo2073.onair.events.ChzzkChatEvent
import kr.apo2073.onair.events.ChzzkDonationEvent
import kr.apo2073.onair.events.ChzzkMissionDonationEvent
import kr.apo2073.onair.utils.Utils.runTask
import org.bukkit.entity.Player
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent
import xyz.r2turntrue.chzzk4j.chat.event.MissionDonationEvent
import xyz.r2turntrue.chzzk4j.chat.event.NormalDonationEvent

object EventBinder {
    fun bindChzzkEvents(cht: ChzzkChat, player: Player): ChzzkChat {
        cht.on(ChatMessageEvent::class.java) { runTask {
            ChzzkChatEvent(it.message, it.chat, player).callEvent()
        } }

        cht.on(NormalDonationEvent::class.java) { runTask {
            ChzzkDonationEvent(it.message, it.chat, player).callEvent()
        } }

        cht.on(MissionDonationEvent::class.java) { runTask {
            ChzzkMissionDonationEvent(it.message, it.chat, player).callEvent()
        } }
        return cht
    }
}