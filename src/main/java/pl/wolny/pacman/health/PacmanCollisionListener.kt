package pl.wolny.pacman.health

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector
import pl.wolny.pacman.formatMessage

class PacmanCollisionListener: Listener {

    @EventHandler
    fun onPacmanCollision(event: PacmanCollisionEvent){
        val player = event.player
        if(player.health <= 10){
            val firework: Firework = player.world.spawnEntity(
                event.pacmanEntity.location.clone().add(Vector(0, 3, 0)),
                EntityType.FIREWORK
            ) as Firework
            val meta = firework.fireworkMeta
            meta.power = 2
            meta.addEffect(FireworkEffect.builder().withColor(Color.RED).build())
            firework.fireworkMeta = meta
            firework.addPassenger(player)
            return
        }
        player.health -= 10
    }
}