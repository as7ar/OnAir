package kr.astar.onair.data

import com.google.gson.Gson
import kr.astar.onair.enums.Platforms

data class ChannelData(val platforms: Platforms, val display: String, val id: String) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

fun String.toChannelData(): ChannelData {
    return Gson().fromJson(this, ChannelData::class.java)
}