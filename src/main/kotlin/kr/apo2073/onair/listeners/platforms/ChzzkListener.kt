package kr.apo2073.onair.listeners.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.data.event.ChatContent
import kr.apo2073.onair.data.event.DonateContent
import kr.apo2073.onair.enums.MessageTarget
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.ChzzkChatEvent
import kr.apo2073.onair.events.ChzzkDonationEvent
import kr.apo2073.onair.events.StreamingChatEvent
import kr.apo2073.onair.events.StreamingDonateEvent
import kr.apo2073.onair.listeners.EventManager
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Debugger
import kr.apo2073.onair.utils.Temp
import kr.apo2073.onair.utils.Utils.asynchronously
import kr.apo2073.onair.utils.Utils.generate
import kr.apo2073.onair.utils.Utils.performCommandAsOP
import kr.apo2073.onair.utils.Utils.runTask
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.onair.utils.toComponent
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
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
                val channel=OnAir.chzzkClient.fetchChannel(string).channelName
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