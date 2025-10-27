package kr.apo2073.onair.listeners.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.MessageTarget
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.ChzzkChatEvent
import kr.apo2073.onair.events.ChzzkDonationEvent
import kr.apo2073.onair.events.StreamingChatEvent
import kr.apo2073.onair.events.StreamingDonateEvent
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
        val content = this.message.content

        asynchronously {
            try {
                val nick = userIdToNick(userId) ?: ConfigSet.anon

                runTask {
                    val suc = StreamingChatEvent(player, Platforms.CHZZK, nick, content).callEvent()
                    if (!suc) plugin.logger.warning("StreamingChatEvent 처리 중 오류 발생")
                }

                val userData = UserData(player)
                if (
                    !userData.getChat() || !userData.getConfig().getBoolean("user.connection.chzzk.isConnected")
                ) return@asynchronously

                val format = (ConfigSet.chatFormat
                    ?.replace("{msg}", content)
                    ?.replace("{nick}", nick)
                    ?.replace("{plat}", getPlatformName())
                    ?.replace("{ch}", getChannelName(player))
                    ?.replace(Regex("\\{[^}]*}"), ConfigSet.emoticon)
                    ?.trim() ?: "{nick}: {msg}").toComponent()

                runTask {
                    when (userData.getMessageTarget()) {
                        MessageTarget.STREAMER -> player.sendMessage(format)
                        else -> Bukkit.broadcast(format)
                    }
                }
            } catch (e: Exception) {
                runTask {
                    player.sendMessage(translate("system.boom"), true)
                }
                e.printStackTrace()
            }
        }
    }


    @EventHandler
    fun ChzzkDonationEvent.onDonate() {
        plugin.reloadConfig()
        val suc= StreamingDonateEvent(
            player ?: return, Platforms.CHZZK,
            userIdToNick(this.message.userId),
            this.message.content,
            this.message.payAmount.toDouble()
        ).callEvent()
        if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")

        if (UserData(player).getDonate().not()) return
        if (UserData(player).getConfig().getBoolean("user.connection.chzzk.isConnected").not()) return
        try {
            val userData= UserData(player)
            val format=(ConfigSet.donation.donationFormat
                ?.replace("{msg}", this.message.content)
                ?.replace("{nick}", userIdToNick(this.message.userId) ?: ConfigSet.anon)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", getChannelName(player))
                ?.replace("{paid}", this.message.payAmount.toString())
                ?.replace(Regex("\\{[^}]*}"), ConfigSet.emoticon)
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle= ConfigSet.donation.showTitle
            val title=(ConfigSet.donation.donationFormat
                ?.replace("{msg}", this.message.content)
                ?.replace("{nick}", userIdToNick(this.message.userId) ?: ConfigSet.anon)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", getChannelName(player))
                ?.replace("{paid}", this.message.payAmount.toString())
                ?.replace(Regex("\\{[^}]*}"), ConfigSet.emoticon)
                ?.trim() ?: "{nick}: {msg}").toComponent()

            if (showTitle) player.showTitle(
                Title.title("".toComponent(), title)
            )

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) player.sendMessage(format)
            else Bukkit.broadcast(format)

            Debugger.debug("try to get command( ${message.payAmount} ): ${
                ConfigSet.donation.command(message.payAmount)
            }")
            val ec=(ConfigSet.donation.command(message.payAmount) ?: return)
                .replace("{player}", player.name)
                .replace("{nick}", userIdToNick(message.userId).toString())
                .replace("{paid}", message.payAmount.toString())
                .replace("{msg}", message.content)
            Debugger.debug("execute command ${message.payAmount} as  $ec")
            player.performCommandAsOP(ec)
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getChannelName(player: OfflinePlayer): String {
        val channelName= UserData(player).getConfig().getString("user.connection.chzzk.display")
        val channelId= ConnectionManager.infoConfig.getString(player.uniqueId.toString()) ?: "UNKNOWN"
        return channelName ?: OnAir.chzzkClient.fetchChannel(channelId).channelName
    }

    fun userIdToNick(string: String?): String? {
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
    private fun getPlatformName(): String {
        return Platforms.CHZZK.generate()
    }
}