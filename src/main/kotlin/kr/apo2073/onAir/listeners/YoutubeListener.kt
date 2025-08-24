package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.StreamingChatEvent
import kr.apo2073.onAir.events.StreamingDonateEvent
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.performCommandAsOP
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.toUUID
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.onAir.utils.toComponent
import kr.apo2073.ytliv.data.Chatting
import kr.apo2073.ytliv.data.SuperChat
import kr.apo2073.ytliv.listener.YouTubeEventListener
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

class YoutubeListener: YouTubeEventListener {
    private val plugin = OnAir.plugin

    override fun onChat(chat: Chatting) {
        val cic= ConnectionManager.infoConfig
        Debugger.debug("Youtube Chat Event Called")
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

            Debugger.debug("Formatting chatting")
            val userData= UserData(player)
            val format=(plugin.config.getString("채팅.형식")
                ?.replace("{msg}", chat.message)
                ?.replace("{nick}", chat.author().name)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace(Regex(":[^:]+:"), "&7(이모티콘)&f")
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) {
                Debugger.debug("Sending message to streamer")
                player.sendMessage(format)
            } else {
                Debugger.debug("Broadcasting message")
                Bukkit.broadcast(format)
            }

            Bukkit.getScheduler().runTask(plugin, Runnable {
                Debugger.debug("Calling StreamingChatEvent")
                val suc= StreamingChatEvent(
                    player, Platforms.YOUTUBE,
                    chat.author().name,
                    chat.message
                ).callEvent()
                if (!suc) plugin.logger.warning("StreamingChatEvent를 처리하던 중 오류가 발생했습니다")
            })

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

        try {
            val userData= UserData(player)
            if (userData.getDonate().not()) return
            if (userData.getConfig().getBoolean("user.connection.youtube.isConnected").not()) return

            val channelName=userData.getConfig().getString("user.connection.youtube.display") ?: return

            val format=(plugin.config.getString("후원.형식")
                ?.replace("{msg}", superChat.message)
                ?.replace("{nick}", superChat.author().name)
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", superChat.amount)
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle=plugin.config.getBoolean("후원.타이틀표시", true)
            val title=(plugin.config.getString("후원.타이틀형식")
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

            val ec=(plugin.config.getString("${superChat.amount}") ?: return)
                .replace("{player}", player.name)
                .replace("{paid}", superChat.amount)
                .replace("{nick}", superChat.author().name)
                .replace("{msg}", superChat.message)
            player.performCommandAsOP(ec)

            Bukkit.getScheduler().runTask(plugin, Runnable {
                val suc= StreamingDonateEvent(
                    player, Platforms.YOUTUBE,
                    superChat.author().name,
                    superChat.message,
                    superChat.amount.toDouble()
                ).callEvent()
                if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")
            })
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getPlatformName(): String {
        return Platforms.YOUTUBE.generate()
    }
}