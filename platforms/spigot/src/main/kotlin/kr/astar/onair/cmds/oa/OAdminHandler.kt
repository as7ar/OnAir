package kr.astar.onair.cmds.oa

import com.google.gson.Gson
import com.google.gson.JsonObject
import kr.astar.onair.OnAir
import kr.astar.onair.utils.Utils.performCommandAsOP
import kr.astar.onair.utils.Utils.reset
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
import kr.astar.onair.utils.toComponent
import kr.astar.onair.utils.toLegacyString
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.title.Title
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
                it.asString.replace(
                    "{command}",
                    plugin.config.getString("command.oadmin.name") ?: "oadmin"
                )
            }.toMutableList()
        array.forEach {
            player.sendMessage(it, true)
        }
    }

    fun modifySetting(target: Player, setting: String, value: String, value1: String="none") {
        val handler= OAHandler(target)
        handler.setSetting(setting, value, value1)

    }

    fun setDonateExecutor(amount: Int, command: String) {
        plugin.reloadConfig()
        plugin.config.set("후원이벤트.${amount}", command)
        plugin.saveConfig()
        player.sendMessage(translate("command.oadmin.donate", mapOf(
            "amount" to amount.toString(),
            "command" to command
        )), true)
    }

    fun donationTest(amount: Int, target: Player = player) {
        val msg = "test"
        val nick = "(anon)"
        val plat = "&cTEST&f"
        val ch = "&7UNKNOWN&f"
        val emoji = translate("alert.replaced.emoji")

        val format = (plugin.config.getString("후원.형식")
            ?.replace("{msg}", msg)
            ?.replace("{nick}", nick)
            ?.replace("{plat}", plat)
            ?.replace("{ch}", ch)
            ?.replace("{paid}", amount.toString())
            ?.replace(Regex("\\{[^}]*}"), emoji)
            ?.trim() ?: "{nick}: {msg}").toComponent()

        val showTitle = plugin.config.getBoolean("후원.타이틀표시", true)
        val title = (plugin.config.getString("후원.타이틀형식")
            ?.replace("{msg}", msg)
            ?.replace("{nick}", nick)
            ?.replace("{plat}", plat)
            ?.replace("{ch}", ch)
            ?.replace("{paid}", amount.toString())
            ?.replace(Regex("\\{[^}]*}"), emoji)
            ?.trim() ?: "{nick}: {msg}").toComponent()

        if (showTitle) target.sendTitle("", title.toLegacyString())

        target.sendMessage(format)

        val ec = (plugin.config.getString("후원이벤트.$amount") ?: return)
            .replace("{player}", target.name)
            .replace("{nick}", nick)
            .replace("{paid}", "$amount")
            .replace("{msg}", msg)
        target.performCommandAsOP(ec)
    }

    fun reset() {
        player.sendMessage(
            translate("command.oadmin.reset.check").toComponent()
                .append(translate("command.check.yes").toComponent()
                    .clickEvent(ClickEvent.callback { aud: Audience ->
                        plugin.reset(false)

                        fun deleteRecursively(file: File) {
                            if (file.isDirectory) file.listFiles()?.forEach { deleteRecursively(it) }
                            file.delete()
                        }
                        val doneMessage=translate("command.oadmin.reset.done")

                        val file= plugin.dataFolder
                        deleteRecursively(file)

                        player.sendMessage(doneMessage, true)
                        plugin.server.pluginManager.disablePlugin(plugin)
                    }))
                .append(translate("command.check.no").toComponent()
                    .clickEvent(ClickEvent.callback {
                        player.sendMessage(translate("command.oadmin.reset.cancel"), true)
                    }))
            , true
        )
    }
}