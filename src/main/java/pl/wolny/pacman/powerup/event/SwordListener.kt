package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.powerup.PowerUp

class SwordListener: Listener {
    @EventHandler
    private fun onJumpBoostActivate(event: PowerupActivateEvent) {
        if(event.powerUp != PowerUp.SWORD){
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
        if(event.powerUp != PowerUp.SWORD){
            return
        }

        Bukkit.getServer().onlinePlayers.forEach{
            it.inventory.remove(Material.WOODEN_SWORD)
        }
    }
}