package kr.astar.onair.paper

import kr.astar.onair.paper.papi.PlaceholderHandler
import org.bukkit.plugin.java.JavaPlugin

class OnAirPaper {

    fun platformEnable(plugin: JavaPlugin) {
        if (plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            PlaceholderHandler(plugin).register()
            println("PAPI Enabled on Paper")
            println("PAPI Enabled on Paper")
            println("PAPI Enabled on Paper")
            println("PAPI Enabled on Paper")
            println("PAPI Enabled on Paper")
        }
    }
}