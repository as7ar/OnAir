package kr.apo2073.onair.connector

import kr.apo2073.api.chzzk4j.chat.ChzzkChat
import kr.apo2073.api.chzzk4j.chat.event.ChatMessageEvent
import kr.apo2073.api.chzzk4j.chat.event.MissionDonationEvent
import kr.apo2073.api.chzzk4j.chat.event.NormalDonationEvent
import kr.apo2073.onair.events.ChzzkChatEvent
import kr.apo2073.onair.events.ChzzkDonationEvent
import kr.apo2073.onair.events.ChzzkMissionDonationEvent
import kr.apo2073.onair.utils.Utils.runTask
import org.bukkit.entity.Player

object EventBinder {
    fun bindChzzkEvents(cht: ChzzkChat, player: Player): ChzzkChat {
        cht.on(ChatMessageEvent::class.java) { runTask {
            ChzzkChatEvent(it.message, cht, player).callEvent()
        } }

        cht.on(NormalDonationEvent::class.java) { runTask {
            ChzzkDonationEvent(it.message, cht, player).callEvent()
        } }

        cht.on(MissionDonationEvent::class.java) { runTask {
            ChzzkMissionDonationEvent(it.message, cht, player).callEvent()
        } }
        return cht
    }
}