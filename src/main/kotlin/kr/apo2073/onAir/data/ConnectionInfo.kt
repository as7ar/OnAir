package kr.apo2073.onAir.data

import kr.apo2073.onAir.OnAir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ConnectionInfo {
    private val plugin= OnAir.plugin

    internal var file= File(plugin.dataFolder, "connection")
    internal var config = YamlConfiguration.loadConfiguration(file)

    fun setValue(path:String, value: Any?) {
        file= File(plugin.dataFolder, "connection")
        config= YamlConfiguration.loadConfiguration(file)
        config.apply {
            set(path, value)
        }.save(file)
    }
}