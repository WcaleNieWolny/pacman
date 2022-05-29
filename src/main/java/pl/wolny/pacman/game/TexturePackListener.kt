package pl.wolny.pacman.game

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import pl.wolny.pacman.formatMessage

class TexturePackListener: Listener {

    @EventHandler
    private fun onTexturePackRejection(event: PlayerResourcePackStatusEvent){
        if(event.status == PlayerResourcePackStatusEvent.Status.DECLINED){
            event.player.kick(formatMessage("<red>Proszę zaakceptować paczkę zasobów, aby wziąć udział w evencie!"))
            return
        }
        if(event.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD){
            event.player.kick(formatMessage("<red>Proszę wejść na serwer ponownie!"))
            return
        }
    }
}