package kr.apo2073.onair.listeners.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.MessageTarget
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.events.StreamingDonateEvent
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
            val suc= StreamingDonateEvent(
                player, Platforms.TOONATION,
                donation.nickName,
                donation.comment,
                donation.amount.toDouble()
            ).callEvent()
            if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")
        }

        try {
            val userData= UserData(player)
            if (userData.getDonate().not()) return
            if (userData.getConfig().getBoolean("user.connection.toonation.isConnected").not()) return

            val channelName=userData.getConfig().getString("user.connection.toonation.display") ?: return

            val format=(ConfigSet.donation.donationFormat
                ?.replace("{msg}", donation.comment.replace("\"", ""))
                ?.replace("{nick}", donation.nickName.replace("\"", ""))
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", donation.amount.toString())
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle=ConfigSet.donation.showTitle
            val title=(ConfigSet.donation.titleFormat
                ?.replace("{msg}", donation.comment.replace("\"", ""))
                ?.replace("{nick}", donation.nickName.replace("\"", ""))
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", donation.amount.toString())
                ?.trim() ?: "{nick}: {msg}").toComponent()

            runTask {
                if (showTitle) player.showTitle(
                    Title.title("".toComponent(), title)
                )

                val target=userData.getMessageTarget()
                if (target== MessageTarget.STREAMER) player.sendMessage(format)
                else Bukkit.broadcast(format)

                val ec=(ConfigSet.donation.command(donation.amount.toInt()) ?: return@runTask)
                    .replace("{player}", player.name)
                    .replace("{nick}", donation.nickName.replace("\"", ""))
                    .replace("{paid}", donation.amount.toString())
                    .replace("{msg}", donation.comment.replace("\"", ""))
                player.performCommandAsOP(ec)
            }
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getPlatformName(): String {
        return Platforms.TOONATION.generate()
    }
}