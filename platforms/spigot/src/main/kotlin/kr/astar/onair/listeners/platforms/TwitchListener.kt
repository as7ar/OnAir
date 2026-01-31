package kr.astar.onair.listeners.platforms

import com.github.twitch4j.pubsub.domain.ChannelBitsData
import kr.astar.api.twitchLiv.data.Chat
import kr.astar.api.twitchLiv.listener.TwitchEventListener

class TwitchListener: TwitchEventListener {
    override fun onChat(chat: Chat) {

    }

    override fun onBitsEvent(data: ChannelBitsData) {
    }
}