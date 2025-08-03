package kr.apo2073.onAir.utils

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.stream.ChkConnect
import kr.apo2073.onAir.stream.TnConnect
import kr.apo2073.onAir.stream.YtConnect
import org.bukkit.entity.Player

class ConnectManager(private val player:Player) {
    private val plugin=OnAir.plugin
    private val userData=UserData(player)

    fun connect(platforms: Platforms, channelName:String, id:String) {
        when(platforms) {
            Platforms.CHZZK -> {
                userData.getConfig().apply {
                    set("user.connection.chzzk.display", channelName)
                }.save(userData.getFile())
                ChkConnect.connect(player, id)
            }
            Platforms.YOUTUBE -> {
                userData.getConfig().apply {
                    set("user.connection.youtube.display", channelName)
                }.save(userData.getFile())
                YtConnect.connect(player, id)
            }
            Platforms.TOONATION -> {
                userData.getConfig().apply {
                    set("user.connection.toonation.display", channelName)
                }.save(userData.getFile())
                TnConnect.connect(player, id)
            }
            else-> return
        }
    }

    fun disconnect(platforms: Platforms) {
        when(platforms) {
            Platforms.CHZZK -> ChkConnect.disconnect(player)
            Platforms.YOUTUBE -> YtConnect.disconnect(player)
            Platforms.TOONATION -> TnConnect.disconnect(player)
            else-> return
        }
    }
}