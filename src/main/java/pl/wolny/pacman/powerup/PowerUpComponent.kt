package pl.wolny.pacman.powerup

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.wolny.pacman.game.GameObject
import pl.wolny.pacman.powerup.event.PowerupActivateEvent
import pl.wolny.pacman.powerup.event.PowerupDeactivateEvent

class PowerUpComponent : GameObject {

    private val powerUps: List<powerUpObject> = PowerUp.values().map { powerUpObject(it, 0) }

    override fun tick() {
        powerUps.forEach {
            if (it.time != 0) {
                it.time = it.time - 2
                if (it.time == 0) {
                    Bukkit.getPluginManager().callEvent(PowerupDeactivateEvent(it.powerUp))
                }
            }
        }
    }

    fun isActive(powerUp: PowerUp) = powerUps.filter { it.powerUp == powerUp }[0].time != 0

    fun activate(powerUp: PowerUp, player: Player? = null) {
        powerUps.filter { powerUpObject -> powerUpObject.powerUp == powerUp }[0].time = powerUp.time
        Bukkit.getPluginManager().callEvent(PowerupActivateEvent(powerUp, player))
    }
}

data class powerUpObject(val powerUp: PowerUp, var time: Int)