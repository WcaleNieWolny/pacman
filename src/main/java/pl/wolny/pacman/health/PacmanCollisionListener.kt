package pl.wolny.pacman.health

import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.util.Vector
import org.spigotmc.event.entity.EntityDismountEvent
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.formatMessage
import java.time.Duration
import java.util.UUID

class PacmanCollisionListener(private val spawnPoints: MutableList<Location>, private val pacmanController: PacmanController): Listener {

    var running = false
    private val healthMap = mutableMapOf<UUID, Int>()

    @EventHandler
    private fun onPacmanCollision(event: PacmanCollisionEvent){
        if(!running){
            return
        }
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
            pacmanController.freezePacman(event.pacmanEntity)
            return
        }
        player.health -= 10
    }

    private fun generateRespanLocation(): Location{
        val loc = spawnPoints.random()
        pacmanController.pacmanMap.forEach { (_, v) ->
            if(10 > v.location.distance(loc)){
                return generateRespanLocation()
            }
        }
        return loc
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private fun onPlayerDeath(event: PlayerDeathEvent){
        if(!running){
            return
        }

        val player = event.player

        var health = healthMap[player.uniqueId]
        if(health == null){
            healthMap[player.uniqueId] = 2
            health = 2
        }else{
            if(health-1 == 0){
                event.isCancelled = true
                player.gameMode = GameMode.SPECTATOR
                Bukkit.broadcast(formatMessage("☠ <red>${player.name} stracił wszystkie życia!"))
                player.showTitle(Title.title(formatMessage("<red> UMARŁEŚ!"), formatMessage("Możesz teraz obserować grę"), Title.Times.of(
                    Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO
                )))
                return
            }
            health--
            healthMap[player.uniqueId] = health
        }

        event.isCancelled = true
        val loc = generateRespanLocation()
        player.health = 20.0
        player.teleport(loc)
        Bukkit.getServer().onlinePlayers.forEach{
            player.spawnParticle(Particle.DRAGON_BREATH, loc, 1500, 0.25, 0.1, 0.25 )
        }
        Bukkit.broadcast(formatMessage("☠ <red>${player.name} odchodzi z hukiem! (<dark_red>♥<white>x<green>${health}<red>)"))
    }

    @EventHandler(ignoreCancelled = true)
    private fun onPlayerDismount(event: EntityDismountEvent){
        if(event.dismounted is Firework){
            event.isCancelled = true
        }
    }
}