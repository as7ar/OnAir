package kr.apo2073.onAir.utils

import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import org.bukkit.entity.Player

class Streamer(private val player: Player) {
    fun getUserdata(): UserData = UserData(player)
    fun getPlayer(): Player = player
    fun connect(platforms: Platforms, channelName:String, id:String) {
        ConnectionManager.Manager(player).connect(platforms, channelName, id)
    }
    fun disconnect(platforms: Platforms) {
        ConnectionManager.Manager(player).disconnect(platforms)
    }
}