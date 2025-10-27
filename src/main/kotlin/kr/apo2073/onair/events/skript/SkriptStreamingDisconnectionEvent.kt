package kr.apo2073.onair.events.skript

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import kr.apo2073.onair.events.PlayerStreamingDisconnectionEvent
import org.bukkit.event.Event

class SkriptStreamingDisconnectionEvent: SkriptEvent() {
    companion object {
        init {
            Skript.registerEvent(
                "SkriptStreamingConnectionEvent",
                SkriptStreamingDisconnectionEvent::class.java,
                PlayerStreamingDisconnectionEvent::class.java,
                "(streaming|stream) disconnection"
            )
        }
    }

    override fun init(args: Array<Literal<*>>, matchedPattern: Int, parseResult: SkriptParser.ParseResult): Boolean {
        return true
    }

    override fun check(event: Event): Boolean {
        return event is PlayerStreamingDisconnectionEvent
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "SkriptStreamingConnectionEvent"
    }
}