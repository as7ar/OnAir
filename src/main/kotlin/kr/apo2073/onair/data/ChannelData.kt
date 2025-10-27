package kr.apo2073.onair.data

import com.google.gson.Gson
import kr.apo2073.onair.enums.Platforms

data class ChannelData(val platforms: Platforms, val display: String, val id: String) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

fun String.toChannelData(): ChannelData {
    return Gson().fromJson(this, ChannelData::class.java)
}