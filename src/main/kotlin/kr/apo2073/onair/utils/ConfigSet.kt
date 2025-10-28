package kr.apo2073.onair.utils

import kr.apo2073.onair.OnAir

object ConfigSet {
    private val plugin = OnAir.plugin
    private val config= plugin.config

    val prefix= config.getString("prefix") ?: "<b><gradient:#E7B0B0:#BA4242>[ OnAir ]</gradient></b> "
    object Command {
        object oa {
            val name = config.getString("command.oa.name") ?: "oa"
            val aliases = config.getStringList("command.oa.aliases")
            val description = config.getString("command.oa.description") ?: "OnAir 메인 명령어"
        }

        object oadmin {
            val name = config.getString("command.oadmin.name") ?: "oadmin"
            val aliases = config.getStringList("command.oadmin.aliases")
            val description = config.getString("command.oadmin.description") ?: "OnAir 관리자 명령어"
        }
    }

    object TabComplete {
        object oa {
            val args_1= config.getStringList("tab-complete.oa.1")
            val args_2= config.getStringList("tab-complete.oa.2")
            val args_3= config.getStringList("tab-complete.oa.3")
            val args_4= config.getStringList("tab-complete.oa.4")
            val args_5= config.getString("tab-complete.oa.5") ?: "채널명"
            val args_6= config.getString("tab-complete.oa.6") ?: "ID"
            val args_7= config.getString("tab-complete.oa.7") ?: "새채널이름"
        }
    }

    val chatFormat= config.getString("채팅.형식")
    val emoticon= config.getString("채팅.이모티콘") ?: "&7(이모티콘)&f"
    val anon= config.getString("채팅.익명") ?: "(익명)"
    val plats= config.getStringList("platforms")

    object donation {
        val donationFormat= config.getString("후원.형식")
        val showTitle= config.getBoolean("후원.타이틀표시", true)
        val titleFormat= config.getString("후원.타이틀형식")
        fun command(amount: Int): String? {
            return config.getString("후원이벤트.$amount")
        }
    }

    val debug= plugin.config.getBoolean("debug", false)
    val colored= plugin.config.getBoolean("플렛폼.채색")
    val en = plugin.config.getBoolean("플렛폼.영어")
    val balloon= plugin.config.getBoolean("soop.donation-balloon", false)
}