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

        when(action) {
            "등록" -> {
                if(args.size < 4) { handler.help(); return true }
                val plat = args[1].toPlatform()
                val channelName = args[2]
                val id = args[3]
                steamer.connect(plat, channelName, id)
            }

            "등록해제" -> {
                if(args.size < 2) { handler.help(); return true }
                val plat = args[1].toPlatform()
                steamer.disconnect(plat)
            }

            "정보" -> handler.viewConnection(userData)

            "설정" -> {
                if(args.size < 3) { handler.help(); return true }
                val setting = args[1]
                val value = args[2]
                val value1 = if(args.size >=4) args[3] else "none"
                handler.setSetting(setting, value, value1)
            }

            "도움말" -> handler.help()
            else -> handler.help()
        }

        return true
    }

    override fun tabComplete(

        sender: CommandSender,

        args: Array<out String>

    ): List<String> {
        val tab = mutableListOf<String>()

        val plats=ConfigSet.plats
        val args1=arrayOf("등록","등록해제","정보","설정","도움말")
        val args2=arrayOf("채팅알림", "후원알림", "메세지대상", "채널이름")
        val args3=arrayOf("켜기", "끄기")
        val args4=arrayOf("스트리머만", "전체")

        if(args.size == 1) tab.addAll(args1)
        if(args.size == 2) {
            when(args[0]) {
                args1[0],args1[1] -> tab.addAll(plats)
                args1[3] -> tab.addAll(args2)
            }
        }
        if(args.size == 3) {
            if(args[0] == args1[0]) tab.add("채널명")
            if(args[0] == args1[3]) {
                when(args[1]) {
                    "채널이름" -> tab.addAll(plats)
                    "채팅알림", "후원알림" -> tab.addAll(args3)
                    "메세지대상" -> tab.addAll(args4)
                    else -> tab.add("값")
                }
            }
        }
        if(args.size == 4) {
            if(args[0] == "등록") tab.add("ID")
            if(args[1] == "채널이름") tab.add("새채널이름")
        }
        return tab
    }
}
