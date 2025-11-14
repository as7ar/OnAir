package kr.astar.onair.utils.chzzk

import kr.astar.api.chzzk4j.auth.ChzzkLoginAdapter

class ChzzkData {
    private var clientID: ClientID?=null
    private val auth=mutableListOf<ChzzkLoginAdapter>()
    fun addAdapter(adapter: ChzzkLoginAdapter) {
        auth.add(adapter)
    }
    fun getAdapter(): MutableList<ChzzkLoginAdapter> = auth
    fun setClientKey(api:String, secret: String) {
        clientID= ClientID(api, secret)
    }
    fun getClientKey(): ClientID?= clientID
}

data class ClientID(val id: String, val secret: String)