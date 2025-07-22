package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import kr.apo2073.onAir.cmds.oa.OAHandler
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.utils.ConnectManager
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.toPlatform
import kr.apo2073.onAir.utils.Utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OACommand: Command(
    OnAir.plugin.config.getString("command.oa.name") ?: "oa",
    OnAir.plugin.config.getStringList("command.oa.aliases"),
    OnAir.plugin.config.getString("command.oa.description") ?: "OnAir 메인 커맨드",
    "apo.oa.channel"
) {
    private val plugin= OnAir.plugin

    override fun execute(
        sender: CommandSender,
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
            OAHandler(sender).viewConnection(userData)
            return true
        }

        if (args[0]=="설정") {
            if (args.size<3) {
                OAHandler(sender).help()
                return true
            }
            val setting=args[1]
            val value=args[2]
            var value1="none"
            if (args.size>=4) value1= args[3]
            OAHandler(sender).setSetting(setting, value, value1)
            return true
        }

        if (
            arrayOf("치지직","유튜브","투네이션").contains(args[0])
            && args.size>=2
        ) {
            val plat=args[0].toPlatform()
            val type=args[1] // 등록 / 등록해제

            if (type=="등록" && args.size>=4) {
                val channelName=args[2]
                val id=args[3]
                ConnectManager(sender).connect(plat, channelName, id)
            } else if (type=="등록해제") {
                ConnectManager(sender).disconnect(plat)
            } else OAHandler(sender).help()
            return true
        }
        OAHandler(sender).help()
        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        val tab=mutableListOf<String>()
        if (args.size==1) {
            tab.addAll(arrayOf(
                "치지직","유튜브","투네이션",
                "정보", "설정", "도움말"
            ))
            if (sender.hasPermission("apo.oa.donate")) tab.add("후원")
        }

        if (args.size==2) {
            if (args[0]=="설정") {
                tab.addAll(arrayOf(
                    "채팅알림", "후원알림",
                    "메세지대상", "채널이름"
                ))
            } else {
                tab.addAll(arrayOf(
                    "등록", "등록해제"
                ))
            }
        }

        if (args.size==3) {
            if (args[0]=="설정") {
                if (args[1]=="채널이름") tab.addAll(arrayOf("치지직", "유튜브", "투네이션"))
                else tab.addAll(arrayOf("값"))
            } else  {
                tab.addAll(arrayOf("채널이름"))
            }
        }

        if (args.size==4) {
            if (args[1]=="채널이름") tab.add("새채널이름")
            else tab.addAll(arrayOf("채널ID"))
        }
        return tab
    }
}