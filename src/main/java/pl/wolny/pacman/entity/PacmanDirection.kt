package pl.wolny.pacman.entity

import org.bukkit.Axis
import org.bukkit.Material
import org.bukkit.util.Vector

enum class PacmanDirection(val vector: Vector, val specialBlocks: Map<Vector, SpecialBlock>) {
    FORWARD(
        Vector(1, 0, 0), mapOf(
            Pair(Vector(1.0, 0.0, 1.0), SpecialBlock(Material.ACACIA_LOG, Axis.X)),
            Pair(Vector(1.0, 0.0, -1.0), SpecialBlock(Material.ACACIA_LOG, Axis.X)),
            Pair(Vector(1.0, -1.0, 0.0), SpecialBlock(Material.BIRCH_LOG, Axis.X)),
            Pair(Vector(1.0, -1.0, 1.0), SpecialBlock(Material.SPRUCE_LOG, Axis.X)),
            Pair(Vector(1.0, -1.0, -1.0), SpecialBlock(Material.DARK_OAK_LOG, Axis.X))
        )
    ),
    BACK(
        Vector(-1, 0, 0), mapOf(
            Pair(Vector(-1.0, 0.0, 1.0), SpecialBlock(Material.ACACIA_LOG, Axis.X)),
            Pair(Vector(-1.0, 0.0, -1.0), SpecialBlock(Material.ACACIA_LOG, Axis.X)),
            Pair(Vector(-1.0, -1.0, 0.0), SpecialBlock(Material.BIRCH_LOG, Axis.X)),
            Pair(Vector(-1.0, -1.0, -1.0), SpecialBlock(Material.SPRUCE_LOG, Axis.X)),
            Pair(Vector(-1.0, -1.0, 1.0), SpecialBlock(Material.DARK_OAK_LOG, Axis.X))
        )
    ),
    LEFT(
        Vector(0, 0, -1), mapOf(
            Pair(Vector(-1.0, 0.0, -1.0), SpecialBlock(Material.ACACIA_LOG, Axis.Z)),
            Pair(Vector(1.0, 0.0, -1.0), SpecialBlock(Material.ACACIA_LOG, Axis.Z)),
            Pair(Vector(0.0, -1.0, -1.0), SpecialBlock(Material.BIRCH_LOG, Axis.Z)),
            Pair(Vector(-1.0, -1.0, -1.0), SpecialBlock(Material.DARK_OAK_LOG, Axis.Z)),
            Pair(Vector(1.0, -1.0, -1.0), SpecialBlock(Material.SPRUCE_LOG, Axis.Z))
        )
    ),
    RIGHT(
        Vector(0, 0, 1), mapOf(
            Pair(Vector(-1.0, 0.0, 1.0), SpecialBlock(Material.ACACIA_LOG, Axis.Z)),
            Pair(Vector(1.0, 0.0, 1.0), SpecialBlock(Material.ACACIA_LOG, Axis.Z)),
            Pair(Vector(0.0, -1.0, 1.0), SpecialBlock(Material.BIRCH_LOG, Axis.Z)),
            Pair(Vector(-1.0, -1.0, 1.0), SpecialBlock(Material.SPRUCE_LOG, Axis.Z)),
            Pair(Vector(1.0, -1.0, 1.0), SpecialBlock(Material.DARK_OAK_LOG, Axis.Z))
        )
    );

    companion object {
        fun fromID(id: Int): PacmanDirection {
            when (id) {
                1 -> return FORWARD
                2 -> return BACK
                3 -> return LEFT
                4 -> return RIGHT
            }
            throw java.lang.UnsupportedOperationException("ID $id DOES NOT EXIST!")
        }
    }
}

data class SpecialBlock(val material: Material, val axis: Axis? = null)
