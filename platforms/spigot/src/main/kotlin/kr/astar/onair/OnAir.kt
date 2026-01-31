package kr.astar.onair

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import xyz.r2turntrue.chzzk4j.ChzzkClient
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter
import xyz.r2turntrue.chzzk4j.auth.ChzzkSimpleUserLoginAdapter
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.naver.NaverAutologinAdapter
import kr.astar.onair.cmds.OACommand
import kr.astar.onair.data.ConnectionManager
import kr.astar.onair.data.UserData
import kr.astar.onair.enums.Platforms
import kr.astar.onair.listeners.BukkitListener
import kr.astar.onair.listeners.platforms.ChzzkListener
import kr.astar.onair.papi.PlaceholderHandler
import kr.astar.onair.utils.Debugger
import kr.astar.onair.utils.OALogger
import kr.astar.onair.utils.Utils.bannerGenerator
import kr.astar.onair.utils.Utils.translate
import kr.astar.onair.utils.Utils.sendMessage
import kr.astar.onair.utils.chzzk.ChzzkData
import kr.astar.onair.utils.toMiniMessage
import kr.astar.api.soopliv.soop.SoopWebSocket
import kr.astar.api.toonLiv.Toonation
import kr.astar.api.twitchLiv.Twitch
import kr.astar.api.utubeLiv.Youtube
import kr.astar.api.weflabLiv.Weflab
import kr.astar.onair.utils.Utils.reset
import kr.astar.onair.utils.toLegacyString
import okio.FileNotFoundException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
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
        lateinit var wf: MutableMap<UUID, Weflab>
    }

    private lateinit var addon: SkriptAddon
    val log= OALogger()
    val version = "1.2.1"

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
        wf = mutableMapOf()
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

        chzzkClient= ChzzkClientBuilder(key?.id, key?.secret)
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
        kr.astar.onair.cmds.OAdminCommand()

        // ========================[ Depends ]=========================

        Debugger.debug("Trying to register Skript addon")
        if (server.pluginManager.getPlugin("Skript")!=null) {
            addon = Skript.registerAddon(this)

            addon.loadClasses("kr.astar.onair.events.skript")

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

        if (apiToken!="" && apiRefresh!="") data.addAdapter(
            ChzzkSimpleUserLoginAdapter(
                apiToken, apiRefresh
            )
        )
        if (cookieSes != "" && cookieAut != "") data.addAdapter(
            ChzzkLegacyLoginAdapter(
                cookieAut, cookieSes
            )
        )
        if (naverId!="" && naverPw!="") data.addAdapter(
            NaverAutologinAdapter(
                naverId, naverPw
            )
        )
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
            version = version,
            author = "ASTAR"
        )

        banner.forEach { server.consoleSender.sendMessage(
            "<gradient:#E7B0B0:#BA4242>${it}</gradient>".toMiniMessage().toLegacyString()
        ) }
    }

    private fun loadFile(name:String): YamlConfiguration {
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
        this.reset(true)
    }
}
