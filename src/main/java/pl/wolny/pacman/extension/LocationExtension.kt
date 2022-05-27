package pl.wolny.pacman.extension

import org.bukkit.Location
import org.bukkit.util.Vector

fun Location.toDataString(): String{
    return "${x}_${y}_${z}"
}

fun LocationDataStringToVector(dataString: String): org.bukkit.util.Vector{
    val data = dataString.split("_")
        .map { it.toDouble() }
        .toDoubleArray()
    return Vector(data[0], data[1], data[2])
}