package kr.apo2073.onAir.events.skript

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import kr.apo2073.onAir.events.PlayerStreamingDisConnectionEvent
import org.bukkit.event.Event

class SkriptStreamingDisConnectionEvent: SkriptEvent() {
    companion object {
        init {
            Skript.registerEvent(
                "SkriptStreamingConnectionEvent",
                SkriptStreamingDisConnectionEvent::class.java,
                PlayerStreamingDisConnectionEvent::class.java,
                "(streaming|stream) disconnection"
            )
        }
    }

    override fun init(args: Array<Literal<*>>, matchedPattern: Int, parseResult: SkriptParser.ParseResult): Boolean {
        return true
    }

    override fun check(event: Event): Boolean {
        return event is PlayerStreamingDisConnectionEvent
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "SkriptStreamingConnectionEvent"
    }
}