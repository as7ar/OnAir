package kr.astar.onair.listeners

import kr.astar.onair.player.Streamer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitListener: Listener {
    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        val streamer= Streamer(player)
        streamer.loadTemp()
    }

    @EventHandler
    fun PlayerQuitEvent.onQuit() {
        val streamer= Streamer(player)
        streamer.saveTemp()
        streamer.disconnectAll()
    }
}