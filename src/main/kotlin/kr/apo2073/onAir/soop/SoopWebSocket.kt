package kr.apo2073.onAir.soop

import kr.apo2073.onAir.soop.listener.SoopListener
import okhttp3.*
import java.util.concurrent.TimeUnit

class SoopWebSocket(
    private val uri: String,
    private val liveInfo: SoopLiveInfo,
    private val user: SoopUser,
    private val listener: SoopListener
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var alive = true
    private var pingThread: Thread? = null

    fun connect() {
        val client = OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url(uri).build()
        client.newWebSocket(request, this)
    }

    override fun onOpen(ws: WebSocket, response: Response) {
        webSocket = ws
        alive = true
        ws.send(CONNECT_PACKET)
        startPingThread()
        listener.onConnect()
    }

    override fun onMessage(ws: WebSocket, text: String) {
        try {
            if (text == CONNECT_RES_PACKET) {
                val joinCmd = makePacket(COMMAND_JOIN, "$FORM_FEED${liveInfo.CHATNO()}${FORM_FEED.repeat(5)}")
                ws.send(joinCmd)
                return
            }

            val cleaned = text.replace(ESC, "")
            val packet = SoopPacket(cleaned.split(FORM_FEED).toMutableList())
            listener.onMessage(packet)
        } catch (e: Exception) {
            listener.onError(e)
            close()
        }
    }

    override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
        alive = false
        listener.onFail(t, response)
        close()
    }

    override fun onClosing(ws: WebSocket, code: Int, reason: String) {
        ws.close(code, reason)
        alive = false
        listener.onDisconnect()
    }

    private fun startPingThread() {
        pingThread = Thread {
            try {
                while (alive) {
                    Thread.sleep(20_000L)
                    webSocket?.send(PING_PACKET)
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }.apply { start() }
    }

    fun close() {
        alive = false
        pingThread?.interrupt()
        webSocket?.close(1000, "Closed by client")
        pingThread = null
    }

    companion object {
        private const val ESC = "\u001B\t"
        private const val FORM_FEED = "\u000C"

        private const val COMMAND_CONNECT = "0001"
        private const val COMMAND_JOIN = "0002"
        private const val COMMAND_PING = "0000"

        private val CONNECT_PACKET = makePacket(COMMAND_CONNECT, FORM_FEED.repeat(3) + "16" + FORM_FEED)
        private val CONNECT_RES_PACKET = makePacket(COMMAND_CONNECT, FORM_FEED.repeat(2) + "16|0" + FORM_FEED)
        private val PING_PACKET = makePacket(COMMAND_PING, FORM_FEED)

        private fun makePacket(command: String, data: String) =
            ESC + command + "%06d00".format(data.length) + data
    }
}
