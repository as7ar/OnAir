package kr.astar.onair.utils

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class OALogger(private val audiences: BukkitAudiences) {

    private fun format(color: String, message: String): Component {
        return MiniMessage.miniMessage().deserialize(
            ConfigSet.prefix.replace(Regex("[|]"), "")
        ).append("<#E06B80>></#E06B80> <$color>$message</$color>".toMiniMessage())
    }

    fun info(string: String) = send("white", string)
    fun warning(string: String) = send("yellow", string)
    fun suc(string: String) = send("green", string)
    fun bug(string: String) = send("red", string)

    private fun send(color: String, string: String) {
        audiences.console().sendMessage(format(color, string))
    }
}