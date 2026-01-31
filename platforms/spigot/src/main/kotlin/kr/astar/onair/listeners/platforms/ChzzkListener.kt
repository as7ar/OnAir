package kr.astar.onair.listeners.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.data.event.ChatContent
import kr.astar.onair.data.event.DonateContent
import kr.astar.onair.enums.Platforms
import kr.astar.onair.events.ChzzkChatEvent
import kr.astar.onair.events.ChzzkDonationEvent
import kr.astar.onair.listeners.EventManager
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Temp
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChzzkListener: Listener {
    private val plugin= OnAir.plugin

    @EventHandler
    fun ChzzkChatEvent.onChat() {
        plugin.reloadConfig()
        val player = player ?: return
        val userId = this.message.userId
        val nick = userIdToNick(userId) ?: ConfigSet.anon
        val content = this.message.content

        EventManager(Platforms.CHZZK).onChat(ChatContent(
            player, nick, content
        ))
    }


    @EventHandler
    fun ChzzkDonationEvent.onDonate() {
        plugin.reloadConfig()
        EventManager(Platforms.CHZZK).onDonate(DonateContent(
            player ?: return, userIdToNick(this.message.userId),
            this.message.payAmount.toDouble(), this.message.content
        ))
    }

    private fun userIdToNick(string: String?): String? {
        try {
            string ?: return null
            return if (Temp.getTempAsString(string)==null) {
                val channel=OnAir.chzzkClient.fetchChannel(string)?.get()?.channelName ?: "Unknown"
                Temp.addTemp(string, channel)
                channel
            } else {
                Temp.getTempAsString(string)
            }
        } catch (e: Exception) {
            return null
        }
    }
}