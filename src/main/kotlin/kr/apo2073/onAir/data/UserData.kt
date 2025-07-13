package kr.apo2073.onAir.data

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class UserData(private val player: OfflinePlayer) {
    private val plugin= OnAir.plugin

    fun getFile(): File=File("${plugin.dataFolder}/userdata", "${player.uniqueId}.yml")
    fun getConfig(): YamlConfiguration= YamlConfiguration.loadConfiguration(getFile())

    fun getChat(): Boolean {
        return getConfig().getBoolean("user.alert.chat", true)
    }

    fun setChat(bool: Boolean) {
        getConfig().apply {
            set("user.alert.chat", bool)
        }.save(getFile())
    }

    fun getDonate(): Boolean {
        return getConfig().getBoolean("user.alert.donate", true)
    }
    fun setDonate(bool: Boolean) {
        getConfig().apply {
            set("user.alert.donate", bool)
        }.save(getFile())
    }

    fun getMessageTarget(): MessageTarget {
        return MessageTarget.valueOf(getConfig().getString("user.alert.target") ?: "STREAMER")
    }

    fun setMessageTarget(messageTarget: MessageTarget) {
        getConfig().apply {
            set("user.alert.target", messageTarget.name)
        }.save(getFile())
    }

    fun getConnections(): MutableList<Platforms> {
        return getConfig().getStringList("connections")
            .map { Platforms.valueOf(it) }
            .toMutableList()
    }

    fun addConnection(platforms: Platforms) {
        getConfig().apply {
            set("connections", getConnections().apply { add(platforms) })
        }.save(getFile())
    }

    fun removeConnection(platforms: Platforms) {
        getConfig().apply {
            set("connections", getConnections().apply { remove(platforms) })
        }.save(getFile())
    }
}