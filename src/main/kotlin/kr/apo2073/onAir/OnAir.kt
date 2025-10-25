package kr.apo2073.onAir

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import kr.apo2073.onAir.cmds.OACommand
import kr.apo2073.onAir.cmds.OAdminCommand
import kr.apo2073.onAir.data.ConnectionManager
import kr.apo2073.onAir.data.UserData
import kr.apo2073.onAir.enums.Platforms
import kr.apo2073.onAir.events.skript.SkriptStreamingChatEvent
import kr.apo2073.onAir.events.skript.SkriptStreamingConnectionEvent
import kr.apo2073.onAir.events.skript.SkriptStreamingDisconnectionEvent
import kr.apo2073.onAir.events.skript.SkriptStreamingDonateEvent
import kr.apo2073.onAir.events.skript.exper.StrmChatExper
import kr.apo2073.onAir.events.skript.exper.StrmConnectExper
import kr.apo2073.onAir.events.skript.exper.StrmDisconnectExper
import kr.apo2073.onAir.events.skript.exper.StrmDonateExper
import kr.apo2073.onAir.listeners.BukkitListener
import kr.apo2073.onAir.listeners.ChzzkListener
import kr.apo2073.onAir.utils.Debugger
import kr.apo2073.onAir.utils.Utils.bannerGenerator
import kr.apo2073.onAir.utils.Utils.sendMessage
import kr.apo2073.onAir.utils.Utils.translate
import kr.apo2073.onAir.utils.chzzk.ChzzkData
import kr.apo2073.onAir.utils.toMiniMessage
import kr.apo2073.tnliv.Toonation
import kr.apo2073.ytliv.Youtube
import okio.FileNotFoundException
import org.bukkit.plugin.java.JavaPlugin
import xyz.r2turntrue.chzzk4j.ChzzkClient
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter
import xyz.r2turntrue.chzzk4j.auth.ChzzkSimpleUserLoginAdapter
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.naver.NaverAutologinAdapter
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
        //lateinit var af:MutableMap<UUID, AfreecatvAPI>
    }

    private lateinit var addon: SkriptAddon

    override fun onLoad() {
        // ========================[ Early Base Setting ]=========================
        plugin =this

        Debugger.debug("Loading Plugin")

        Debugger.debug("saving default file")
        saveDefaultConfig()
        saveResource("lang/ko.json", true)

        cht = mutableMapOf()
        tn = mutableMapOf()
//        af = mutableMapOf()
        yt = mutableMapOf()
    }

    override fun onEnable() {
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

            SkriptStreamingConnectionEvent()
            SkriptStreamingDisconnectionEvent()
            SkriptStreamingChatEvent()
            SkriptStreamingDonateEvent()

            StrmConnectExper()
            StrmDisconnectExper()
            StrmChatExper()
            StrmDonateExper()

            Debugger.debug("Succeed to register Skript Addon")
        } else Debugger.debug("Failed to register Skript Addon")

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

        banner.forEach { server.consoleSender.sendMessage(it.toMiniMessage()) }
    }

    override fun onDisable() {
        try {
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
