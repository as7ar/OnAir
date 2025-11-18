package kr.astar.onair;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import org.slf4j.Logger
import templates.kr.astar.onair.BuildConstants

@Plugin(
    id = "OnAir",
    name = "velo-OnAir",
    version = BuildConstants.VERSION
    ,url = "https://grassproject.github.io"
    ,authors = ["AS7AR"]
)
class OnAir @Inject constructor(val logger: Logger) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }
}
