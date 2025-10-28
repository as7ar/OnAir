package kr.apo2073.onair.connector.platforms

import kr.apo2073.onair.OnAir
import kr.apo2073.onair.connector.AbstractConnector
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.platforms.ToonationListener
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Temp
import kr.apo2073.onair.utils.Utils.asynchronously
import kr.apo2073.onair.utils.Utils.runTask
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.toonLiv.Toonation
import kr.apo2073.toonLiv.ToonationBuilder
import kr.apo2073.toonLiv.exception.TokenNotFound
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class TnConnector(
    private val display:String
): AbstractConnector(Platforms.TOONATION) {

    override fun connect(player: Player, id: String) = safeRun(player) { try {
        withUserData(player) { user, config->
            if (!ConnectionManager.connectionCheck(player, id)) return@withUserData

            val first=config.getBoolean("user.connection.toonation.first", true)

            val builder= ToonationBuilder()
                .setDebug(ConfigSet.debug)
                .setKey(id)

            OnAir.tn[player.uniqueId] =
                if (first) builder.addListener(ToonationListener()).build()
                else builder.build()

            asynchronously {
                val channel: String? = Temp.getTempAsString(display) ?: run {
                    val streamer = Toonation.getStreamerAsync(display).get()
                    val name = streamer?.nickname
                    if (name != null) Temp.addTemp(display, name)
                    name
                }

                runTask {
                    if (channel==null) {
                        player.sendMessage(translate("command.oa.connection.tn.channelid"),true)
                        return@runTask
                    }

                    user.connect(platform, id, channel)

                    player.sendMessage(translate(
                        "alert.connection.toonation", mapOf(
                            "name" to channel,)),
                        true
                    )
                }
            }
        }
    } catch (e: TokenNotFound) {
        player.sendMessage(translate("alert.not.exist.channel"), true)
        return@safeRun
    } }

    override fun disconnect(player: Player) = safeRun(player) {
        val id= ConnectionManager.infoConfig
            .getString("${player.uniqueId}.toonation") ?: return@safeRun

        OnAir.tn[player.uniqueId]?.close()
        OnAir.tn.remove(player.uniqueId)

        UserData(player).disconnect(platform, id)
        player.sendMessage(translate("alert.disconnect"), true)
    }
}