package kr.apo2073.onair.utils

import kr.apo2073.onair.OnAir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ConfigSet {
    private val plugin = OnAir.plugin
    private val config = plugin.config
    private val commandFile = File(plugin.dataFolder, "command.yml")
    private val cConfig: YamlConfiguration by lazy {
        YamlConfiguration.loadConfiguration(commandFile)
    }

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

    val chatFormat = getStringCompat("채팅.형식", "chat.format")
    val emoticon= getStringCompat("채팅.이모티콘", "chat.emoticon") ?: "&7(이모티콘)&f"
    val anon= getStringCompat("채팅.익명", "chat.anon") ?: "(익명)"
    val plats= config.getStringList("platforms")

    object donation {
        val donationFormat= getStringCompat("후원.형식", "donation.format")
        val showTitle= config.getBoolean("후원.타이틀표시", true)
        val titleFormat= getStringCompat("후원.타이틀형식", "donation.title-format")
        fun command(amount: Int): String? {
            return getStringCompat("후원이벤트.$amount", "donation-event.${amount}")
        }
    }

    val debug= plugin.config.getBoolean("debug", false)
    val colored= plugin.config.getBoolean("플렛폼.채색")
    val en = plugin.config.getBoolean("플렛폼.영어")
    val balloon= plugin.config.getBoolean("soop.donation-balloon", false)

    private fun getStringCompat(vararg keys: String): String? {
        for (key in keys) {
            val value = config.getString(key)
            if (value != null) return value
        }
        return null
    }
}