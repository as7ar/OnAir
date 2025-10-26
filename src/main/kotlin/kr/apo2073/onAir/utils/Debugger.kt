package kr.apo2073.onAir.utils

import kr.apo2073.onAir.OnAir
import org.bukkit.Bukkit

class Debugger {
    companion object {
        private val plugin= OnAir.plugin
        @JvmStatic
        fun debug(string: String) {
            plugin.reloadConfig()
            if (!ConfigSet.debug) return
            OALogger().info("[DEBUG] $string")
        }
    }
}