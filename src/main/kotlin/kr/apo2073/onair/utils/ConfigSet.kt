package kr.apo2073.onair.utils

import kr.apo2073.onair.OnAir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ConfigSet {
    private val plugin = OnAir.plugin
    private val config = plugin.config
    private val commandFile = File(plugin.dataFolder, "command.yml")
    private val cConfig
        get() = YamlConfiguration.loadConfiguration(commandFile)

    val prefix= cConfig.getString("prefix") ?: "<b><gradient:#E7B0B0:#BA4242>[ OnAir ]</gradient></b> "
    object Command {
        object oa {
            val name = cConfig.getString("command.oa.name") ?: "oa"
            val aliases = cConfig.getStringList("command.oa.aliases")
            val description = cConfig.getString("command.oa.description") ?: "OnAir 메인 명령어"
        }

        object oadmin {
            val name = cConfig.getString("command.oadmin.name") ?: "oadmin"
            val aliases = cConfig.getStringList("command.oadmin.aliases")
            val description = cConfig.getString("command.oadmin.description") ?: "OnAir 관리자 명령어"
        }
    }

    object TabComplete {
        object oa {
            val args_1= cConfig.getStringList("tab-complete.oa.1")
            val args_2= cConfig.getStringList("tab-complete.oa.2")
            val args_3= cConfig.getStringList("tab-complete.oa.3")
            val args_4= cConfig.getStringList("tab-complete.oa.4")
            val args_5= cConfig.getString("tab-complete.oa.5") ?: "채널명"
            val args_6= cConfig.getString("tab-complete.oa.6") ?: "ID"
            val args_7= cConfig.getString("tab-complete.oa.7") ?: "새채널이름"
            val args_8= cConfig.getString("tab-complete.oa.8") ?: "값"
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