package kr.apo2073.onAir.data

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class UserData(private val player: OfflinePlayer) {
    private val plugin = OnAir.plugin

    fun getFile(): File {
        val file = File(plugin.dataFolder, "userdata/${player.uniqueId}.yml")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        return file
    }

    fun getConfig(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(getFile())
    }

    fun getChat(): Boolean = getConfig().getBoolean("user.alert.chat", true)

    fun setChat(bool: Boolean) {
        updateConfig("user.alert.chat", bool)
    }

    fun getDonate(): Boolean = getConfig().getBoolean("user.alert.donate", true)

    fun setDonate(bool: Boolean) {
        updateConfig("user.alert.donate", bool)
    }

    fun getMessageTarget(): MessageTarget {
        val value = getConfig().getString("user.alert.target") ?: "STREAMER"
        return MessageTarget.valueOf(value)
    }

    fun setMessageTarget(target: MessageTarget) {
        updateConfig("user.alert.target", target.name)
    }

    fun getConnections(): MutableList<Platforms> {
        return getConfig().getStringList("user.connection-list")
            .mapNotNull { runCatching { Platforms.valueOf(it) }.getOrNull() }
            .toMutableList()
    }

    fun addConnection(platform: Platforms) {
        val connections = getConnections()
        if (!connections.contains(platform)) {
            connections.add(platform)
            val config = YamlConfiguration.loadConfiguration(
                File(plugin.dataFolder, "userdata/${player.uniqueId}.yml")
            )
            config.apply {
                set("user.connection-list", connections.map { it.name })
            }.save(File(plugin.dataFolder, "userdata/${player.uniqueId}.yml"))
        }
        println(getConnections())
    }

    fun removeConnection(platform: Platforms) {
        updateConfig("user.connection.${platform.name.lowercase()}", null)
        val connections = getConnections()
        connections.remove(platform)
        updateConfig("user.connection-list", connections.map { it.name })
    }

    private fun updateConfig(path: String, value: Any?) {
        val config = this.getConfig()
        config.apply {
            set(path, value)
        }.save(getFile())
    }
}
