package pl.wolny.pacman.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.pacman.entity.PacmanController
import pl.wolny.pacman.formatMessage

class PacmanCommand(private val pacmanController: PacmanController) : CommandExecutor {
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
                val block = sender.getTargetBlock(5)
                if (block == null) {
                    sender.sendMessage(formatMessage("<red>Nie patrzysz na blok!"))
                    return true
                }
                pacmanController.registerPacman(block, sender)
                sender.sendMessage(formatMessage("<green>Rejestruję pacmana!"))
            }
            else -> {
                sender.sendMessage(formatMessage("<red>Złe argumenty!"))
                return true
            }
        }
        return true
    }

}