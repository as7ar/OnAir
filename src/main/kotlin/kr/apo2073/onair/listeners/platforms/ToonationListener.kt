package kr.apo2073.onair.listeners.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.data.event.DonateContent
import kr.apo2073.onair.enums.MessageTarget
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.StreamingDonateEvent
import kr.apo2073.onair.listeners.EventManager
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Utils.generate
import kr.apo2073.onair.utils.Utils.performCommandAsOP
import kr.apo2073.onair.utils.Utils.runTask
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.toUUID
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.onair.utils.toComponent
import kr.apo2073.toonLiv.data.Donation
import kr.apo2073.toonLiv.listener.ToonationEventListener
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

class ToonationListener: ToonationEventListener {
    private val plugin= OnAir.plugin
    override fun onDonation(donation: Donation) {
        plugin.reloadConfig()
        val cic= ConnectionManager.infoConfig
        val uuid=cic.getString(donation.alertBox)?.toUUID() ?: run {
            plugin.logger.warning(translate(
                "system.boom", mapOf("err" to "uuid parse error (${cic.getString(donation.alertBox)})")
            ))
            return
        }
        val player= Bukkit.getPlayer(uuid) ?: run {
            plugin.logger.warning(translate(
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