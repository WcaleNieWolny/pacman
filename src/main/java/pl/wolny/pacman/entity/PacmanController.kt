package pl.wolny.pacman.entity

import org.bukkit.Axis
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Orientable
import org.bukkit.block.data.Rotatable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.util.Vector
import java.io.InvalidObjectException
import java.util.*

class PacmanController: Listener {

    private val pacmanMap: MutableMap<UUID, PacmanEntity> = mutableMapOf()

    fun registerPacman(block: Block, player: Player) {

        val blockList = mutableListOf<Block>()
        val direction = PacmanDirection.LEFT

        generateRotatedPacman(block, direction, blockList)
        val entity = PacmanEntity(blockList, block.location)
        pacmanMap[player.uniqueId] = entity
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private fun onHotbarItemChange(event: PlayerItemHeldEvent){
        val item = event.player.inventory.getItem(event.newSlot) ?: return
        if(!item.itemMeta.hasCustomModelData()){
            return
        }

        if(item.type != Material.PAPER){
            return
        }

        val pacman = pacmanMap[event.player.uniqueId] ?: return
        val itemCustomModel = item.itemMeta.customModelData
        val direction = PacmanDirection.fromID(itemCustomModel)

        move(direction, pacman)

    }

    private fun move(direction: PacmanDirection, pacmanEntity: PacmanEntity){
        val pacmanBlocksDataCopy = mutableMapOf<Location, BlockData>()
        val pacmanBlockCopy = mutableListOf<Block>()
        val pacmanBlocks = pacmanEntity.blocks

        pacmanBlocks.forEach{
            pacmanBlocksDataCopy[it.location] = it.blockData.clone()
        }

        pacmanBlocks.forEach {
            val relative = it.getRelative(direction.vector.x.toInt(), direction.vector.y.toInt(), direction.vector.z.toInt())
            relative.blockData = pacmanBlocksDataCopy[it.location]!!
            pacmanBlockCopy.add(relative)
        }

        pacmanBlocks.filterNot { pacmanBlockCopy.contains(it) }.forEach{
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

    private fun getCenterPacmanBlock(pacman: PacmanEntity): Block{
        pacman.blocks.forEach { block ->
            for (face in BlockFace.values()) {
                if(block.getRelative(face).type == Material.AIR){
                    continue
                }
                return block
            }
        }
        throw InvalidObjectException("Pacman Blocks does not contain center block! This should never happen!")
    }

}