package kr.apo2073.onAir.soop.listener

import kr.apo2073.onAir.soop.SoopPacket
import okhttp3.Response

interface SoopListener {
    fun onConnect()
    fun onMessage(packet: SoopPacket)
    fun onFail(t: Throwable, response: Response?)
    fun onError(e: Exception)
    fun onDisconnect()
}