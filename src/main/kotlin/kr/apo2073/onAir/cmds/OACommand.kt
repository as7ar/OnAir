package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.cmds.oa.OAHandler
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.player.Streamer
import kr.apo2073.onAir.utils.ConfigSet
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.toPlatform
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OACommand: Command(
    ConfigSet.Command.oa.name,
    ConfigSet.Command.oa.aliases,
    ConfigSet.Command.oa.description,
    "apo.oa.channel"
) {
    private val plugin= OnAir.plugin

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
        if (args.isEmpty() || args[0]=="도움말") {
            handler.help()
            return true
        }
        val userData= UserData(sender)

        if (args[0]=="정보") {
            handler.viewConnection(userData)
            return true
        }

        if (args[0]=="설정") {
            if (args.size<3) {
                handler.help()
                return true
            }
            val setting=args[1]
            val value=args[2]
            var value1="none"
            if (args.size>=4) value1= args[3]
            handler.setSetting(setting, value, value1)
            return true
        }

        if (
            arrayOf("치지직","유튜브","투네이션","숲","트위치").contains(args[0])
            && args.size>=2
        ) {
            val plat=args[0].toPlatform()
            val type=args[1] // ( 등록 / 등록해제 )

            when (type) {
                "등록"  -> {
                    if (args.size >= 4) {
                        val channelName = args[2]
                        val id = args[3]
                        steamer.connect(plat, channelName, id)
                    }
                }
                "등록해제" -> steamer.disconnect(plat)
                else -> handler.help()
            }
            return true
        }
        handler.help()
        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        val tab = mutableListOf<String>()
        if (args.size == 1) {
            tab.addAll(arrayOf("치지직","유튜브","투네이션"/*, "숲"*/, "정보", "설정", "도움말"))
        }

        if (args.size == 2) {
            if (args[0] == "설정") {
                tab.addAll(arrayOf("채팅알림", "후원알림", "메세지대상", "채널이름"))
            } else {
                tab.addAll(arrayOf("등록", "등록해제"))
            }
        }

        if (args.size == 3) {
            if (args[0] == "설정") {
                when (args[1]) {
                    "채널이름" -> tab.addAll(arrayOf("치지직","유튜브","투네이션"))
                    "채팅알림", "후원알림" -> tab.addAll(arrayOf("켜기", "끄기"))
                    "메세지대상" -> tab.addAll(arrayOf("스트리머만", "전체"))
                    else -> tab.add("값")
                }
            } else {
                tab.add("채널이름")
            }
        }

        if (args.size == 4) {
            if (args[1] == "채널이름") tab.add("새채널이름")
            else tab.add("채널ID")
        }
        return tab
    }
}