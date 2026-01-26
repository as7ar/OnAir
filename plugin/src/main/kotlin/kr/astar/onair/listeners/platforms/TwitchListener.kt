package kr.astar.onair.listeners.platforms

import com.github.twitch4j.pubsub.domain.ChannelBitsData
import kr.astar.onair.api.twitchLiv.data.Chat
import kr.astar.onair.api.twitchLiv.listener.TwitchEventListener

class TwitchListener: TwitchEventListener {
    override fun onChat(chat: Chat) {

    }

    override fun onBitsEvent(data: ChannelBitsData) {
    }
}