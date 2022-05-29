package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.point.PointComponent
import pl.wolny.pacman.powerup.PowerUp

class FreePointListener(private val pointComponent: PointComponent): Listener {
    @EventHandler
    private fun onFreePointsActivate(event: PowerupDeactivateEvent) {
        if (event.powerUp != PowerUp.FREE_POINTS) {
            return
        }
        pointComponent.addPoints(event.player!!, points = 35)
        Bukkit.broadcast(formatMessage("<green>${event.player.name} otwrzymuje darmowe 35 puntktów!"))
    }
}