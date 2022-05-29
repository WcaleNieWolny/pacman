package pl.wolny.pacman.game

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.health.HealthComponent
import pl.wolny.pacman.point.PointComponent
import pl.wolny.pacman.powerup.PowerUpComponent
import pl.wolny.pacman.powerup.event.FreePointListener
import pl.wolny.pacman.powerup.event.JumpBoostListener
import pl.wolny.pacman.powerup.event.KillablePacmanListener
import pl.wolny.pacman.powerup.event.SwordListener

class GameService(private val plugin: JavaPlugin) {

    private val gameTimer: GameTimer = GameTimer(plugin)
    private val pacmanController = PacmanController()
    val powerUpComponent = PowerUpComponent()
    private val gameSpawnPointsComponent = GameSpawnPointsComponent()
    private val pointComponent = PointComponent(plugin, gameSpawnPointsComponent.spawnPoints, powerUpComponent)
    private val healthComponent = HealthComponent(gameSpawnPointsComponent.spawnPoints, pacmanController)

    fun init() {
        Bukkit.getPluginManager().registerEvents(pacmanController, plugin)
        Bukkit.getPluginManager().registerEvents(KillablePacmanListener(pacmanController), plugin)
        Bukkit.getPluginManager().registerEvents(JumpBoostListener(gameSpawnPointsComponent.spawnPoints), plugin)
        Bukkit.getPluginManager().registerEvents(FreePointListener(pointComponent), plugin)
        Bukkit.getPluginManager().registerEvents(TexturePackListener(), plugin)
        Bukkit.getPluginManager().registerEvents(SwordListener(healthComponent, pointComponent), plugin)
        Bukkit.getPluginManager().registerEvents(pointComponent, plugin)
        Bukkit.getPluginManager().registerEvents(healthComponent, plugin)
        gameSpawnPointsComponent.init()
    }

    fun prepare(player: Player) {
        //TODO: Move events to init
        pacmanController.registerPacman(Bukkit.getWorld("world")!!.getBlockAt(-93, -57, 73), player)
        pointComponent.prepare()
        giveItems(player)
    }

    fun start(player: Player) {

        prepareServer(player)

        gameTimer.register(pacmanController)
        gameTimer.register(powerUpComponent)
        gameTimer.register(pointComponent)
        gameTimer.start()
        pacmanController.running = true
        pointComponent.running = true
        healthComponent.running = true
    }

    private fun prepareServer(player: Player) {
        Bukkit.getServer().onlinePlayers.forEach{
            it.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 9999, 0, true, false))
            it.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 9999, 0, true, false))
            it.teleport(gameSpawnPointsComponent.spawnPoints.random())
        }
    }

    fun halt() {
        gameTimer.stop()
        pacmanController.running = false
        pointComponent.running = false
        healthComponent.running = false
        pacmanController.clear()
        pointComponent.clear()
        gameSpawnPointsComponent.spawnPoints.clear()

        Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, "minecraft:effect clear @a")
    }

    private fun giveItems(player: Player) {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta
        for (i in 1..4){
            meta.setCustomModelData(1)
            item.itemMeta = meta
            player.inventory.setItem(5+i, item)
        }
    }

}