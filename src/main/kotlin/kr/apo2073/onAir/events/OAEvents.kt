package kr.apo2073.onAir.events

import kr.apo2073.onAir.enums.Platforms
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class PlayerStreamingConnectionEvent(
    val player: Player,
    val platforms: Platforms,
    val id: String
): Event() {
    override fun getEventName() = "PlayerStreamingConnectionEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}

data class PlayerStreamingDisConnectionEvent(
    val player: Player,
    val platforms: Platforms,
    val id: String
): Event() {
    override fun getEventName() = "PlayerStreamingDisConnectionEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}