package kr.apo2073.onAir.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.enums.Platforms
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.io.File

object Utils {
    private val plugin= OnAir.plugin
    val prefix = MiniMessage.miniMessage().deserialize("<b><gradient:#E7B0B0:#BA4242>[ OnAir ]</gradient></b> ")
    fun Player.sendMessage(string: String, boolean: Boolean=true) {
        this.sendMessage(
            if (boolean) prefix.append(string.toComponent())
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

    fun translate(string: String): String {
        plugin.reloadConfig()
        val lang=plugin.config.getString("language") ?: "ko"
        val json= File("${plugin.dataFolder}/lang", "${lang}.json")
        return Gson().fromJson(json.readText(), JsonObject::class.java)
            .get(string)?.asString ?: string
    }

    fun Platforms.generate(): String {
        when(this) {
            Platforms.CHZZK -> {
                val platform = if (plugin.config.getBoolean("플렛폼.영어")) "Chzzk" else "치지직"
                return if (plugin.config.getBoolean("플렛폼.채색")) "§a$platform§f" else platform
            }
            Platforms.YOUTUBE -> {
                val platform = if (plugin.config.getBoolean("플렛폼.영어")) "Youtube" else "유튜브"
                return if (plugin.config.getBoolean("플렛폼.채색")) "§c$platform§f" else platform
            }
            Platforms.TOONATION -> {
                val platform = if (plugin.config.getBoolean("플렛폼.영어")) "Toonation" else "투네이션"
                return if (plugin.config.getBoolean("플렛폼.채색")) "§9$platform§f" else platform
            }
        }
    }
}

