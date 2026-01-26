package kr.astar.onair.listeners.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.api.weflabLiv.data.alert.Donation
import kr.astar.onair.api.weflabLiv.listener.WeflabListener
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.event.DonateContent
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.EventManager
import kr.astar.onair.utils.Utils.runTask
import kr.astar.onair.utils.Utils.toUUID
import kr.astar.onair.utils.Utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class WeflabListener: WeflabListener {
    private val plugin = OnAir.plugin
    private val eventManager= EventManager(Platforms.WEFLAB)

    private fun getStreamer(id: String): Player? {
        val uuid_str=ConnectionManager.infoConfig.getString(id) ?: run {
            plugin.log.warning(translate("system.boom", mapOf(
                "err" to "uuid parse error"
            )))
            return null
        }
        val uuid = uuid_str.toUUID()

        val player = Bukkit.getPlayer(uuid) ?: run {
            plugin.log.warning(translate("system.boom", mapOf(
                "err" to "player not found or offline ($uuid)"
            )))
            return null
        }

        return player
    }

    override fun onDonation(donation: Donation) {
        val player = getStreamer(donation.donationData.idx) ?: return
        runTask {
            eventManager.onDonate(DonateContent(
                player, donation.user.nickname,
                donation.amount.toDouble(), donation.content
            ))
        }
    }
}