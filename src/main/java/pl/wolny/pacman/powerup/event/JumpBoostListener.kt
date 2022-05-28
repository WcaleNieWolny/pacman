package pl.wolny.pacman.powerup.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pl.wolny.pacman.powerup.PowerUp

class JumpBoostListener(): Listener {

    @EventHandler
    private fun onJumpBoostActivate(event: PowerupActivateEvent) {
        if(event.powerUp != PowerUp.JUMP_BOOST){
            return
        }
        val player = event.player ?: return
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 5, 4, true))
    }

    @EventHandler
    private fun onJumpBoostDeactivate(event: PowerupDeactivateEvent) {
        if(event.powerUp != PowerUp.JUMP_BOOST){
            return
        }
        val player = event.player ?: return
        if(player.location.y >= 57){
            //Push player to edge
            //val nearSpawnPoints =
        }

    }
}