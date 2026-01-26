package kr.astar.onair.spigot

import kr.astar.onair.spigot.papi.PlaceholderHandler
import org.bukkit.plugin.java.JavaPlugin

class OnAirSpigot {

   fun platformEnable(plugin: JavaPlugin) {
        if (plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            PlaceholderHandler(plugin).register()
            println("PAPI Enabled on Spigot")
            println("PAPI Enabled on Spigot")
            println("PAPI Enabled on Spigot")
            println("PAPI Enabled on Spigot")
        }
    }
}