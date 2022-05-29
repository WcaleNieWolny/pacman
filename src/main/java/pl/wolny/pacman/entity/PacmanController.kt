package pl.wolny.pacman.entity

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Orientable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import pl.wolny.pacman.extension.getRelative
import pl.wolny.pacman.game.GameObject
import pl.wolny.pacman.health.PacmanCollisionEvent
import java.util.*

//THAT IS A FRICKING SPAGETTI

class PacmanController : Listener, GameObject {

    var killablePacman = false
    private var killablePacmanChange = false

    val pacmanMap: MutableMap<UUID, PacmanEntity> = mutableMapOf()
    var running = false


    fun registerPacman(block: Block, player: Player) {

        val blockList = mutableListOf<Block>()
        val direction = PacmanDirection.RIGHT

        generateRotatedPacman(block, direction, blockList)
        val entity = PacmanEntity(blockList, block.location, direction)
        pacmanMap[player.uniqueId] = entity
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private fun onHotbarItemChange(event: PlayerItemHeldEvent) {
        if (!running) {
            return
        }
        val item = event.player.inventory.getItem(event.newSlot) ?: return
        if (!item.itemMeta.hasCustomModelData()) {
            return
        }

        if (item.type != Material.PAPER) {
            return
        }

        val pacman = pacmanMap[event.player.uniqueId] ?: return

        if(pacman.freeze != 0){
            event.isCancelled = true
            return
        }

        val itemCustomModel = item.itemMeta.customModelData
        val direction = PacmanDirection.fromID(itemCustomModel)

        pacman.nextDirection = direction

    }

    private fun move(direction: PacmanDirection, pacmanEntity: PacmanEntity) {
        val pacmanBlocksDataCopy = mutableMapOf<Location, BlockData>()
        val pacmanBlockCopy = mutableListOf<Block>()
        val pacmanBlocks = pacmanEntity.blocks
        val centerBlock = getCenterPacmanBlock(pacmanEntity)

        pacmanBlocks.forEach {
            pacmanBlocksDataCopy[it.location] = it.blockData.clone()
        }

        if (pacmanEntity.direction != direction && !checkCollisions(
                pacmanBlocks,
                centerBlock.getRelative(direction.vector),
                direction
            )
        ) {
            generateRotatedPacman(
                getCenterPacmanBlock(pacmanEntity).getRelative(direction.vector),
                direction,
                pacmanBlockCopy
            )
            pacmanBlocks.filterNot { pacmanBlockCopy.contains(it) }.forEach {
                it.type = Material.AIR
            }
            pacmanEntity.direction = direction
            pacmanEntity.blocks.clear()
            pacmanEntity.blocks.addAll(pacmanBlockCopy)
            pacmanEntity.location.add(direction.vector)
            checkPlayerCollision(pacmanEntity)
            return
        }

        if (checkCollisions(pacmanBlocks, centerBlock, pacmanEntity.direction)) {
            return
        }

        if (killablePacmanChange) {
            generateRotatedPacman(
                getCenterPacmanBlock(pacmanEntity).getRelative(pacmanEntity.direction.vector),
                pacmanEntity.direction,
                pacmanBlockCopy
            )
            pacmanBlocks.filterNot { pacmanBlockCopy.contains(it) }.forEach {
                it.type = Material.AIR
            }
            killablePacmanChange = false
            pacmanEntity.blocks.clear()
            pacmanEntity.blocks.addAll(pacmanBlockCopy)
            pacmanEntity.location.add(pacmanEntity.direction.vector)
            checkPlayerCollision(pacmanEntity)
            return
        }

        pacmanBlocks.forEach {
            val relative =
                it.getRelative(pacmanEntity.direction.vector)
            relative.blockData = pacmanBlocksDataCopy[it.location]!!
            pacmanBlockCopy.add(relative)
        }

        pacmanBlocks.filterNot { pacmanBlockCopy.contains(it) }.forEach {
            it.type = Material.AIR
        }

        pacmanEntity.location.add(pacmanEntity.direction.vector)
        pacmanEntity.blocks.clear()
        pacmanEntity.blocks.addAll(pacmanBlockCopy)
        checkPlayerCollision(pacmanEntity)
    }

    fun generateRotatedPacman(
        block: Block,
        direction: PacmanDirection,
        blockList: MutableList<Block>
    ) {
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {

                    val changedBlock = block.getRelative(x, y, z)
                    val specialBlock = direction.specialBlocks[Vector(x, y, z)]

                    if (specialBlock != null) {
                        if (!killablePacman) {
                            changedBlock.type = specialBlock.materials.first
                        } else {
                            changedBlock.type = specialBlock.materials.second
                        }
                        if (specialBlock.axis != null && changedBlock.blockData is Orientable) {
                            val blockData = changedBlock.blockData
                            (blockData as Orientable).axis = specialBlock.axis
                            changedBlock.blockData = blockData
                        }
                    } else {
                        if (!killablePacman) {
                            changedBlock.type = Material.GOLD_BLOCK
                        } else {
                            changedBlock.type = Material.DIAMOND_BLOCK
                        }
                    }
                    blockList.add(changedBlock)
                }
            }
        }
    }

    private fun getCenterPacmanBlock(pacman: PacmanEntity): Block {
        return pacman.location.block
    }

    private fun checkCollisions(blocks: List<Block>, centre: Block, direction: PacmanDirection): Boolean {
        val vector = direction.vector.clone().multiply(2)
        for (block in blocks) {
            if (block.getRelative(vector).type == Material.BLUE_WOOL) {
                return true
            }
        }
        if (centre.getRelative(2, 0, 2).type == Material.BLUE_WOOL) {
            return true
        }
        if (centre.getRelative(-2, 0, 2).type == Material.BLUE_WOOL) {
            return true
        }
        if (centre.getRelative(2, 0, -2).type == Material.BLUE_WOOL) {
            return true
        }
        if (centre.getRelative(-2, 0, -2).type == Material.BLUE_WOOL) {
            return true
        }
        return false
    }

    fun checkPlayerCollision(pacman: PacmanEntity){
        val loc1 = pacman.location.clone()
        val loc2 = pacman.location.clone()
        val vector = Vector(3, 3, 3)
        loc1.add(vector)
        loc2.subtract(vector)
        pacman.location.world.getNearbyEntities(BoundingBox.of(loc1, loc2)){
            it is Player
        }.map { it as Player }.forEach{
            Bukkit.getPluginManager().callEvent(PacmanCollisionEvent(it, pacman))
        }
    }

    override fun tick() {
        for (pair in pacmanMap) {
            if(pair.value.freeze != 0){
                pair.value.freeze -=2
                continue
            }
            move(pair.value.nextDirection, pair.value)
        }
    }

    fun clear() {
        pacmanMap.forEach { _, v ->
            v.blocks.forEach {
                it.type = Material.AIR
            }
        }
    }

    fun handleKillable() {
        killablePacmanChange = true
    }

    fun freezePacman(pacmanEntity: PacmanEntity) {
        pacmanEntity.freeze = 60
    }

}