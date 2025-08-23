package kr.apo2073.onAir.cmds.oa

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.utils.Utils.sendMessage
import org.bukkit.entity.Player
import java.io.File

class OAdminHandler(private val player: Player) {
    private val plugin= OnAir.plugin

    fun help() {
        plugin.reloadConfig()
        val lang=plugin.config.getString("language") ?: "ko"
        val json= File("${plugin.dataFolder}/lang", "${lang}.json")
        val array=Gson().fromJson(json.readText(), JsonObject::class.java)
            .get("command.oadmin.help")
            .asJsonArray.mapNotNull {
                it.asString
                    .replace("{command}", plugin.config.getString("command.oadmin.name") ?: "oadmin")
            }.toMutableList()
        array.forEach {
            player.sendMessage(it, true)
        }
    }
}