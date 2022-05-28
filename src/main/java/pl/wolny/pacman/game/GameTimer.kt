package pl.wolny.pacman.game

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class GameTimer(private val plugin: JavaPlugin) {

    private var syncTask: BukkitTask? = null
    private var asyncTask: BukkitTask? = null
    private val syncObjectList = mutableListOf<GameObject>()
    private val asyncObjectList = mutableListOf<GameObject>()

    fun start() {
        syncTask = generateRunnable(syncObjectList).runTaskTimer(plugin, 5, 2)
        asyncTask = generateRunnable(asyncObjectList).runTaskTimer(plugin, 5, 2)
    }

    fun stop() {
        syncTask?.cancel()
        asyncTask?.cancel()
    }

    fun register(gameObject: GameObject, async: Boolean = false) {
        if (async) {
            syncObjectList.add(gameObject)
        } else {
            asyncObjectList.add(gameObject)
        }
    }

    private fun generateRunnable(list: MutableList<GameObject>): BukkitRunnable {
        return object : BukkitRunnable() {
            override fun run() {
                for (gameObject in list) {
                    gameObject.tick()
                }
            }
        }
    }
}