package pl.wolny.pacman

import kr.entree.spigradle.annotations.SpigotPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.pacman.commands.PacmanCommand
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.game.GameService
import pl.wolny.pacman.powerup.event.KillablePacmanListener


@SpigotPlugin
class PacmanMain : JavaPlugin() {

    private val gameService = GameService(this)
    private val pacmanCommand = PacmanCommand(gameService, gameService.powerUpComponent)

    override fun onEnable() {
        gameService.init()
        getCommand("pacman")?.setExecutor(pacmanCommand)
        Bukkit.getPluginManager().registerEvents(KillablePacmanListener(), this)
    }
}