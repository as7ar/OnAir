package kr.apo2073.onair

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import kr.apo2073.onair.cmds.OACommand
import kr.apo2073.onair.cmds.OAdminCommand
import kr.apo2073.onair.data.ConnectionManager
import kr.apo2073.onair.data.UserData
import kr.apo2073.onair.enums.Platforms
import kr.apo2073.onair.listeners.BukkitListener
import kr.apo2073.onair.listeners.platforms.ChzzkListener
import kr.apo2073.onair.papi.PlaceholderHandler
import kr.apo2073.onair.utils.Debugger
import kr.apo2073.onair.utils.OALogger
import kr.apo2073.onair.utils.Utils.bannerGenerator
import kr.apo2073.onair.utils.Utils.sendMessage
import kr.apo2073.onair.utils.Utils.translate
import kr.apo2073.onair.utils.chzzk.ChzzkData
import kr.apo2073.onair.utils.toMiniMessage
import kr.apo2073.soopliv.soop.SoopWebSocket
import kr.apo2073.toonLiv.Toonation
import kr.apo2073.twitchLiv.Twitch
import kr.apo2073.utubeLiv.Youtube
import okio.FileNotFoundException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import xyz.r2turntrue.chzzk4j.ChzzkClient
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter
import xyz.r2turntrue.chzzk4j.auth.ChzzkSimpleUserLoginAdapter
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.naver.NaverAutologinAdapter
import java.io.File
import java.io.InputStreamReader
import java.util.*

class OnAir : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var plugin: OnAir

        lateinit var chzzkData: ChzzkData
        lateinit var chzzkClient: ChzzkClient

        lateinit var cht: MutableMap<UUID, ChzzkChat>
        lateinit var tn: MutableMap<UUID, Toonation>
        lateinit var yt:MutableMap<UUID, Youtube>
        lateinit var tw: MutableMap<UUID, Twitch>
        lateinit var sp: MutableMap<UUID, SoopWebSocket>
    }

    private lateinit var addon: SkriptAddon
    val log= OALogger()

    override fun onLoad() {
        // ========================[ Early Base Setting ]=========================
        plugin =this

        Debugger.debug("Loading Plugin")

        Debugger.debug("saving default file")
        saveDefaultConfig()
        saveResource("lang/ko.json", true)
        loadFile("command.yml")

        cht = mutableMapOf()
        tn = mutableMapOf()
        yt = mutableMapOf()
        tw = mutableMapOf()
        sp = mutableMapOf()
    }

    override fun onEnable() {
        log.info("Enabling OnAir Plugin...")
        // ========================[ Default Setting ]=========================

        chzzkData =ChzzkData()
        val clientApi=config.getString("chzzk.client.id") ?: ""
        val clientSecret=config.getString("chzzk.client.secret") ?: ""

        Debugger.debug("setting Chzzk Client (api: ${clientApi}, secret: $clientSecret )")
        chzzkData.setClientKey(clientApi, clientSecret)
        val key= chzzkData.getClientKey()
        val data= chzzkData
        setAut()

        chzzkClient=ChzzkClientBuilder(key?.id, key?.secret)
            .apply {
                data.getAdapter().forEach {
                    withLoginAdapter(it)
                }
            }.build()

        // ========================[ Listener ]=========================

        Debugger.debug("Registering Events")
        server.pluginManager.registerEvents(ChzzkListener(), this)
        server.pluginManager.registerEvents(BukkitListener(), this)

        // ========================[ Command ]=========================

        Debugger.debug("Registering Command")
        OACommand()
        OAdminCommand()

        // ========================[ Depends ]=========================

        Debugger.debug("Trying to register Skript addon")
        if (server.pluginManager.getPlugin("Skript")!=null) {
            addon = Skript.registerAddon(this)

            addon.loadClasses("kr.apo2073.onAir.events.skript")

            Debugger.debug("Succeed to register Skript Addon")
        } else Debugger.debug("Failed to register Skript Addon")

        Debugger.debug("Trying to register PlaceholderAPI expansion")
        if (server.pluginManager.getPlugin("PlaceholderAPI")!=null) {
            PlaceholderHandler().register()
        }
        // ========================[ Loading Plugin Suc ]=========================
        printLogo()
    }

    private fun setAut() { reloadConfig()
        val data= chzzkData

        val apiToken=config.getString("chzzk.api.accessToken") ?: ""
        val apiRefresh=config.getString("chzzk.api.refreshToken") ?: ""

        val cookieSes=config.getString("chzzk.cookie.ses") ?: ""
        val cookieAut=config.getString("chzzk.cookie.aut") ?: ""

        val naverId=config.getString("chzzk.naver.id") ?: ""
        val naverPw=config.getString("chzzk.naver.pw") ?: ""

        if (apiToken!="" && apiRefresh!="") data.addAdapter(ChzzkSimpleUserLoginAdapter(
            apiToken, apiRefresh
        ))
        if (cookieSes != "" && cookieAut != "") data.addAdapter(ChzzkLegacyLoginAdapter(
            cookieAut, cookieSes
        ))
        if (naverId!="" && naverPw!="") data.addAdapter(NaverAutologinAdapter(
            naverId, naverPw
        ))
    }

    private fun printLogo() {
        val art = listOf(
            " ██████╗ ███╗   ██╗ █████╗ ██╗██████╗ ",
            "██╔═══██╗████╗  ██║██╔══██╗██║██╔══██╗",
            "██║   ██║██╔██╗ ██║███████║██║██████╔╝",
            "██║   ██║██║╚██╗██║██╔══██║██║██╔══██╗",
            "╚██████╔╝██║ ╚████║██║  ██║██║██║  ██║",
            " ╚═════╝ ╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝"
        )

        val banner = bannerGenerator(
            art,
            version = pluginMeta.version,
            author = pluginMeta.authors.joinToString(", ")
        )

        banner.forEach { server.consoleSender.sendMessage(
            "<gradient:#E7B0B0:#BA4242>${it}</gradient>".toMiniMessage()
        ) }
    }

    fun loadFile(name:String): YamlConfiguration {
        val file = File(dataFolder, name)

        if (!file.exists()) saveResource(name, false)

        val userConfig = YamlConfiguration.loadConfiguration(file)

        val defaultConfigStream = getResource(name) ?: return userConfig
        val defaultConfig = YamlConfiguration.loadConfiguration(
            InputStreamReader(defaultConfigStream, Charsets.UTF_8)
        )

        userConfig.options().copyDefaults(true)
        userConfig.setDefaults(defaultConfig)
        userConfig.save(file)

        return userConfig
    }


    override fun onDisable() {
        try {
            log.warning("Disabling OnAir Plugin...")
            server.onlinePlayers.forEach { player->
                Debugger.debug("Trying to disable plugin from Player: ${player.name}")
                player.sendMessage(translate("system.disabled.player"), true)
                val userData= UserData(player)
                for (platforms in Platforms.entries) {
                    Debugger.debug("Disconnecting Platform: ${platforms.name}")
                    ConnectionManager.Manager(player).disconnect(platforms)
                }
                userData.getFile().delete()
                ConnectionManager.infoFile.delete()
            }
        } catch (_: FileNotFoundException) {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
