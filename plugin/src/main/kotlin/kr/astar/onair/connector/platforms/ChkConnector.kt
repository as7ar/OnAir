package kr.astar.onair.connector.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.connector.EventBinder
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
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
            val name = channel?.get()?.channelName ?: throw ChannelNotExistsException(
                "Cant find channel"
            )
            val fol = channel.get()?.followerCount.toString()

            user.connect(platform, id)
            player.sendMessage(translate("alert.connection.chzzk", mapOf(
                "name" to name,
                "fol" to fol
            )), true)
        } } catch (e: ChannelNotExistsException) {
            player.sendMessage(translate("alert.not.exist.channel",mapOf(
                "err" to (e.message ?: "0")
            )), true)
        }
    }

    override fun disconnect(player: Player) = disconnect(player, platform)
}