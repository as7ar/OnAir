package kr.apo2073.onair.events.skript

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import kr.apo2073.onair.events.PlayerStreamingConnectionEvent
import org.bukkit.event.Event

class SkriptStreamingConnectionEvent: SkriptEvent() {
    companion object {
        init {
            Skript.registerEvent(
                "SkriptStreamingConnectionEvent",
                SkriptStreamingConnectionEvent::class.java,
                PlayerStreamingConnectionEvent::class.java,
                "(streaming|stream) connection"
            )
        }
    }

    override fun init(args: Array<Literal<*>>, matchedPattern: Int, parseResult: SkriptParser.ParseResult): Boolean {
        return true
    }

    override fun check(event: Event): Boolean {
        return event is PlayerStreamingConnectionEvent
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "SkriptStreamingConnectionEvent"
    }
}