package pl.wolny.pacman.extension

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

inline fun World.forEachIn(loc1: Location, loc2: Location, action: (Block) -> Unit) {
    val highestX: Int = loc2.blockX.coerceAtLeast(loc1.blockX)
    val lowestX: Int = loc2.blockX.coerceAtMost(loc1.blockX)

    val highestY: Int = loc2.blockY.coerceAtLeast(loc1.blockY)
    val lowestY: Int = loc2.getBlockY().coerceAtMost(loc1.blockY)

    val highestZ: Int = loc2.blockZ.coerceAtLeast(loc1.blockZ)
    val lowestZ: Int = loc2.blockZ.coerceAtMost(loc1.blockZ)

    for (x in lowestX..highestX) {
        for (y in lowestY..highestY) {
            for (z in lowestZ..highestZ) {
                action(getBlockAt(x, y, z))
            }
        }
    }
}
