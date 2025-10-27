package kr.apo2073.onair.player

import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.data.toChannelData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.utils.Temp
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

    fun disconnectAll() {
        getUserdata().getConnections().forEach {
            ConnectionManager.Manager(player).disconnect(it)
        }
    }

    fun saveTemp() {
        val data=getUserdata()
        val l= data.getConnections().joinToString("|") { data.getChannelData(it).toString() }
        Temp.addTemp("${player.uniqueId}", l)
    }

    fun loadTemp() {
        val data= Temp.getTemp("${player.uniqueId}", String::class) ?: return
        val l= data.split("|").map { it.toChannelData() }
        l.forEach {
            ConnectionManager.Manager(player).connect(it.platforms, it.display, it.id)
        }
    }
}