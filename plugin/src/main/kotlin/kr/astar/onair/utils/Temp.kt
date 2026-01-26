package kr.astar.onair.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kr.astar.onair.OnAir
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.cast

object Temp {
    private val plugin = OnAir.plugin
    private val temperature = ConcurrentHashMap<String, Any>()
    private val file = File(plugin.dataFolder, "temp")
    private val gson =  GsonBuilder().setPrettyPrinting().create()

    init {
        load()
    }

    fun addTemp(key: String, value: Any) {
        temperature[key] = value
        save()
    }

    fun removeTemp(key: String) {
        temperature.remove(key)
        save()
    }

    fun <T: Any> getTemp(key: String, clazz: KClass<T>): T? {
        return try {
            clazz.cast(temperature[key])
        } catch (e: Exception) {
            null
        }
    }
    fun getTempAsString(key: String): String? = temperature[key] as? String
    fun getAll(): Map<String, Any> = temperature.toMap()

    private fun save() {
        try {
            if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
            val copy = HashMap(temperature)
            file.writeText(gson.toJson(copy))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun load() {
        if (!file.exists()) return
        try {
            val type = object : TypeToken<MutableMap<String, Any>>() {}.type
            val map: MutableMap<String, Any> = gson.fromJson(file.readText(), type)
            temperature.putAll(map)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
