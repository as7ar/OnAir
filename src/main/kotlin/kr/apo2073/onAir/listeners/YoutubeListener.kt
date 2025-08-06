package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.ytliv.data.Chatting
import kr.apo2073.ytliv.data.SuperChat
import kr.apo2073.ytliv.listener.YouTubeEventListener

class YoutubeListener: YouTubeEventListener {
    override fun onChat(chat: Chatting) {

    }

    override fun onSuperChat(superChat: SuperChat) {

    }

    private fun getPlatformName(): String {
        return Platforms.YOUTUBE.generate()
    }
}