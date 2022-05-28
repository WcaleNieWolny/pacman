package pl.wolny.pacman.game

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import pl.wolny.pacman.extension.forEachIn

class GameSpawnPointsComponent {
    val spawnPoints = mutableListOf<Location>()

    fun init(){
        val world = Bukkit.getWorld("world")
        world?.forEachIn(Location(world, 0.0, -59.0, 0.0), Location(world, -168.0, -59.0, 139.0)) { block ->
            if (block.type == Material.STONE_BUTTON) {
                spawnPoints.add(block.location)
            }
        }
    }
}