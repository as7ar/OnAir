package kr.astar.onair.connector

import kr.astar.onair.events.ChzzkChatEvent
import kr.astar.onair.events.ChzzkDonationEvent
import kr.astar.onair.events.ChzzkMissionDonationEvent
import kr.astar.onair.utils.Utils.runTask
import org.bukkit.entity.Player
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent
import xyz.r2turntrue.chzzk4j.chat.event.MissionDonationEvent
import xyz.r2turntrue.chzzk4j.chat.event.NormalDonationEvent

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