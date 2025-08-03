package kr.apo2073.onAir.events

import kr.apo2073.onAir.enums.Platforms
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerStreamingConnectionEvent(
    player: Player,
    val platform: Platforms,
    val id: String,
    val display:String
): PlayerEvent(player) {
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

class PlayerStreamingDisConnectionEvent(
    player: Player,
    val platform: Platforms,
): PlayerEvent(player) {
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

class StreamingChatEvent(
    streamer:Player,
    val platform: Platforms,
    val user:String?,
    val content:String?
):PlayerEvent(streamer) {
    override fun getEventName() = "StreamingChatEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}

class StreamingDonateEvent(
    streamer:Player,
    val platform: Platforms,
    val user:String?,
    val content:String?,
    val amount: Double
): PlayerEvent(streamer) {
    override fun getEventName() = "StreamingDonateEvent"
    override fun getHandlers(): HandlerList = getHandlerList()
    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}