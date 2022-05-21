package pl.wolny.pacman.game

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.pacman.entity.PacmanController

class GameService(private val plugin: JavaPlugin) {

    private val gameTimer: GameTimer = GameTimer(plugin)
    private val pacmanController = PacmanController()

    fun init(){
        Bukkit.getPluginManager().registerEvents(pacmanController, plugin)
    }

    fun prepare(player: Player){
        pacmanController.registerPacman(Bukkit.getWorld("world")!!.getBlockAt(-93, -57, 73), player)
    }

    fun start(){

        gameTimer.register(pacmanController)
        gameTimer.start()
        pacmanController.running = true
    }

    fun halt(){
        gameTimer.stop()
        pacmanController.running = false
    }

}