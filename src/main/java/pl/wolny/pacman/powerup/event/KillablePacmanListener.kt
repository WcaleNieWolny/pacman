package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.entity.PacmanDirection
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.powerup.PowerUp

class KillablePacmanListener(private val pacmanController: PacmanController) : Listener {

    @EventHandler
    private fun onKillablePacmanDeactivate(event: PowerupDeactivateEvent) {
        if (event.powerUp != PowerUp.KILLABLE_PACMAN) {
            return
        }
        pacmanController.killablePacman = false
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("<red>Pacman odzyskał siły! Uciekaj!"))

        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.inventory.remove(Material.SNOWBALL)
        }
    }

    @EventHandler
    private fun onKillablePacmanActivate(event: PowerupActivateEvent) {
        if (event.powerUp != PowerUp.KILLABLE_PACMAN) {
            return
        }
        pacmanController.killablePacman = true
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("<green>Pacman jest słaby! Strzel w niego śnieżką aby go zabić!"))

        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.inventory.setItem(0, ItemStack(Material.SNOWBALL, 1))
        }

    }

    @EventHandler
    private fun onSnowballHitEvent(event: ProjectileHitEvent) {
        val block = event.hitBlock ?: return

        if (pacmanController.killablePacman) {
            pacmanController.pacmanMap.forEach { (_, v) ->
                if (v.blocks.contains(block)) {
                    v.blocks.forEach {
                        it.type = Material.AIR
                    }
                    val block = Bukkit.getWorld("world")!!.getBlockAt(-93, -57, 73)
                    val blocks = mutableListOf<Block>()
                    pacmanController.generateRotatedPacman(
                        block,
                        PacmanDirection.RIGHT,
                        blocks
                    )
                    v.location = block.location //Make sure that pacman does not get teleported to old location
                    v.blocks.clear()
                    v.blocks.addAll(blocks)
                    v.direction = PacmanDirection.RIGHT
                }
            }

            Bukkit.getServer().onlinePlayers.forEach {
                it.spawnParticle(Particle.EXPLOSION_LARGE, block.location, 400, 0.75, 0.75, 0.75)
            }
        }
    }
}