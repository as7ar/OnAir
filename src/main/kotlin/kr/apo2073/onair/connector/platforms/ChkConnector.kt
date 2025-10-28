package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.connector.EventBinder
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import org.bukkit.entity.Player
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder
import xyz.r2turntrue.chzzk4j.exception.ChannelNotExistsException

class ChkConnector: AbstractConnector(Platforms.CHZZK) {
    override fun connect(player: Player, id: String) = safeRun(player) { try {
        withUserData(player) { user, config ->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first = config.getBoolean("user.connection.chzzk.first", true)

            val chat = OnAir.cht[player.uniqueId]?.apply {
                closeBlocking()
                closeAsync()
            }

            val cht = ChzzkChatBuilder(OnAir.chzzkClient, id).build().apply {
                connectBlocking()
            }

            OnAir.cht[player.uniqueId] =
                if (first) EventBinder.bindChzzkEvents(cht, player)
                else cht

            val channel = OnAir.chzzkClient.fetchChannel(id)
            val name = channel.channelName
            val fol = channel.followerCount.toString()

            user.connect(platform, id)
            player.sendMessage(translate("alert.connection.chzzk", mapOf(
                "name" to name,
                "fol" to fol
            )))
        } } catch (e: ChannelNotExistsException) {
            player.sendMessage(translate("alert.not.exist.channel",mapOf(
                "err" to (e.message ?: "0")
            )), true)
        }
    }

    override fun disconnect(player: Player) = safeRun(player) {
        val id = ConnectionManager.infoConfig
            .getString("${player.uniqueId}.chzzk") ?: return@safeRun
        OnAir.cht[player.uniqueId]?.closeBlocking()
        OnAir.cht.remove(player.uniqueId)
        UserData(player).disconnect(platform, id)
        player.sendMessage(translate("alert.disconnect"))
    }
}