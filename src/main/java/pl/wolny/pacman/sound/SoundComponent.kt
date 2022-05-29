package pl.wolny.pacman.sound

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.game.GameObject

class SoundComponent(private val pacmanController: PacmanController) : GameObject {

    var running = false

    override fun tick() {
        if (!running) {
            return
        }

        Bukkit.getServer().onlinePlayers.forEach { player ->
            pacmanController.pacmanMap.forEach { (_, v) ->
                val distance = player.location.distance(v.location)

                if (10 >= distance) {
                    player.playSound(player.location, Sound.BLOCK_ANVIL_PLACE, SoundCategory.AMBIENT, 0.3f, 1f)
                    return
                }
                if (20 >= distance) {
                    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, SoundCategory.AMBIENT, 1.2f, 1f)
                    return
                }
                if (30 >= distance) {
                    player.playSound(player.location, Sound.BLOCK_ANVIL_STEP, SoundCategory.AMBIENT, 1f, 1f)
                    return
                }
            }
        }
    }
}