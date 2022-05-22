package pl.wolny.pacman.powerup.event

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pl.wolny.pacman.formatMessage

class KillablePacmanListener: Listener {

    @EventHandler
    private fun onKillablePacmanDeactivate(event: PowerupDeactivateEvent){
        Bukkit.broadcast(formatMessage("- > ${event.powerUp}"))
    }

    @EventHandler
    private fun onKillablePacmanActivate(event: PowerupActivateEvent){
        Bukkit.broadcast(formatMessage("+ > ${event.powerUp}"))
    }

}