package kr.apo2073.onair.events.skript.exper

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.apo2073.onair.events.PlayerStreamingDisconnectionEvent
import kr.apo2073.onair.utils.Utils.generate
import org.bukkit.event.Event

class StrmDisconnectExper: SimpleExpression<String>() {
    companion object {
        init {
            Skript.registerExpression(
                StrmDisconnectExper::class.java,
                String::class.java,
                ExpressionType.PROPERTY,
                "platform [of [(the|a)] disconnection]",
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
        if (event is PlayerStreamingDisconnectionEvent) {
            return when (pattern) {
                0 -> arrayOf(event.platform.generate())
                else -> arrayOf()
            }
        }
        return arrayOf()
    }

    override fun isSingle() = true
    override fun getReturnType() = String::class.java
    override fun toString(e: Event?, debug: Boolean) = "streaming disconnection exper"
}