package kr.astar.onair.connector.platforms

import kr.astar.onair.OnAir
import kr.astar.onair.connector.AbstractConnector
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.platforms.ToonationListener
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Temp
import kr.astar.onair.utils.Utils.asynchronously
import kr.astar.onair.utils.Utils.runTask
import kr.astar.onair.utils.Utils.translate
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.api.toonLiv.Toonation
import kr.astar.api.toonLiv.ToonationBuilder
import kr.astar.api.toonLiv.exception.TokenNotFound
import org.bukkit.entity.Player

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

    override fun disconnect(player: Player) = disconnect(player, platform)
}