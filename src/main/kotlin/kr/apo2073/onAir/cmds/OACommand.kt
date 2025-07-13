package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.cmds.oa.OAHandler
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class OACommand: TabExecutor {
    private val plugin= OnAir.plugin

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("apo.oa.channel")) {
            sender.sendMessage(translate("command.oa.permission.no"), true)
            return true
        }
        if (args.isEmpty() || args[0]=="도움말") {
            OAHandler(sender).help()
            return true
        }
        val userData= UserData(sender)

        if (args[0]=="정보") {

            return true
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        val tab=mutableListOf<String>()
        if (args.isEmpty()) {
            tab.addAll(arrayOf(
                "치지직","유튜브","투네이션",
                "정보", "설정", "도움말"
            ))
            if (sender.hasPermission("apo.oa.donate")) tab.add("후원")
        }

        if (args.size==1) {
            tab.addAll(arrayOf(
                "등록", "등록해제"
            ))
        }
        return tab
    }
}