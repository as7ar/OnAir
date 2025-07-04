package kr.apo2073.onAir.data

import kr.apo2073.onAir.OnAir
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class UserData(private val player: OfflinePlayer) {
    private val plugin= OnAir.plugin

    fun getFile(): File=File("${plugin.dataFolder}/userdata", "${player.uniqueId}.yml")
    fun getConfig(): YamlConfiguration= YamlConfiguration.loadConfiguration(getFile())

    fun getConnections() {

    }
}