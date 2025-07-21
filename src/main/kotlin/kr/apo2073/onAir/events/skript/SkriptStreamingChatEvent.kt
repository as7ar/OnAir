package kr.apo2073.onAir.events.skript

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import kr.apo2073.onAir.events.StreamingChatEvent
import org.bukkit.event.Event

class SkriptStreamingChatEvent:SkriptEvent() {
    companion object {
        init {
            Skript.registerEvent(
                "SkriptStreamingChatEvent",
                SkriptStreamingChatEvent::class.java,
                StreamingChatEvent::class.java,
                "(streaming|stream) chat"
            )
        }
    }

    override fun init(args: Array<Literal<*>>, matchedPattern: Int, parseResult: SkriptParser.ParseResult): Boolean {
        return true
    }

    override fun check(event: Event): Boolean {
        return event is StreamingChatEvent
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "SkriptStreamingChatEvent"
    }
}