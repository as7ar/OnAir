package kr.apo2073.onAir.cmds.oa

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.data.ConnectionInfo
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.utils.Utils.generate
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.strUUID
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
            cnm = cnm
                .replace("{platform}", plat.generate())
                .replace("{connection-info}", ConnectionInfo.config.getString(player.strUUID()) ?: "(알 수 없음)")

            player.sendMessage(cnm, true)
        }
    }

    fun setSetting(setting: String, value: String) {
        // TODO: this
    }
}