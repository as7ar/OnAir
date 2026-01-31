package kr.astar.onair.listeners.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.event.ChatContent
import kr.astar.onair.data.event.DonateContent
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.EventManager
import kr.astar.onair.utils.Debugger
import kr.astar.onair.utils.Utils.runTask
import kr.astar.onair.utils.Utils.toUUID
import kr.astar.onair.utils.Utils.translate
import kr.astar.api.utubeLiv.data.Chatting
import kr.astar.api.utubeLiv.data.SuperChat
import kr.astar.api.utubeLiv.listener.YouTubeEventListener
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class YoutubeListener : YouTubeEventListener {

    private val plugin = OnAir.plugin
    private val eventManager = EventManager(Platforms.YOUTUBE)

    private fun getPlayerByVideoId(videoId: String): Player? {
        val uuidStr = ConnectionManager.infoConfig.getString(videoId)
        val uuid = uuidStr?.toUUID()
        if (uuid == null) {
            plugin.logger.warning(translate("system.boom", mapOf("err" to "uuid parse error ($uuidStr)")))
            return null
        }

        val player = Bukkit.getPlayer(uuid)
        if (player == null) {
            plugin.logger.warning(translate("system.boom", mapOf("err" to "player not found or offline ($uuid)")))
            return null
        }

        Debugger.debug("Player loaded: ${player.name}")
        return player
    }

    override fun onChat(chat: Chatting) {
        plugin.reloadConfig()
        val player = getPlayerByVideoId(chat.videoId) ?: return

        runTask {
            eventManager.onChat(ChatContent(
                player, chat.author().name, chat.message
            ))
        }
    }

    override fun onSuperChat(superChat: SuperChat) {
        plugin.reloadConfig()
        val player = getPlayerByVideoId(superChat.videoId) ?: return
        val amount = superChat.amount.toDoubleOrNull() ?: return

        runTask {
            eventManager.onDonate(DonateContent(
                player, superChat.author().name,
                amount, superChat.message
            ))
        }
    }
}