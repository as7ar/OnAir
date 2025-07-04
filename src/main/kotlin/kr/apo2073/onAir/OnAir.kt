package kr.apo2073.onAir

import kr.apo2073.Toonation
import kr.apo2073.onAir.chzzk.ChzzkData
import kr.apo2073.ytliv.Youtube
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
        lateinit var plugin: JavaPlugin
        lateinit var chzzkData: ChzzkData

        lateinit var chzzkClient: ChzzkClient
        lateinit var cht: MutableMap<UUID, ChzzkChat>
        lateinit var tn: MutableMap<UUID, Toonation>
        lateinit var yt:MutableMap<UUID, Youtube>
        //lateinit var af:MutableMap<UUID, AfreecatvAPI>
    }
    override fun onEnable() {
        plugin =this
        chzzkData =ChzzkData()
        saveDefaultConfig()

        cht = mutableMapOf()
        tn = mutableMapOf()
//        af = mutableMapOf()
        yt = mutableMapOf()
    }

    override fun onLoad() {
        chzzkData.setClientKey(
            config.getString("chzzk.client.id") ?: "",
            config.getString("chzzk.client.secret") ?: ""
        )
        val key= chzzkData.getClientKey()
        val data= chzzkData
        setAut()

        chzzkClient=ChzzkClientBuilder(key?.id, key?.secret)
            .apply {
                data.getAdapter().forEach {
                    withLoginAdapter(it)
                }
            }.build()
    }

    private fun setAut() { reloadConfig()
        val data= chzzkData

        val apiToken=config.getString("chzzk.api.accessToken") ?: ""
        val apiRefresh=config.getString("chzzk.api.refreshToken") ?: ""

        val cookieSes=config.getString("chzzk.cookie.ses") ?: ""
        val cookieAut=config.getString("chzzk.cookie.aut") ?: ""

        val naverId=config.getString("chzzk.naver.id") ?: ""
        val naverPw=config.getString("chzzk.naver.pw") ?: ""

        if (apiToken!="" && apiRefresh!="") {
            data.addAdapter(ChzzkSimpleUserLoginAdapter(
                    apiToken, apiRefresh
            ))
        }
        if (cookieSes != "" && cookieAut != "") {
            data.addAdapter(ChzzkLegacyLoginAdapter(
                    cookieAut, cookieSes
            ))
        }
        if (naverId!="" && naverPw!="") {
            data.addAdapter(NaverAutologinAdapter(
                    naverId, naverPw
            ))
        }
    }

    override fun onDisable() {
        server.onlinePlayers.forEach { player->

        }
    }
}
