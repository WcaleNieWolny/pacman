package pl.wolny.pacman.extension

import org.bukkit.block.Block
import org.bukkit.util.Vector


fun Block.getRelative(vector: Vector): Block {
    return getRelative(
        vector.x.toInt(),
        vector.y.toInt(),
        vector.z.toInt()
    )
}