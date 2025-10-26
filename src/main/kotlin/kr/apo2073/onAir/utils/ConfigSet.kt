package kr.apo2073.onAir.utils

import kr.apo2073.onAir.OnAir

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

    val chatFormat= config.getString("채팅.형식")
    val emoticon= config.getString("채팅.이모티콘") ?: "&7(이모티콘)&f"
    val anon= config.getString("채팅.익명") ?: "(익명)"

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
}