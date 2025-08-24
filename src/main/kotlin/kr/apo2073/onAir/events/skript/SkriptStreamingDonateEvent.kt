package kr.apo2073.onAir.events.skript

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import kr.apo2073.onAir.events.StreamingDonateEvent
import org.bukkit.event.Event

class SkriptStreamingDonateEvent: SkriptEvent() {
    companion object {
        init {
            Skript.registerEvent(
                "SkriptStreamingDonateEvent",
                SkriptStreamingDonateEvent::class.java,
                StreamingDonateEvent::class.java,
                "(streaming|stream) donat(e|ion)"
            )
        }
    }

    override fun init(args: Array<Literal<*>>, matchedPattern: Int, parseResult: SkriptParser.ParseResult): Boolean {
        return true
    }

    override fun check(event: Event): Boolean {
        return event is StreamingDonateEvent
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "SkriptStreamingDonateEvent"
    }
}