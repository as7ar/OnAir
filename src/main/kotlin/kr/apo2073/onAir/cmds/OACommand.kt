package kr.apo2073.onAir.cmds

import kr.apo2073.onAir.OnAir
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class OACommand: TabExecutor {
    private val plugin= OnAir.plugin

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): List<String?> {
        val tab=mutableListOf<String>()
        return tab
    }
}