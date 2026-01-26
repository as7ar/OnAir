package kr.astar.onair.cmds

import kr.astar.onair.OnAir
import kr.astar.onair.cmds.oa.OAdminHandler
import kr.astar.onair.utils.ConfigSet
import kr.astar.onair.utils.Debugger
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.Utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OAdminCommand: Command(
    ConfigSet.Command.oadmin.name,
    ConfigSet.Command.oadmin.aliases,
    ConfigSet.Command.oadmin.description,
    "astar.oa.admin"
) {
    private val plugin = OnAir.plugin

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("apo.oa.admin")) {
            sender.sendMessage(translate("command.oa.permission.no"), true)
            return true
        }
        plugin.reloadConfig()
        val handler=OAdminHandler(sender)

        if (args.isEmpty()) {
            handler.help()
            return true
        }

        if (args[0]=="플러그인초기화") {
            try {
                handler.reset()
            } catch (e: Exception) {
                sender.sendMessage(translate(
                    "command.got.problems",
                    mapOf("err" to (e.message ?: "0"))
                ), true)
                e.printStackTrace()
            }
            return true
        }

        if (args.size >= 2 && args[0] == "후원테스트") {
            val amount = args[1].toIntOrNull() ?: run {
                sender.sendMessage(translate("command.invalid.value"), true)
                return true
            }
            val target = if (args.size >= 3) {
                Bukkit.getPlayer(args[2]) ?: run {
                    sender.sendMessage(translate("command.invalid.player"), true)
                    return true
                }
            } else sender

            handler.donationTest(amount, target)
            return true
        }

        if (args.size>=3) {
            if (args[0]=="후원") {
                val amount=args[1].toIntOrNull() ?: run {
                    sender.sendMessage(translate("command.invalid.value"), true)
                    return true
                }
                val command=args.drop(2).joinToString(" ")
                Debugger.debug("setting '${amount}' command to '${command}'")
                handler.setDonateExecutor(amount, command)
                return true
            }
        } else OAdminHandler(sender).help()

        if (args.size >= 4 && args[0] != "후원") {
            val target = Bukkit.getPlayer(args[0]) ?: run {
                sender.sendMessage(translate("command.oadmin.invalid.player"), true)
                return true
            }
            val setting = args[1]
            val value = args[2]
            val value1 = if (args.size >= 5) args[4] else "none"

            handler.modifySetting(target, setting, value, value1)
            return true
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        val tab = mutableListOf<String>()
        if (args.size == 1) {
            tab.addAll(Bukkit.getOnlinePlayers().map { it.name })
            tab.addAll(arrayOf("후원", "후원테스트", "플러그인초기화"))
        }
        if (args.size == 2) {
            if (args[0] == "후원" || args[0] == "후원테스트") {
                for (i in 1000..10000 step 1000) {
                    tab.add(i.toString())
                }
            } else {
                tab.addAll(arrayOf("채팅알림", "후원알림", "메세지대상", "채널이름"))
            }
        }
        if (args.size == 3) {
            when {
                args[0] == "후원" || args[0] == "후원테스트" -> {
                    tab.add("명령어")
                }
                args[1] == "채널이름" -> {
                    tab.addAll(arrayOf("치지직","유튜브","투네이션","숲", "트위치"))
                }
                args[1] == "채팅알림" || args[1] == "후원알림" -> {
                    tab.addAll(arrayOf("켜기", "끄기"))
                }
                args[1] == "메세지대상" -> {
                    tab.addAll(arrayOf("스트리머만", "전체"))
                }
                else -> tab.add("값")
            }
        }

        if (args.size == 4) {
            if (args[1] == "채널이름") tab.add("새채널이름")
            if (args[0] == "후원테스트") tab.addAll(Bukkit.getOnlinePlayers().map { it.name })
        }
        return tab
    }
}