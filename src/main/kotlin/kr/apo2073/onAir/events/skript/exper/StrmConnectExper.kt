package kr.apo2073.onAir.events.skript.exper

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.apo2073.onAir.events.PlayerStreamingConnectionEvent
import kr.apo2073.onAir.utils.Utils.generate
import org.bukkit.event.Event

class StrmConnectExper: SimpleExpression<String>() {
    companion object {
        init {
            Skript.registerExpression(
                StrmConnectExper::class.java,
                String::class.java,
                ExpressionType.PROPERTY,
                "platform [of [(the|a)] connection]",
                "(the|a) (channel|stream[ing])'s (channel id|channel key|key|id)",
                "(the|a) (channel|stream[ing])'s (chennel display|display)"
            )
        }
    }

    private var pattern: Int = 0

    override fun init(
        exprs: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        pattern = matchedPattern
        return true
    }

    override fun get(event: Event): Array<String?> {
        if (event is PlayerStreamingConnectionEvent) {
            return when (pattern) {
                0 -> arrayOf(event.platform.generate())
                1 -> arrayOf(event.id)
                2 -> arrayOf(event.display)
                else -> arrayOf()
            }
        }
        return arrayOf()
    }

    override fun isSingle() = true
    override fun getReturnType() = String::class.java
    override fun toString(e: Event?, debug: Boolean) = "streaming connection exper"
}