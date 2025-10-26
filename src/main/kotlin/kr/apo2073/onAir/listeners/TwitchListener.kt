package kr.apo2073.onAir.listeners

import com.github.twitch4j.pubsub.domain.ChannelBitsData
import kr.apo2073.twitchLiv.data.Chat
import kr.apo2073.twitchLiv.listener.TwitchEventListener

class TwitchListener: TwitchEventListener {
    override fun onChat(chat: Chat) {
    }

    override fun onBitsEvent(data: ChannelBitsData) {
    }
}