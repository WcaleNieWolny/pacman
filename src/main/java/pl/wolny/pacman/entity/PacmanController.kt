package pl.wolny.pacman.entity

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Orientable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.util.Vector
import pl.wolny.pacman.extension.getRelative
import pl.wolny.pacman.game.GameObject
import java.io.InvalidObjectException
import java.util.*

class PacmanController : Listener, GameObject {

    private val pacmanMap: MutableMap<UUID, PacmanEntity> = mutableMapOf()
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
        if(!running){
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

        if (pacmanEntity.direction != direction && !checkCollisions(pacmanBlocks, centerBlock.getRelative(direction.vector), direction)) {
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
            return
        }

        if(checkCollisions(pacmanBlocks, centerBlock, pacmanEntity.direction)){
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


        pacmanEntity.blocks.clear()
        pacmanEntity.blocks.addAll(pacmanBlockCopy)
    }

    private fun generateRotatedPacman(
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
                        changedBlock.type = specialBlock.material
                        if (specialBlock.axis != null) {
                            val blockData = changedBlock.blockData
                            (blockData as Orientable).axis = specialBlock.axis
                            changedBlock.blockData = blockData
                        }
                    } else {
                        changedBlock.type = Material.GOLD_BLOCK
                    }
                    blockList.add(changedBlock)
                }
            }
        }
    }

    private fun getCenterPacmanBlock(pacman: PacmanEntity): Block {
        blockLoop@ for (block in pacman.blocks) {
            for (face  in BlockFace.values().filter{
                it == BlockFace.NORTH ||
                it == BlockFace.EAST ||
                it == BlockFace.SOUTH ||
                it == BlockFace.WEST ||
                it == BlockFace.UP ||
                it == BlockFace.DOWN
            }) {
                if (block.getRelative(face).type == Material.AIR || block.getRelative(face).type == Material.STONE_BUTTON) {
                    continue@blockLoop
                }
            }
            return block
        }
        throw InvalidObjectException("Pacman Blocks does not contain center block! This should never happen!")
    }

    private fun checkCollisions(blocks: List<Block>, centre: Block, direction: PacmanDirection): Boolean{
        val vector = direction.vector.clone().multiply(2)
        for (block in blocks) {
            if(block.getRelative(vector).type == Material.BLUE_WOOL){
                return true
            }
        }
        if(centre.getRelative(2, 0, 2).type == Material.BLUE_WOOL){
            return true
        }
        if(centre.getRelative(-2, 0, 2).type == Material.BLUE_WOOL){
            return true
        }
        if(centre.getRelative(2, 0, -2).type == Material.BLUE_WOOL){
            return true
        }
        if(centre.getRelative(-2, 0, -2).type == Material.BLUE_WOOL){
            return true
        }
        return false
    }

    override fun tick() {
        for (pair in pacmanMap) {
            move(pair.value.nextDirection, pair.value)
        }
    }

    fun clear() {
        pacmanMap.forEach { _,v ->
            v.blocks.forEach{
                it.type = Material.AIR
            }
        }
    }

}