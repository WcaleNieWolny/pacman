package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.powerup.PowerUp

class JumpBoostListener(private val spawnPoints: MutableList<Location>) : Listener {

    @EventHandler
    private fun onJumpBoostActivate(event: PowerupActivateEvent) {
        if (event.powerUp != PowerUp.JUMP_BOOST) {
            return
        }
        val player = event.player ?: return
        Bukkit.broadcast(formatMessage("<green>${event.player.name} otwrzymuje umiejętności królika!"))
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 100, 4, true))
    }

    @EventHandler
    private fun onJumpBoostDeactivate(event: PowerupDeactivateEvent) {
        if (event.powerUp != PowerUp.JUMP_BOOST) {
            return
        }
        val player = event.player ?: return
        Bukkit.broadcast(formatMessage("<red>${event.player.name} traci umiejętności królika!"))
        if (player.location.y >= -57) {
            //Push player to edge
            val location = player.location
            val nearSpawnPoint: Location = spawnPoints.map { Pair(it, it.distance(location)) }.sortedBy { it.second }
                .first().first //Push player to that location
            val vector = nearSpawnPoint.toVector()
            vector.subtract(location.toVector())
            vector.multiply(0.6)
            player.velocity = vector
        }

    }
}