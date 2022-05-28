package pl.wolny.pacman.health

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import pl.wolny.pacman.entity.PacmanEntity

class PacmanCollisionEvent(val player: Player, val pacmanEntity: PacmanEntity) : Event() {
    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList = getHandlerList()

}