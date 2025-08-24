package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.cmds.oa.OAdminHandler
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.performCommandAsOP
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
    private val plugin= OnAir.plugin

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("apo.oa.admin")) {
            sender.sendMessage(translate("command.oa.permission.no"), true)
            return true
        }
        plugin.reloadConfig()

        if (args.size>=2 && args[0]=="후원테스트") {
            val amount=args[1].toIntOrNull() ?: run {
                sender.sendMessage(translate("command.oadmin.invalid.value"), true)
                return true
            }
            OAdminHandler(sender).donationTest(amount)
            return true
        }

        if (args.size>=3) {
            if (args[0]=="후원") {
                val amount=args[1].toIntOrNull() ?: run {
                    sender.sendMessage(translate("command.oadmin.invalid.value"), true)
                    return true
                }
                val command=args.drop(2).joinToString(" ")
                Debugger.debug("setting '${amount}' command to '${command}'")
                OAdminHandler(sender).setDonateExecutor(amount, command)
                return true
            }
        } else OAdminHandler(sender).help()

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        val tab= mutableListOf<String>()
        if (args.size==1) {
            tab.addAll(Bukkit.getOnlinePlayers().map { it.name })
            tab.addAll(arrayOf("후원", "후원테스트"))
        }
        if (args.size==2) {
            if (args[0]=="후원" || args[0]=="후원테스트") {
                for (i in 1000..10000 step 1000) {
                    tab.add(i.toString())
                }
            } else {
                tab.addAll(arrayOf(
                    "채팅알림", "후원알림",
                    "메세지대상", "채널이름"
                ))
            }
        }
        if (args.size==3) {
            if (args[0]=="후원" || args[0]=="후원테스트") {
                tab.add("명령어")
            } else {
                if (args[1]=="채널이름") tab.addAll(arrayOf("치지직", "유튜브", "투네이션"))
                if (args[0]=="설정") tab.addAll(arrayOf("값"))
                if (args[1]=="채팅알림" || args[1]=="후원알림") tab.addAll(arrayOf("켜기", "끄기"))
            }
        }

        if (args.size==4) {
            if (args[1]=="채널이름") tab.add("새채널이름")
            if (args[0]=="후원" || args[0]=="후원테스트") tab.add("명령어")
        }
        return tab
    }
}