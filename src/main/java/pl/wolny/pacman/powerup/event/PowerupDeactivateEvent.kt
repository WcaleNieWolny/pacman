package pl.wolny.pacman.powerup.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import pl.wolny.pacman.powerup.PowerUp

class PowerupDeactivateEvent(val powerUp: PowerUp, val player: Player? = null) : Event() {
    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList = getHandlerList()

}