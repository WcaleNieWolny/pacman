package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.health.HealthComponent
import pl.wolny.pacman.point.PointComponent
import pl.wolny.pacman.powerup.PowerUp
import kotlin.math.roundToInt

class SwordListener(private val healthComponent: HealthComponent, private val pointComponent: PointComponent) :
    Listener {
    @EventHandler
    private fun onJumpBoostActivate(event: PowerupActivateEvent) {
        if (event.powerUp != PowerUp.SWORD) {
            return
        }
        val player = event.player ?: return
        player.sendMessage(formatMessage("<green>Zyskujesz miecz! Masz 30 sekund za zabicie kogoś. PS: Ukradniesz 35% jej punktów ;)"))

        val item = ItemStack(Material.WOODEN_SWORD)
        val meta = item.itemMeta as Damageable
        meta.damage = 58
        item.itemMeta = meta
        player.inventory.setItem(1, item)
    }

    @EventHandler
    private fun onJumpBoostDeactivate(event: PowerupDeactivateEvent) {
        if (event.powerUp != PowerUp.SWORD) {
            return
        }

        Bukkit.getServer().onlinePlayers.forEach {
            it.inventory.remove(Material.WOODEN_SWORD)
        }
    }

    @EventHandler
    private fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        val victim = event.entity

        val loc = victim.location.clone()

        if (attacker !is Player || victim !is Player) {
            return
        }

        val item = attacker.inventory.itemInMainHand
        if (item.type != Material.WOODEN_SWORD) {
            event.isCancelled = true
            return
        }

        healthComponent.kill(victim)
        Bukkit.broadcast(formatMessage("☠ <red>${attacker.name} zabija ${victim.name}! (<dark_red>♥<white>x<green>${healthComponent.healthMap[victim.uniqueId]}<red>)"))
        //POINT STEALING

        val points = (pointComponent.points(victim).toDouble() * 0.35).roundToInt()

        pointComponent.addPoints(victim, 0 - points)
        pointComponent.addPoints(attacker, points)

        Bukkit.broadcast(formatMessage("<red>${attacker.name} kradnie $points punktów od ${victim.name}!"))
        //particle

        Bukkit.getServer().onlinePlayers.forEach {
            it.spawnParticle(
                Particle.BLOCK_CRACK,
                loc,
                150,
                0.2,
                0.2,
                0.2,
                Bukkit.getServer().createBlockData(Material.REDSTONE_BLOCK)
            )
        }

    }
}