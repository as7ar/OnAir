package kr.astar.onair.listeners.platforms

import kr.astar.onair.api.toonLiv.data.Donation
import kr.astar.onair.api.toonLiv.listener.ToonationEventListener
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.event.DonateContent
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.EventManager
import kr.astar.onair.OnAir
import kr.astar.onair.utils.Utils.runTask
import kr.astar.onair.utils.Utils.toUUID
import kr.astar.onair.utils.Utils.translate
import org.bukkit.Bukkit

class ToonationListener: ToonationEventListener {
    private val plugin = OnAir.plugin
    override fun onDonation(donation: Donation) {
        plugin.reloadConfig()
        val cic= ConnectionManager.infoConfig
        val uuid=cic.getString(donation.alertBox)?.toUUID() ?: run {
            plugin.log.warning(translate(
                "system.boom", mapOf("err" to "uuid parse error (${cic.getString(donation.alertBox)})")
            ))
            return
        }
        val player= Bukkit.getPlayer(uuid) ?: run {
            plugin.log.warning(translate(
                "system.boom", mapOf("err" to "current donator's player is not exist or offline")
            ))
            return
        }

        runTask {
            EventManager(Platforms.TOONATION).onDonate(DonateContent(
                player, donation.nickName,
                donation.amount.toDouble(), donation.comment
            ))
        }
    }
}