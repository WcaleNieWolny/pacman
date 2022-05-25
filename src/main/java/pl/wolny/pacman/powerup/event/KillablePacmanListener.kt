package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.entity.PacmanDirection
import pl.wolny.pacman.formatMessage

class KillablePacmanListener(private val pacmanController: PacmanController): Listener {

    @EventHandler
    private fun onKillablePacmanDeactivate(event: PowerupDeactivateEvent){
        pacmanController.killablePacman = false
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("- > ${event.powerUp}"))

        Bukkit.getServer().onlinePlayers.forEach{ player ->
            player.inventory.setItem(0, ItemStack(Material.SNOWBALL, 1))
        }
    }

    @EventHandler
    private fun onKillablePacmanActivate(event: PowerupActivateEvent){
        pacmanController.killablePacman = true
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("+ > ${event.powerUp}"))

        Bukkit.getServer().onlinePlayers.forEach{ player ->
            player.inventory.remove(Material.SNOWBALL)
        }
    }

    @EventHandler
    private fun onSnowballHitEvent(event: ProjectileHitEvent){
        val block = event.hitBlock ?: return

        if(pacmanController.killablePacman){
            pacmanController.pacmanMap.forEach { (_, v) ->  if(v.blocks.contains(block)){
                v.blocks.forEach{
                    it.type = Material.AIR
                }
                val blocks = mutableListOf<Block>()
                pacmanController.generateRotatedPacman(
                    Bukkit.getWorld("world")!!.getBlockAt(-93, -57, 73),
                    PacmanDirection.RIGHT,
                    blocks
                )
                v.blocks.clear()
                v.blocks.addAll(blocks)
                v.direction = PacmanDirection.RIGHT
            }}
        }
    }
}