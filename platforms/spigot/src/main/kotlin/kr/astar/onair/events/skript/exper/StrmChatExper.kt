package kr.astar.onair.events.skript.exper

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.astar.onair.events.StreamingChatEvent
import kr.astar.onair.utils.Utils.generate
import org.bukkit.event.Event

class StrmChatExper: SimpleExpression<String>() {
    companion object {
        init {
            Skript.registerExpression(
                StrmChatExper::class.java,
                String::class.java,
                ExpressionType.PROPERTY,
                "platform [of [(the|a)] (stream[ing]|chat)]",
                "[chat] user['s id]",
                "content [of the chat]"
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
        if (event is StreamingChatEvent) {
            return when (pattern) {
                0 -> arrayOf(event.platform.generate())
                1 -> arrayOf(event.user)
                2 -> arrayOf(event.content)
                else -> arrayOf()
            }
        }
        return arrayOf()
    }

    override fun isSingle() = true
    override fun getReturnType() = String::class.java
    override fun toString(e: Event?, debug: Boolean): String {
        return when (pattern) {
            0 -> "platform of the streaming chat"
            1 -> "user id of the streaming chat"
            2 -> "content of the streaming chat"
            else -> "unknown streaming chat expr"
        }
    }
}