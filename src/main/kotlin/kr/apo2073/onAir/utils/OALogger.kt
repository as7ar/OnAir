package kr.apo2073.onAir.utils

import kr.apo2073.onAir.utils.Utils.prefix
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class OALogger {
    private fun format(color: String, message: String): Component {
        return "$prefix <#E06B80>></#E06B80> <$color>$message</$color>".toMiniMessage()
    }

    fun info(string: String) = send("white", string)
    fun warning(string: String) = send("yellow", string)
    fun suc(string: String) = send("green", string)
    fun bug(string: String) = send("red", string)

    private fun send(color: String, string: String) {
        Bukkit.getConsoleSender().sendMessage(format(color, string))
    }
}