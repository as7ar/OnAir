package kr.apo2073.onAir.cmds.oa

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.MessageTarget
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.strUUID
import kr.apo2073.onAir.utils.Utils.toPlatform
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.entity.Player
import java.io.File

class OAHandler(private val player: Player) {
    private val plugin= OnAir.plugin

    fun help() {
        plugin.reloadConfig()
        val lang=plugin.config.getString("language") ?: "ko"
        val json= File("${plugin.dataFolder}/lang", "${lang}.json")
        val array=Gson().fromJson(json.readText(), JsonObject::class.java)
            .get("command.oa.help")
            .asJsonArray.mapNotNull {
                it.asString
                    .replace("{command}", plugin.config.getString("command.name") ?: "oa")
            }.toMutableList()
        array.forEach {
            player.sendMessage(it, true)
        }
    }

    fun viewConnection(userData: UserData) {
        val pl = Platforms.entries.toMutableList()
        val cn = userData.getConnections()

        for (plat in pl) {
            if (plat==Platforms.UNKNOWN) continue
            var cnm = translate("command.oa.connection.list")
            val regex = Regex("""\{(\w+)\s*\?\s*'(.*?)'\s*:\s*'(.*?)'}""")

            cnm = regex.replace(cnm) { match ->
                val condition = match.groupValues[1].trim()
                val trueValue = match.groupValues[2]
                val falseValue = match.groupValues[3]

                if (condition == "connection") {
                    val result = if (cn.contains(plat)) trueValue else falseValue
                    result
                } else {
                    match.value
                }
            }
//            println("${player.strUUID()}.${plat.name.lowercase()}")
            val info=ConnectionInfo.config.getString("${player.strUUID()}.${plat.name.lowercase()}")
                .also { println("Connection info: ${it ?: "NULL"}") }
            cnm = cnm.replace("{platform}", plat.generate())
                .replace("{connection-info}",
                    info ?: "(알 수 없음)"
                )

            player.sendMessage(cnm, true)
        }
    }

    fun setSetting(setting: String, value: String, value1:String="none") {
        val userdata=UserData(player)
        when(setting) {
            "채팅알림"-> {
                val set=!value.contains("끄기")
                userdata.setChat(set)
                player.sendMessage(translate("command.oa.setting.chat", mapOf(
                    "setting" to "&l&6채팅&r",
                    "value" to (if (set) "&a켜기&f" else "&c끄기&f")
                )), true)
            }
            "후원알림"-> {
                val set=!value.contains("끄기")
                userdata.setDonate(set)
                player.sendMessage(translate("command.oa.setting", mapOf(
                    "setting" to "&l&6방송&r",
                    "value" to (if (set) "&a켜기&f" else "&c끄기&f")
                )), true)
            }
            "메세지대상"-> {
                val set=
                    if (value.contains("스트리머만")) MessageTarget.STREAMER
                    else MessageTarget.EVERYONE
                userdata.setMessageTarget(set)
                player.sendMessage(translate("command.oa.setting", mapOf(
                    "setting" to "&l&6방송 알림 대상&r",
                    "value" to value
                )), true)
            }
            "채널이름"-> {
                val platforms=value.toPlatform()
                userdata.getConfig().apply {
                    set("user.connection.${platforms.name.lowercase()}.display", value1)
                }.save(userdata.getFile())
                player.sendMessage(translate("command.oa.setting", mapOf(
                    "setting" to "&l&6${value} 채널 이름&r",
                    "value" to value1
                )), true)
            }
        }
    }
}