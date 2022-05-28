package pl.wolny.pacman

import kr.entree.spigradle.annotations.SpigotPlugin
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.pacman.command.PacmanCommand
import pl.wolny.pacman.game.GameService


@SpigotPlugin
class PacmanMain : JavaPlugin() {

    private val gameService = GameService(this)
    private val pacmanCommand = PacmanCommand(gameService, gameService.powerUpComponent)

    override fun onEnable() {
        gameService.init()
        getCommand("pacman")?.setExecutor(pacmanCommand)
    }
}