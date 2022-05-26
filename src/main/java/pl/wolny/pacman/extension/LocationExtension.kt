package pl.wolny.pacman.extension

import org.bukkit.Location

fun Location.toDataString(): String{
    return "${x}_{$y}_${z}"
}

fun LocationDataStringToCords(dataString: String): DoubleArray{
    return dataString.split("_").map { it.toDouble() }.toDoubleArray()
}