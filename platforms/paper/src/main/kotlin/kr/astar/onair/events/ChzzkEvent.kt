package kr.astar.onair.events

import xyz.r2turntrue.chzzk4j.chat.ChatMessage
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.chat.DonationMessage
import xyz.r2turntrue.chzzk4j.chat.MissionDonationMessage
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class ChzzkChatEvent(val message: ChatMessage, val chat: ChzzkChat, val player: Player?): Event() {
    override fun getEventName() = "ChzzkChatEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}

data class ChzzkDonationEvent(val message: DonationMessage, val chat: ChzzkChat, val player: Player?) : Event() {
    override fun getEventName() = "ChzzkDonationEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}

data class ChzzkMissionDonationEvent(val message: MissionDonationMessage, val chat: ChzzkChat, val player: Player?) : Event() {
    override fun getEventName() = "ChzzkMissionDonationEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}