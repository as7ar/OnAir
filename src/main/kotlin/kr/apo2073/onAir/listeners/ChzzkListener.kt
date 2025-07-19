package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.ChzzkChatEvent
import kr.apo2073.onAir.events.ChzzkDonationEvent
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.performCommandAsOP
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.onAir.utils.toComponent
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChzzkListener: Listener {
    private val plugin= OnAir.plugin

    @EventHandler
    fun ChzzkChatEvent.onChat() {
        if (UserData(player ?: return).getChat().not()) return
        if (UserData(player).getConfig().getBoolean("user.connection.chzzk.isConnected").not()) return
        try {
            val userData= UserData(player)
            val format=(plugin.config.getString("채팅.형식")
                ?.replace("{msg}", this.message.content)
                ?.replace("{user}", this.message.userId ?: "(익명)")
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", getChannelName(player))
                ?.replace(Regex("\\{[^}]*}"), "&7(이모티콘)&f")
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) {
                player.sendMessage(format)
            } else {
                Bukkit.broadcast(format)
            }
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    @EventHandler
    fun ChzzkDonationEvent.onDonate() {
        if (UserData(player ?: return).getDonate().not()) return
        if (UserData(player).getConfig().getBoolean("user.connection.chzzk.isConnected").not()) return
        try {
            val userData= UserData(player)
            val format=(plugin.config.getString("후원.형식")
                ?.replace("{msg}", this.message.content)
                ?.replace("{user}", this.message.userId ?: "(익명)")
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", getChannelName(player))
                ?.replace(Regex("\\{[^}]*}"), "&7(이모티콘)&f")
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle=plugin.config.getBoolean("후원.타이틀표시", true)
            val title=(plugin.config.getString("후원.타이틀형식")
                ?.replace("{msg}", this.message.content)
                ?.replace("{user}", this.message.userId ?: "(익명)")
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", getChannelName(player))
                ?.replace(Regex("\\{[^}]*}"), "&7(이모티콘)&f")
                ?.trim() ?: "{nick}: {msg}").toComponent()

            if (showTitle) {
                player.showTitle(
                    Title.title("".toComponent(), title)
                )
            }

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) {
                player.sendMessage(format)
            } else {
                Bukkit.broadcast(format)
            }

            val ec=(plugin.config.getString("${message.payAmount}") ?: return)
                .replace("{player}", player.name)
                .replace("{user}", message.userId)
                .replace("{paid}", message.payAmount.toString())
                .replace("{msg}", message.content)
            player.performCommandAsOP(ec)

        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getChannelName(player: OfflinePlayer): String {
        val channelName= UserData(player).getConfig().getString("user.chzzk.display")
        val channelId= ConnectionInfo.config.getString(player.uniqueId.toString()) ?: "알 수 없음"
        return channelName ?: OnAir.chzzkClient.fetchChannel(channelId).channelName
    }

    private fun getPlatformName(): String {
        return Platforms.CHZZK.generate()
    }
}