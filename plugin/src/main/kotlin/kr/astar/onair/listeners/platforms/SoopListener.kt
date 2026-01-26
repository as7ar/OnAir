package kr.astar.onair.listeners.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.api.soopliv.data.Chat
import kr.astar.onair.api.soopliv.data.Donate
import kr.astar.onair.api.soopliv.soop.listener.SoopEventListener
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.event.ChatContent
import kr.astar.onair.data.event.DonateContent
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.EventManager
import kr.astar.onair.utils.Utils.runTask
import kr.astar.onair.utils.Utils.toUUID
import kr.astar.onair.utils.Utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SoopListener: SoopEventListener() {
    private val plugin = OnAir.plugin
    private val eventManager= EventManager(Platforms.SOOP)

    private fun getPlayer(tag: String): Player? {
        val uuid_str=ConnectionManager.infoConfig.getString(tag)
        val uuid=uuid_str?.toUUID()

        if (uuid == null) {
            plugin.log.warning(translate("system.boom", mapOf(
                "err" to "uuid parse error"
            )))
            return null
        }

        val player = Bukkit.getPlayer(uuid)
        if (player == null) {
            plugin.log.warning(translate("system.boom", mapOf(
                "err" to "player not found or offline ($uuid)"
            )))
            return null
        }
        return player
    }

    override fun onChat(chat: Chat) {
//        println(chat.streamerTag)
        val player= getPlayer(chat.streamerTag) ?: return
        runTask {
            eventManager.onChat(ChatContent(
                player, chat.user.nickname, chat.message
            ))
        }
    }

    override fun onDonation(donate: Donate) {
        val player= getPlayer(donate.streamerTag) ?: return
        runTask {
            eventManager.onDonate(DonateContent(
                player, donate.donator.nickname,
                donate.amount.toDouble(), donate.message
            ))
        }
    }
}