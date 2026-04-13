package kr.astar.onair;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import kr.astar.onair.velocity.VeloChannel
import kr.astar.onair.velocity.VeloChannel.CHANNEL
import org.slf4j.Logger
import templates.kr.astar.onair.BuildConstants
import java.io.ByteArrayInputStream
import java.io.DataInputStream


@Plugin(
    id = "OnAir",
    name = "OnAir-Velo",
    version = BuildConstants.VERSION,
    url = "https://grassproject.github.io",
    authors = ["AS7AR"]
)
class OnAir @Inject constructor(val server: ProxyServer, val logger: Logger) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        server.channelRegistrar.register(
            MinecraftChannelIdentifier.from(
            CHANNEL
        ))
    }

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        val id = event.identifier as? MinecraftChannelIdentifier ?: return
        if (id.id != CHANNEL) return
        if (event.source !is Player) return

        val input = DataInputStream(ByteArrayInputStream(event.data))

        try {
            val type = input.readUTF()
            when(type) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
