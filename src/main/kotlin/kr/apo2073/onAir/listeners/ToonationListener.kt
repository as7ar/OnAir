package kr.apo2073.onAir.listeners

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.StreamingDonateEvent
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.performCommandAsOP
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.toUUID
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.onAir.utils.toComponent
import kr.apo2073.tnliv.data.Donation
import kr.apo2073.tnliv.listener.ToonationEventListener
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

class ToonationListener: ToonationEventListener {
    private val plugin= OnAir.plugin
    override fun onDonation(donation: Donation) {
        plugin.reloadConfig()
        val cic= ConnectionManager.infoConfig
        val uuid=cic.getString(donation.alertBox)?.toUUID() ?: run {
            plugin.logger.warning(translate(
                "system.boom",
                mapOf("err" to "uuid parse error (${cic.getString(donation.alertBox)})")
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

        val suc= StreamingDonateEvent(
            player, Platforms.TOONATION,
            donation.nickName,
            donation.comment,
            donation.amount.toDouble()
        ).callEvent()
        if (!suc) plugin.logger.warning("StreamingDonateEvent를 처리하던 중 오류가 발생했습니다")

        try {
            val userData= UserData(player)
            if (userData.getDonate().not()) return
            if (userData.getConfig().getBoolean("user.connection.toonation.isConnected").not()) return

            val channelName=userData.getConfig().getString("user.connection.toonation.display") ?: return

            val format=(plugin.config.getString("후원.형식")
                ?.replace("{msg}", donation.comment.replace("\"", ""))
                ?.replace("{nick}", donation.nickName.replace("\"", ""))
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", donation.amount.toString())
                ?.trim() ?: "{nick}: {msg}").toComponent()

            val showTitle=plugin.config.getBoolean("후원.타이틀표시", true)
            val title=(plugin.config.getString("후원.타이틀형식")
                ?.replace("{msg}", donation.comment.replace("\"", ""))
                ?.replace("{nick}", donation.nickName.replace("\"", ""))
                ?.replace("{plat}", getPlatformName())
                ?.replace("{ch}", channelName)
                ?.replace("{paid}", donation.amount.toString())
                ?.trim() ?: "{nick}: {msg}").toComponent()

            if (showTitle) player.showTitle(
                Title.title("".toComponent(), title)
            )

            val target=userData.getMessageTarget()
            if (target== MessageTarget.STREAMER) player.sendMessage(format)
            else Bukkit.broadcast(format)

            val ec=(plugin.config.getString("후원이벤트.${donation.amount.toInt()}") ?: return)
                .replace("{player}", player.name)
                .replace("{nick}", donation.nickName.replace("\"", ""))
                .replace("{paid}", donation.amount.toString())
                .replace("{msg}", donation.comment.replace("\"", ""))
            player.performCommandAsOP(ec)
        } catch (e: Exception) {
            player.sendMessage(translate("system.boom"), true)
            e.printStackTrace()
        }
    }

    private fun getPlatformName(): String {
        return Platforms.TOONATION.generate()
    }
}