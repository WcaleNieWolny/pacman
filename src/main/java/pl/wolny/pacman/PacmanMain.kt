package pl.wolny.pacman

import kr.entree.spigradle.annotations.SpigotPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.pacman.commands.PacmanCommand
import pl.wolny.pacman.entity.PacmanController

private val pacmanController = PacmanController()
private val pacmanCommand = PacmanCommand(pacmanController)

@SpigotPlugin
class PacmanMain : JavaPlugin() {
    override fun onEnable() {
        getCommand("pacman")?.setExecutor(pacmanCommand)
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(pacmanController, this)
    }
}