package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.OnAir
import kr.apo2073.soopliv.data.Chat
import kr.apo2073.soopliv.data.Donate
import kr.apo2073.soopliv.soop.listener.SoopEventListener

class SoopListener: SoopEventListener() {
    private val plugin = OnAir.plugin

    override fun onChat(chat: Chat) {
        plugin.reloadConfig()

    }

    override fun onDonation(donate: Donate) {

    }
}