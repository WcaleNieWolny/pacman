package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.formatMessage

class KillablePacmanListener(private val pacmanController: PacmanController): Listener {

    @EventHandler
    private fun onKillablePacmanDeactivate(event: PowerupDeactivateEvent){
        pacmanController.killablePacman = false
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("- > ${event.powerUp}"))
    }

    @EventHandler
    private fun onKillablePacmanActivate(event: PowerupActivateEvent){
        pacmanController.killablePacman = true
        pacmanController.handleKillable()
        Bukkit.broadcast(formatMessage("+ > ${event.powerUp}"))
    }

}