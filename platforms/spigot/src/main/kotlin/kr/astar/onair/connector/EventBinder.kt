package kr.astar.onair.connector

import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent
import xyz.r2turntrue.chzzk4j.chat.event.MissionDonationEvent
import xyz.r2turntrue.chzzk4j.chat.event.NormalDonationEvent
import kr.astar.onair.events.ChzzkChatEvent
import kr.astar.onair.events.ChzzkDonationEvent
import kr.astar.onair.events.ChzzkMissionDonationEvent
import kr.astar.onair.utils.Utils.runTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object EventBinder {
    fun bindChzzkEvents(cht: ChzzkChat, player: Player): ChzzkChat {
        cht.on(ChatMessageEvent::class.java) { runTask {
            Bukkit.getPluginManager().callEvent(ChzzkChatEvent(it.message, cht, player))
        } }

        cht.on(NormalDonationEvent::class.java) { runTask {
            Bukkit.getPluginManager().callEvent(ChzzkDonationEvent(it.message, cht, player))
        } }

        cht.on(MissionDonationEvent::class.java) { runTask {
            Bukkit.getPluginManager().callEvent(ChzzkMissionDonationEvent(it.message, cht, player))
        } }
        return cht
    }
}