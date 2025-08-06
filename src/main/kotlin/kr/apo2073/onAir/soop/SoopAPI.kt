package kr.apo2073.onAir.soop

import kr.apo2073.onAir.soop.listener.SoopListener
import kr.apo2073.onAir.utils.soop.SoopWebSocket
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets

//fun main() {
//    println(SoopAPI.isOnline("khm11903"))
//    val info= SoopAPI.getPlayerLive("khm11903") ?: return
//    println("wss://${info.CHDOMAIN()?.lowercase()}:${(info.CHPT()?.toInt() ?: 0)+1}/Websocket/khm11903")
//    SoopAPI.connect("a", "khm11903", object : SoopListener {
//        override fun onConnect() {
//            println("connected")
//        }
//
//        override fun onMessage(packet: SoopPacket) {
//            packet.dataList.forEach { println(it) }
//        }
//
//        override fun onFail(t: Throwable, response: Response?) {
//        }
//
//        override fun onError(e: Exception) {
//        }
//
//        override fun onDisconnect() {
//        }
//    })
//}
object SoopAPI {
    fun connect(display: String?, bjid: String, listener: SoopListener): SoopWebSocket? {
        val info=getPlayerLive(bjid) ?: return null
        val user= SoopUser(display ?: info.BJID(), bjid)
        val soopWebSocket= SoopWebSocket(
            "wss://${info.CHATNO()?.lowercase()}:${(info.CHPT()?.toInt() ?: 0)+1}/Websocket/${bjid}",
            info, user, listener
        )
        soopWebSocket.connect()
        return soopWebSocket
    }

    fun getPlayerLive(bjid: String): SoopLiveInfo? {
        val requestURL = "https://live.afreecatv.com/afreeca/player_live_api.php?bjid=$bjid"

        return try {
            val client = HttpClient.newHttpClient()

            val bodyJson = JSONObject().apply {
                put("bid", bjid)
                put("type", "live")
                put("confirm_adult", "false")
                put("player_type", "html5")
                put("mode", "landing")
                put("from_api", "0")
                put("pwd", "")
                put("stream_type", "common")
                put("quality", "HD")
            }

            val request = HttpRequest.newBuilder()
                .POST(ofFormData(bodyJson))
                .uri(URI.create(requestURL))
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val response = client.sendAsync(request, BodyHandlers.ofString()).get()
            if (response.statusCode() == 200) {
                val parser = JSONParser()
                val jsonObject = parser.parse(response.body()) as JSONObject
                val channel = jsonObject["CHANNEL"] as JSONObject

                SoopLiveInfo(
                    channel["CHDOMAIN"].toString(),
                    channel["CHATNO"].toString(),
                    channel["FTK"].toString(),
                    channel["TITLE"].toString(),
                    channel["BJID"].toString(),
                    channel["BNO"].toString(),
                    (channel["CHPT"].toString().toInt() + 1).toString()
                )
            } else null
        } catch (ex: Exception) {
            null
        }
    }

    fun isOnline(bjid: String): Boolean {
        val requestURL = "https://live.afreecatv.com/afreeca/player_live_api.php?bjid=$bjid"

        try {
            val client = HttpClient.newHttpClient()

            val bodyJson = JSONObject().apply {
                put("bid", bjid)
                put("confirm_adult", "false")
                put("type", "live")
                put("mode", "landing")
                put("player_type", "html5")
                put("from_api", "0")
                put("pwd", "")
                put("stream_type", "common")
                put("quality", "HD")
            }

            val request = HttpRequest.newBuilder()
                .POST(ofFormData(bodyJson))
                .uri(URI.create(requestURL))
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val response = client.sendAsync(request, BodyHandlers.ofString()).get()
            if (response.statusCode() == 200) {
                val parser = JSONParser()
                val jsonObject = parser.parse(response.body()) as JSONObject
                val channel = jsonObject["CHANNEL"] as JSONObject
                val online = channel["RESULT"].toString().toInt()
                return online != 0
            }
        } catch (ex: Exception) {
            return false
        }
        return false
    }

    fun ofFormData(data: JSONObject): BodyPublisher {
        val builder = StringBuilder()
        var first = true

        data.forEach { (key, value) ->
            if (!first) builder.append("&") else first = false
            builder.append(URLEncoder.encode(key.toString(), StandardCharsets.UTF_8))
            builder.append("=")
            builder.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8))
        }

        return BodyPublishers.ofString(builder.toString())
    }
}