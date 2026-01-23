package kr.astar.onair.utils

import kr.astar.onair.OnAir

class Debugger {
    companion object {
        private val plugin= OnAir.plugin
        @JvmStatic
        fun debug(string: String) {
            plugin.reloadConfig()
            if (!ConfigSet.debug) return
            plugin.log.info("[DEBUG] $string")
        }
    }
}