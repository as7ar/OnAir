package kr.astar.onair.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.astar.onair.OnAir
import kr.astar.onair.enums.Platforms
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object Utils {
    private val plugin= OnAir.plugin
    val prefix = MiniMessage.miniMessage().deserialize(ConfigSet.prefix)
    fun Player.sendMessage(string: String, boolean: Boolean=true) {
        this.sendMessage(
            if (boolean) prefix.append(
                try {
                    string.toMiniMessage()
                } catch (e: Exception) {
                    string.toComponent()
                }
            )
            else string.toComponent()
        )
    }
    fun Player.sendMessage(component: Component, boolean: Boolean=true) {
        this.sendMessage(
            if (boolean) prefix.append(component)
            else component
        )
    }

    fun Player.performCommandAsOP(command: String) {
        val iisOP = this.isOp
        this.isOp = true
        this.performCommand(command)
        this.isOp = iisOP
    }

    fun Player.strUUID(): String = this.uniqueId.toString()

    fun UUID.str(): String = this.toString()
    fun String.toUUID(): UUID= UUID.fromString(this)

    fun translate(string: String): String {
        plugin.reloadConfig()
        val lang=plugin.config.getString("language") ?: "ko"
        val json= File("${plugin.dataFolder}/lang", "${lang}.json")
        Debugger.debug(json.toString())
        return Gson().fromJson(json.readText(), JsonObject::class.java).get(string)?.asString
            ?.replace("{plugin}", plugin.pluginMeta.name) ?: string
    }

    fun translate(key: String, placeholders: Map<String, String>): String {
        var message = translate(key)
        placeholders.forEach { (placeholder, value) ->
            message = message.replace("{$placeholder}", value)
        }
        return message
    }

    fun Platforms.generate(): String {
        when(this) {
            Platforms.CHZZK -> {
                val platform = if (ConfigSet.en) "Chzzk" else "치지직"
                return if (ConfigSet.colored) "§a$platform§r" else platform
            }
            Platforms.YOUTUBE -> {
                val platform = if (ConfigSet.en) "Youtube" else "유튜브"
                return if (ConfigSet.colored) "§c$platform§r" else platform
            }
            Platforms.TOONATION -> {
                val platform = if (ConfigSet.en) "Toonation" else "투네이션"
                return if (ConfigSet.colored) "§b$platform§r" else platform
            }
            Platforms.TWITCH -> {
                val platform = if (ConfigSet.en) "Twitch" else "트위치"
                return if (ConfigSet.colored) "§d$platform§r" else platform
            }
            Platforms.SOOP -> {
                val platform = if (ConfigSet.en) "Soop" else "숲"
                return if (ConfigSet.colored) "§9$platform§r" else platform
            }
            else-> return "알 수 없음"
        }
    }

    fun String.toPlatform():Platforms {
        if (this.uppercase().contains("CHZZK") || this.contains("치지직")) return Platforms.CHZZK
        if (this.uppercase().contains("YOUTUBE") || this.contains("유튜브")) return Platforms.YOUTUBE
        if (this.uppercase().contains("TOONATION") || this.contains("투네이션")) return Platforms.TOONATION
        if (this.uppercase().contains("TWITCH") || this.contains("트위치")) return Platforms.TWITCH
        if (this.uppercase().contains("SOOP") || this.contains("숲")) return Platforms.SOOP
        return Platforms.UNKNOWN
    }

    fun bannerGenerator(
        artLines: List<String>,
        version: String,
        author: String
    ): Array<String> {
        val innerWidth = artLines.maxOf { it.length }

        fun boxed(line: String): String =
            "|" + line.padEnd(innerWidth, ' ') + "|"

        val topBottom = "+" + "=".repeat(innerWidth) + "+"
        val separator = "|" + "=".repeat(innerWidth) + "|"

        val info = "Version: $version     author: $author"
            .padEnd(innerWidth, ' ')

        val result = mutableListOf<String>()
        result += topBottom
        result += artLines.map { boxed(it) }
        result += separator
        result += boxed(info)
        result += topBottom
        return result.toTypedArray()
    }

    infix fun asynchronously(runnable: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
    }

    infix fun runTask(runnable: Runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable)
    }
}

