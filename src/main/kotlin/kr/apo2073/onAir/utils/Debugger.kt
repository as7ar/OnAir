package kr.apo2073.onAir.utils

import kr.apo2073.onAir.OnAir

class Debugger {
    companion object {
        private val plugin= OnAir.plugin
        @JvmStatic
        fun debug(string: String) {
            plugin.reloadConfig()
            if (
                !plugin.config.getBoolean("debug", false)
            ) return
            plugin.logger.warning("- [DEBUG] $string")
        }
    }
}