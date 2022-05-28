package pl.wolny.pacman.game

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.health.PacmanCollisionListener
import pl.wolny.pacman.point.PointComponent
import pl.wolny.pacman.powerup.PowerUpComponent
import pl.wolny.pacman.powerup.event.KillablePacmanListener

class GameService(private val plugin: JavaPlugin) {

    private val gameTimer: GameTimer = GameTimer(plugin)
    private val pacmanController = PacmanController()
    val powerUpComponent = PowerUpComponent()
    private val pointComponent = PointComponent(plugin, powerUpComponent)
    private val gameSpawnPointsComponent = GameSpawnPointsComponent()
    private val pacmanCollisionListener = PacmanCollisionListener(gameSpawnPointsComponent.spawnPoints, pacmanController)

    fun init() {
        Bukkit.getPluginManager().registerEvents(pacmanController, plugin)
        Bukkit.getPluginManager().registerEvents(KillablePacmanListener(pacmanController), plugin)
    }

    fun prepare(player: Player) {
        Bukkit.getPluginManager().registerEvents(pointComponent, plugin)
        Bukkit.getPluginManager().registerEvents(pacmanCollisionListener, plugin)
        gameSpawnPointsComponent.init()
    }

    fun prepare(player: Player) {
        //TODO: Move events to init
        pacmanController.registerPacman(Bukkit.getWorld("world")!!.getBlockAt(-93, -57, 73), player)
        pointComponent.prepare()
        pointComponent.running = true
        pacmanCollisionListener.running = true
        giveItems(player)
    }

    fun start() {

        gameTimer.register(pacmanController)
        gameTimer.register(powerUpComponent)
        gameTimer.register(pointComponent)
        gameTimer.start()
        pacmanController.running = true
    }

    fun halt() {
        gameTimer.stop()
        pacmanController.running = false
        pointComponent.running = false
        pacmanCollisionListener.running = false
        pacmanController.clear()
        pointComponent.clear()
        gameSpawnPointsComponent.spawnPoints.clear()
    }

    private fun giveItems(player: Player) {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta

        meta.setCustomModelData(1)
        item.itemMeta = meta
        player.inventory.setItem(6, item)

        meta.setCustomModelData(2)
        item.itemMeta = meta
        player.inventory.setItem(7, item)

        meta.setCustomModelData(3)
        item.itemMeta = meta
        player.inventory.setItem(5, item)

        meta.setCustomModelData(4)
        item.itemMeta = meta
        player.inventory.setItem(8, item)
    }

}