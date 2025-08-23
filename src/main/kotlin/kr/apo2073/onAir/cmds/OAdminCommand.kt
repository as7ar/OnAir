package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OAdminCommand: Command(
    OnAir.plugin.config.getString("command.oadmin.name") ?: "oa",
    OnAir.plugin.config.getStringList("command.oadmin.aliases"),
    OnAir.plugin.config.getString("command.oadmin.description") ?: "OnAir 관리자 명령어",
    "apo.oa.admin"
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("apo.oa.admin")) {
            sender.sendMessage(translate("command.oa.permission.no"), true)
            return true
        }

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        val tab= mutableListOf<String>()
        if (args.size==1) {
            tab.addAll(Bukkit.getOnlinePlayers().map { it.name })
        }
        return tab
    }
}