package kr.apo2073.onair.cmds

import kr.apo2073.onair.cmds.oa.OAHandler
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.player.Streamer
import kr.apo2073.onair.utils.ConfigSet
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.toPlatform
import kr.apo2073.onair.utils.Utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OACommand: Command(
    ConfigSet.Command.oa.name,
    ConfigSet.Command.oa.aliases,
    ConfigSet.Command.oa.description,
    "apo.oa.channel"
) {

    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        val steamer= Streamer(sender)
        val handler= OAHandler(sender)
        if (!sender.hasPermission("apo.oa.channel")) {
            sender.sendMessage(translate("command.oa.permission.no"), true)
            return true
        }
        if (args.isEmpty()) {
            handler.help()
            return true
        }

        val userData= UserData(sender)
        val action = args[0] // 등록 / 등록해제 / 정보 / 설정 / 도움말

        val args1=ConfigSet.TabComplete.oa.args_1

        when(action) {
            args1[0] -> {
                if(args.size < 4) { handler.help(); return true }
                val plat = args[1].toPlatform()
                val channelName = args[2]
                val id = args[3]
                steamer.connect(plat, channelName, id)
            }

            args1[1] -> {
                if(args.size < 2) { handler.help(); return true }
                val plat = args[1].toPlatform()
                steamer.disconnect(plat)
            }

            args1[2] -> handler.viewConnection(userData)

            args1[3] -> {
                if(args.size < 3) { handler.help(); return true }
                val setting = args[1]
                val value = args[2]
                val value1 = if(args.size >=4) args[3] else "none"
                handler.setSetting(setting, value, value1)
            }

            args1[4] -> handler.help()
            else -> handler.help()
        }

        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> { //todo: YEA
        val tab = mutableListOf<String>()
        val plats=ConfigSet.plats
        val args1=ConfigSet.TabComplete.oa.args_1
        val args2=ConfigSet.TabComplete.oa.args_2
        val args3=ConfigSet.TabComplete.oa.args_3
        val args4=ConfigSet.TabComplete.oa.args_4

        if(args.size == 1) tab.addAll(args1)
        if(args.size == 2) {
            when(args[0]) {
                args1[0],args1[1] -> tab.addAll(plats)
                args1[3] -> tab.addAll(args2)
            }
        }
        if(args.size == 3) {
            if(args[0] == args1[0]) tab.add(ConfigSet.TabComplete.oa.args_5)
            if(args[0] == args1[3]) {
                when(args[1]) {
                    args2[3] -> tab.addAll(plats)
                    args2[0], args2[1] -> tab.addAll(args3)
                    args2[2] -> tab.addAll(args4)
                    else -> tab.add(ConfigSet.TabComplete.oa.args_8)
                }
            }
        }
        if(args.size == 4) {
            if(args[0] == args1[0]) tab.add(ConfigSet.TabComplete.oa.args_6)
            if(args[1] == args2[3]) tab.add(ConfigSet.TabComplete.oa.args_7)
        }
        return tab
    }
}
