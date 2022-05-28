package pl.wolny.pacman.point

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import pl.wolny.pacman.extension.LocationDataStringToVector
import pl.wolny.pacman.extension.forEachIn
import pl.wolny.pacman.extension.toDataString
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.game.GameObject
import pl.wolny.pacman.point.scoreboard.ScoreHelper
import pl.wolny.pacman.powerup.PowerUp
import pl.wolny.pacman.powerup.PowerUpComponent
import java.util.*


//TODO: GameObject
class PointComponent(plugin: JavaPlugin, private val spawnPoints: MutableList<Location>) : Listener, GameObject {

    val spawnPoints = mutableListOf<Location>()
    private val playerPoints: MutableList<PowerPlayer> = mutableListOf()
    private val pickedPoints: MutableList<Vector> = mutableListOf()
    private val namespacedKey = NamespacedKey(plugin, "PACMAN_LOC")
    private var time = 600
    var running = false

    fun prepare() {
        val world = Bukkit.getWorld("world")
        world?.forEachIn(Location(world, 0.0, -59.0, 0.0), Location(world, -168.0, -59.0, 139.0)) { block ->
            if (block.type == Material.STONE_BUTTON || block.type == Material.CRIMSON_BUTTON) {
                spawnPoints.add(block.location)
            }
        }
        dropItems(true)
        preparePointsMap()
        prepareScoreBoard()

    }

    fun dropItems(all: Boolean = false) {
        val item = ItemStack(Material.GOLD_NUGGET, 1)
        val meta = item.itemMeta
        for (it in spawnPoints) {
            val item = when(it.block.type){
                Material.STONE_BUTTON -> ItemStack(Material.GOLD_NUGGET, 1)
                Material.CRIMSON_BUTTON -> ItemStack(Material.IRON_NUGGET, 1)
                else -> ItemStack(Material.GOLD_NUGGET, 1)
            }
            val meta = item.itemMeta
            //TODO: Loop all items in pickedPoints and spawn
            if (!all) {
                val vector = Vector(it.x, it.y, it.z)
                if (!pickedPoints.contains(vector)) {
                    continue
                }
            }
            meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, it.toDataString())
            item.itemMeta = meta
            val loc = it.clone()
            loc.add(Vector(0.5, 0.75, 0.5))
            val dropItem = it.world.dropItem(loc, item)
            dropItem.setGravity(false)
            dropItem.setWillAge(true)
            dropItem.velocity = Vector(0, 0, 0)
        }

        pickedPoints.clear()
    }

    fun clear() {
        for (entity in Bukkit.getWorld("world")!!.entities) {
            if (entity is Item) {
                entity.remove()
            }
        }
        playerPoints.clear()
    }

    private fun prepareScoreBoard() {
        Bukkit.getServer().onlinePlayers.forEach {
            val scoreboard = ScoreHelper.createScore(it)
            scoreboard.setTitle("Punkty")
        }
        renderScoreBoard()
    }

    private fun preparePointsMap() {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            playerPoints.add(PowerPlayer(player.uniqueId, player.name))
        }
    }

    @EventHandler
    private fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if(!running){
            return
        }
        val player = event.player
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player)
        }
    }

    @EventHandler
    private fun onPlayerPickupEvent(event: EntityPickupItemEvent) {
        if(!running){
            return
        }
        val player = event.entity
        val item = event.item.itemStack

        if (player !is Player) {
            return
        }
        if (item.type != Material.GOLD_NUGGET && item.type != Material.IRON_NUGGET) {
            return
        }
        if (!item.itemMeta.persistentDataContainer.has(namespacedKey)) {
            return
        }

        val vector = LocationDataStringToVector(
            item.itemMeta.persistentDataContainer.get(
                namespacedKey,
                PersistentDataType.STRING
            )!!
        )
        pickedPoints.add(vector)

        event.isCancelled = true
        event.item.remove()

        if(item.type == Material.GOLD_NUGGET){
            val powerPlayer = playerPoints.filter { it.uuid == player.uniqueId }[0]
            powerPlayer.point = powerPlayer.point + 1
            playerPoints.sortBy { it.point }
            renderScoreBoard()
        }else{
            powerUpComponent.activate(PowerUp.values().random(), player)
        }

    }

    private fun renderScoreBoard() {
        for (player in Bukkit.getServer().onlinePlayers) {
            val scoreHelper = ScoreHelper.getByPlayer(player) ?: continue
            var id = 1
            for (point in playerPoints) {
                scoreHelper.setSlot(id, "&a${point.name}&f > ${point.point}")
                id++
            }
        }
    }

    override fun tick() {
        time -= 2
        if (time == 0) {
            dropItems()
            println("YES!")
            time = 300
        }
    }

}

data class PowerPlayer(val uuid: UUID, val name: String, var point: Int = 0)