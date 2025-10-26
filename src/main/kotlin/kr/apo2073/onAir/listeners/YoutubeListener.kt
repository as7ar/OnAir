package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.StreamingChatEvent
import kr.apo2073.onAir.events.StreamingDonateEvent
import kr.apo2073.onAir.utils.ConfigSet
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.performCommandAsOP
import kr.apo2073.onAir.utils.Utils.runTask
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.toUUID
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.onAir.utils.toComponent
import kr.apo2073.utubeLiv.data.Chatting
import kr.apo2073.utubeLiv.data.SuperChat
import kr.apo2073.utubeLiv.listener.YouTubeEventListener
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

class YoutubeListener: YouTubeEventListener {
    private val plugin = OnAir.plugin

    override fun onChat(chat: Chatting) {
        plugin.reloadConfig()
        val cic= ConnectionManager.infoConfig
        val uuid=cic.getString(chat.videoId)?.toUUID() ?: run {
            plugin.logger.warning(translate(
                "system.boom",
                mapOf("err" to "uuid parse error (${cic.getString(chat.videoId)})")
            ))
            return
        }
        val player= Bukkit.getPlayer(uuid) ?: run {
            plugin.logger.warning(translate(
                "system.boom",
                mapOf("err" to "current donator's player is not exist or offline")
            ))
            return
        }
        Debugger.debug("Player loaded: ${player.name}")

        runTask {
            Debugger.debug("Calling StreamingChatEvent")
            val suc= StreamingChatEvent(
                player, Platforms.YOUTUBE,
                chat.author().name,
                chat.message
            ).callEvent()
            if (!suc) plugin.logger.warning("StreamingChatEvent를 처리하던 중 오류가 발생했습니다")
        }

        try {
            if (UserData(player).getChat().not()) {
                Debugger.debug("Chatting not allowed")
                return
            }
            if (UserData(player).getConfig().getBoolean("user.connection.youtube.isConnected").not()) {
                Debugger.debug("Channel not connected") // 여기서 막힘
                return
            }

            val channelName=UserData(player).getConfig().getString("user.connection.youtube.display") ?: return

            val userData= UserData(player)
            val format=(ConfigSet.chatFormat
                ?.replace("{msg}", chat.message)
                ?.replace("{nick}", chat.author().name)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace(Regex(":[^:]+:"), ConfigSet.emoticon)
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) player.sendMessage(format)
            else Bukkit.broadcast(format)
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    override fun onSuperChat(superChat: SuperChat) {
        val cic= ConnectionManager.infoConfig
        val uuid=cic.getString(superChat.videoId)?.toUUID() ?: run {
            plugin.logger.warning(translate(
                "system.boom",
                mapOf("err" to "uuid parse error (${cic.getString(superChat.videoId)})")
            ))
            return
        }
        val player= Bukkit.getPlayer(uuid) ?: run {
            plugin.logger.warning(translate(
                "system.boom",
                mapOf("err" to "current donator's player is not exist or offline")
            ))
            return
        }

        plugin.reloadConfig()
        runTask {
            val suc= StreamingDonateEvent(
                player, Platforms.YOUTUBE,
                superChat.author().name,
                superChat.message,
                superChat.amount.toDouble()
            ).callEvent()
            if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")
        }

        try {
            val userData= UserData(player)
            if (userData.getDonate().not()) return
            if (userData.getConfig().getBoolean("user.connection.youtube.isConnected").not()) return

            val channelName=userData.getConfig().getString("user.connection.youtube.display") ?: return

            val format=(ConfigSet.donation.donationFormat
                ?.replace("{msg}", superChat.message)
                ?.replace("{nick}", superChat.author().name)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", superChat.amount)
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle= ConfigSet.donation.showTitle
            val title=(ConfigSet.donation.titleFormat
                ?.replace("{msg}", superChat.message)
                ?.replace("{nick}", superChat.author().name)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", superChat.amount)
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

            val ec=(ConfigSet.donation.command(superChat.amount.toInt()) ?: return)
                .replace("{player}", player.name)
                .replace("{paid}", superChat.amount)
                .replace("{nick}", superChat.author().name)
                .replace("{msg}", superChat.message)
            player.performCommandAsOP(ec)
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getPlatformName(): String {
        return Platforms.YOUTUBE.generate()
    }
}