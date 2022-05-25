package pl.wolny.pacman.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.pacman.formatMessage
import pl.wolny.pacman.game.GameService
import pl.wolny.pacman.powerup.PowerUp
import pl.wolny.pacman.powerup.PowerUpComponent

class PacmanCommand(private val gameService: GameService, private val powerUpComponent: PowerUpComponent) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val arg = args.getOrNull(0)
        if (arg == null) {
            sender.sendMessage(formatMessage("<red>Podaj argument!"))
            return true
        }
        when (arg) {
            "spawn" -> {
                if (sender !is Player) {
                    sender.sendMessage(formatMessage("<red>Nie jesteś graczem aby to zrobić!"))
                    return true
                }
                gameService.prepare(sender)
                sender.sendMessage(formatMessage("<green>Rejestruję pacmana!"))
            }
            "start" -> {
                gameService.start()
                sender.sendMessage(formatMessage("<green>Startuję grę!"))
            }
            "halt" -> {
                gameService.halt()
                sender.sendMessage(formatMessage("<green>Stopuję pacmana!"))
            }
            "p1" -> {
                powerUpComponent.activate(PowerUp.KILLABLE_PACMAN)
            }
            else -> {
                sender.sendMessage(formatMessage("<red>Złe argumenty!"))
                return true
            }
        }
        return true
    }

}