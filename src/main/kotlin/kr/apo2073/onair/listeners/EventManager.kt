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

class EventManager(private val platforms: Platforms) {
    private val plugin= OnAir.plugin

    fun onChat(chat: ChatContent) {
        plugin.reloadConfig()
        val player=chat.offlinePlayer.player ?: return
        val platform= platforms.name.lowercase()
        try {
            asynchronously {
                val userData = UserData(player)
                if (!userData.getChat() || !userData.getConfig().getBoolean(
                        "user.connection.${platform}.isConnected"
                )) return@asynchronously

                val format = (ConfigSet.chatFormat
                    ?.replace("{msg}", chat.content)
                    ?.replace("{nick}", chat.nickname ?: ConfigSet.anon)
                    ?.replace("{plat}", getPlatformName())
                    ?.replace("{ch}", getChannelName(player))
                    ?.replace(Regex("\\{[^}]*}|:[^:]+:"), ConfigSet.emoticon)
                    ?.trim() ?: "{nick}: {msg}").toComponent()

                runTask {
                    when (userData.getMessageTarget()) {
                        MessageTarget.STREAMER -> player.sendMessage(format)
                        else -> Bukkit.broadcast(format)
                    }
                }
            }

            runTask {
                val suc = StreamingChatEvent(
                    player, platforms,
                    chat.nickname, chat.content
                ).callEvent()
                if (!suc) plugin.logger.warning("StreamingChatEvent 처리 중 오류 발생")
            }
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    fun onDonate(donate: DonateContent) {
        plugin.reloadConfig()
        val player=donate.offlinePlayer.player ?: return
        val platform= platforms.name.lowercase()

        try {
            asynchronously {
                val userData = UserData(player)
                if (!userData.getDonate() || !userData.getConfig().getBoolean(
                        "user.connection.${platform}.isConnected"
                )) return@asynchronously

                val format=(ConfigSet.donation.donationFormat
                    ?.replace("{msg}", donate.content ?: "")
                    ?.replace("{nick}", donate.nickname ?: ConfigSet.anon)
                    ?.replace("{plat}", getPlatformName())
                    ?.replace("{ch}", getChannelName(player))
                    ?.replace("{paid}", donate.payAmount.toString())
                    ?.replace(Regex("\\{[^}]*}|:[^:]+:"), ConfigSet.emoticon)
                    ?.trim() ?: "{nick}: {msg}").toComponent()

                val showTitle= ConfigSet.donation.showTitle
                val title=(ConfigSet.donation.donationFormat
                    ?.replace("{msg}", donate.content ?: "")
                    ?.replace("{nick}", donate.nickname ?: ConfigSet.anon)
                    ?.replace("{plat}", getPlatformName())
                    ?.replace("{ch}", getChannelName(player))
                    ?.replace("{paid}", donate.payAmount.toString())
                    ?.replace(Regex("\\{[^}]*}|:[^:]+:"), ConfigSet.emoticon)
                    ?.trim() ?: "{nick}: {msg}").toComponent()

                if (showTitle) player.showTitle(
                    Title.title("".toComponent(), title)
                )

                val target=userData.getMessageTarget()
                if (target== MessageTarget.STREAMER) player.sendMessage(format)
                else Bukkit.broadcast(format)

                Debugger.debug("try to get command( ${donate.payAmount} ): ${
                    ConfigSet.donation.command(donate.payAmount.toInt())
                }")
                val ec=(ConfigSet.donation.command(donate.payAmount.toInt()) ?: return@asynchronously)
                    .replace("{player}", player.name)
                    .replace("{nick}", donate.nickname ?: ConfigSet.anon)
                    .replace("{paid}", donate.payAmount.toString())
                    .replace("{msg}", donate.content ?: "")
                Debugger.debug("execute command ${donate.payAmount} as  $ec")
                player.performCommandAsOP(ec)
            }

            runTask {
                val suc= StreamingDonateEvent(
                    player, platforms,
                    donate.nickname, donate.content,
                    donate.payAmount
                ).callEvent()
                if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")
            }
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getChannelName(player: OfflinePlayer): String {
        val channelName= UserData(player).getConfig()
            .getString("user.connection.${platforms.name.lowercase()}.display")
        val channelId= ConnectionManager.infoConfig
            .getString(player.uniqueId.toString()) ?: "UNKNOWN"
        return if (platforms==Platforms.CHZZK) {
            channelName ?: OnAir.chzzkClient.fetchChannel(channelId).channelName
        } else channelName ?:  "UNKNOWN"
    }

    private fun getPlatformName(): String {
        return platforms.generate()
    }
}