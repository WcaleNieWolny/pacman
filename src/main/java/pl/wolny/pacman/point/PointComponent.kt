package pl.wolny.pacman.point

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import pl.wolny.pacman.extension.forEachIn
import pl.wolny.pacman.extension.toDataString

//TODO: GameObject
class PointComponent(private val plugin: JavaPlugin) {
    val spawnPoints = mutableListOf<Location>()
    fun prepare(){
        val world = Bukkit.getWorld("world")
        world?.forEachIn(Location(world,0.0, -59.0, 0.0), Location(world,-168.0, -59.0, 139.0)){block ->
            if(block.type == Material.STONE_BUTTON){
                spawnPoints.add(block.location)
            }
        }
        dropItems()

    }

    fun dropItems(){
        val item = ItemStack(Material.GOLD_NUGGET, 1)
        val meta = item.itemMeta
        spawnPoints.forEach {
            meta.persistentDataContainer.set(NamespacedKey(plugin, "PACMAN_LOC"), PersistentDataType.STRING, it.toDataString() )
            item.itemMeta = meta
            val loc = it.clone()
            loc.add(Vector(0.5, 0.75, 0.5))
            val dropItem = it.world.dropItem(loc, item)
            dropItem.setGravity(false)
            dropItem.velocity = Vector(0,0,0)
        }
    }
}