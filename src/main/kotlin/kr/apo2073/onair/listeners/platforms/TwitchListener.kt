package kr.apo2073.onair.listeners.platforms

import com.github.twitch4j.pubsub.domain.ChannelBitsData
import kr.apo2073.twitchLiv.data.Chat
import kr.apo2073.twitchLiv.listener.TwitchEventListener

class TwitchListener: TwitchEventListener {
    override fun onChat(chat: Chat) {

    }

    override fun onBitsEvent(data: ChannelBitsData) {
    }
}