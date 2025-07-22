package kr.apo2073.onAir.events.skript.exper

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.apo2073.onAir.events.StreamingDonateEvent
import kr.apo2073.onAir.utils.Utils.generate
import org.bukkit.event.Event

class StrmDonateExper: SimpleExpression<String>() {
    companion object {
        init {
            Skript.registerExpression(
                StrmDonateExper::class.java,
                String::class.java,
                ExpressionType.PROPERTY,
                "platform [of the stream[ing]]",
                "[chat] user's id",
                "content [of donate] [message]",
                "[donate] amount"
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
        if (event is StreamingDonateEvent) {
            return when (pattern) {
                0 -> arrayOf(event.platform.generate())
                1 -> arrayOf(event.user)
                2 -> arrayOf(event.content)
                3-> arrayOf(event.amount.toString())
                else -> arrayOf()
            }
        }
        return arrayOf()
    }

    override fun isSingle() = true
    override fun getReturnType() = String::class.java
    override fun toString(e: Event?, debug: Boolean): String {
        return when (pattern) {
            0 -> "platform of the streaming donate"
            1 -> "user id of the streaming donate"
            2 -> "content of the streaming donate"
            3-> "pay amount of the streaming donate"
            else -> "unknown streaming donate expr"
        }
    }
}