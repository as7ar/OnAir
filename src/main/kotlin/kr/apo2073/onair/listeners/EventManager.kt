package kr.apo2073.onair.listeners

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.data.event.ChatContent
import kr.apo2073.onair.data.event.DonateContent
import kr.apo2073.onair.enums.MessageTarget
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.StreamingChatEvent
import kr.apo2073.onair.events.StreamingDonateEvent
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Debugger
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
import org.bukkit.entity.Player

class EventManager(private val platforms: Platforms) {
    private val plugin= OnAir.plugin

    fun onChat(chat: ChatContent) {
        plugin.reloadConfig()
        val player = chat.offlinePlayer.player ?: return

        safely(player) {
            asynchronously {
                val userData = UserData(player)
                if (
                    !userData.getChat()
                    || !ConnectionManager.isConnected(player, platforms)
                ) return@asynchronously

                val formatted = formatMessage(
                    ConfigSet.chatFormat,
                    chat.nickname, chat.content, player
                )

                runTask {
                    when (userData.getMessageTarget()) {
                        MessageTarget.STREAMER -> player.sendMessage(formatted)
                        else -> Bukkit.broadcast(formatted)
                    }
                }
            }

            callChatEvent(player, chat)
        }
    }

    fun onDonate(donate: DonateContent) {
        plugin.reloadConfig()
        val player = donate.offlinePlayer.player ?: return

        safely(player) {
            asynchronously {
                val userData = UserData(player)
                if (
                    !userData.getDonate()
                    || !ConnectionManager.isConnected(player, platforms)
                ) return@asynchronously

                val formatted = formatMessage(
                    ConfigSet.donation.donationFormat,
                    donate.nickname, donate.content, player,
                    donate.payAmount
                )

                val titleComponent = formatMessage(
                    ConfigSet.donation.titleFormat,
                    donate.nickname, donate.content, player,
                    donate.payAmount
                )

                if (ConfigSet.donation.showTitle) {
                    player.showTitle(Title.title("".toComponent(), titleComponent))
                }

                when (userData.getMessageTarget()) {
                    MessageTarget.STREAMER -> player.sendMessage(formatted)
                    else -> Bukkit.broadcast(formatted)
                }

                executeCommand(player, donate)
            }

            callDonateEvent(player, donate)
        }
    }

    private fun formatMessage(
        baseFormat: String?,
        nickname: String?,
        message: String?,
        player: OfflinePlayer,
        paid: Double? = null
    ) = (baseFormat
        ?.replace("{msg}", message ?: "")
        ?.replace("{nick}", nickname ?: ConfigSet.anon)
        ?.replace("{plat}", platforms.generate())
        ?.replace("{ch}", getChannelName(player))
        ?.replace("{paid}", paid?.toString() ?: "")
        ?.replace(Regex("\\{[^}]*}|:[^:]+:"), ConfigSet.emoticon)
        ?.trim() ?: "{nick}: {msg}").toComponent()

    private fun executeCommand(player: Player, donate: DonateContent) {
        val command = ConfigSet.donation.command(donate.payAmount.toInt()) ?: return
        val parsed = command
            .replace("{player}", player.name)
            .replace("{nick}", donate.nickname ?: ConfigSet.anon)
            .replace("{paid}", donate.payAmount.toString())
            .replace("{msg}", donate.content ?: "")

        Debugger.debug("Executing donation command: $parsed")
        player.performCommandAsOP(parsed)
    }

    private fun callChatEvent(player: Player, chat: ChatContent) {
        runTask {
            val success = StreamingChatEvent(
                player, platforms, chat.nickname, chat.content
            ).callEvent()
            if (!success) plugin.log.warning("StreamingChatEvent 처리 중 오류 발생")
        }
    }

    private fun callDonateEvent(player: Player, donate: DonateContent) {
        runTask {
            val success = StreamingDonateEvent(
                player, platforms, donate.nickname, donate.content, donate.payAmount
            ).callEvent()
            if (!success) plugin.log.warning("StreamingDonateEvent 처리 중 오류 발생")
        }
    }

    private fun getChannelName(player: OfflinePlayer): String {
        val userConfig = UserData(player).getConfig()
        val name = userConfig.getString(
            "user.connection.${platforms.name.lowercase()}.display"
        )
        val id = ConnectionManager.infoConfig
            .getString(player.uniqueId.toString()) ?: "UNKNOWN"
        return when (platforms) {
            Platforms.CHZZK -> name ?: OnAir.chzzkClient.fetchChannel(id).channelName
            else -> name ?: "UNKNOWN"
        }
    }

    private inline fun safely(player:Player, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }
}