package pl.wolny.pacman.entity

import org.bukkit.Location
import org.bukkit.block.Block

data class PacmanEntity(
    var blocks: MutableList<Block>,
    var location: Location,
    var direction: PacmanDirection,
    var nextDirection: PacmanDirection = direction
)