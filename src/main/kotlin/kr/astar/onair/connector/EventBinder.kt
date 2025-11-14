package kr.astar.onair.connector

import kr.astar.api.chzzk4j.chat.ChzzkChat
import kr.astar.api.chzzk4j.chat.event.ChatMessageEvent
import kr.astar.api.chzzk4j.chat.event.MissionDonationEvent
import kr.astar.api.chzzk4j.chat.event.NormalDonationEvent
import kr.astar.onair.events.ChzzkChatEvent
import kr.astar.onair.events.ChzzkDonationEvent
import kr.astar.onair.events.ChzzkMissionDonationEvent
import kr.astar.onair.utils.Utils.runTask
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