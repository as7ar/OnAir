package kr.apo2073.onAir.utils

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import org.bukkit.entity.Player

class ConnectManager(private val player:Player) {
    private val plugin=OnAir.plugin
    private val userData=UserData(player)
    private val cic=ConnectionInfo.config
    private val cif=ConnectionInfo.file

    fun connect(platforms: Platforms, channelName:String, id:String) {}

    fun disconnect(platforms: Platforms) {}
}