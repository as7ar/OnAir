package kr.apo2073.onair.listeners.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.event.ChatContent
import kr.apo2073.onair.data.event.DonateContent
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.EventManager
import kr.apo2073.onair.utils.Utils.runTask
import kr.apo2073.onair.utils.Utils.toUUID
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.soopliv.data.Chat
import kr.apo2073.soopliv.data.Donate
import kr.apo2073.soopliv.soop.listener.SoopEventListener
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SoopListener: SoopEventListener() {
    private val plugin = OnAir.plugin
    private val eventManager= EventManager(Platforms.SOOP)

    private fun getPlayer(tag: String): Player? {
        val uuid_str=ConnectionManager.infoConfig.getString(tag)
        val uuid=uuid_str?.toUUID()

        if (uuid == null) {
            plugin.logger.warning(translate("system.boom", mapOf(
                "err" to "uuid parse error"
            )))
            return null
        }

        val player = Bukkit.getPlayer(uuid)
        if (player == null) {
            plugin.logger.warning(translate("system.boom", mapOf(
                "err" to "player not found or offline ($uuid)"
            )))
            return null
        }
        return player
    }

    override fun onChat(chat: Chat) {
        val player= getPlayer(chat.streamerTag) ?: return
        runTask {
            eventManager.onChat(ChatContent(
                player, chat.nickname, chat.message
            ))
        }
    }

    override fun onDonation(donate: Donate) {
        val player= getPlayer(donate.streamerTag) ?: return
        runTask {
            eventManager.onDonate(DonateContent(
                player, donate.donorNickname,
                donate.amount.toDouble(), donate.message
            ))
        }
    }
}