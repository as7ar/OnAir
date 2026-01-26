package kr.astar.onair.api.minecraft

object BukkitPlatforms {

    private val isPaper: Boolean = runCatching {
        Class.forName("com.destroystokyo.paper.ParticleBuilder", false, this::class.java.classLoader)
    }.isSuccess

    fun isPaper() = isPaper
    fun isSpigot() = !isPaper
}