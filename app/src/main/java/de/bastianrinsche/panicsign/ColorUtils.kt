package de.bastianrinsche.panicsign

import android.content.Context
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.*

class ColorUtils {
    private lateinit var colorKeys: Array<String>
    private lateinit var colorRGBMap: HashMap<String, String>
    lateinit var intlNameKeyMap: HashMap<String, String>
    lateinit var colorMap: HashMap<String, Int>

    //Used in Unit Tests
    constructor()

    constructor(context: Context) {
        val resources = context.resources
        colorKeys = resources.getStringArray(R.array.colors_keys)
        val intlColors = resources.getStringArray(R.array.colors_spoken)
        check(intlColors.size == colorKeys.size) { "The amount of spoken colors and color keys don't match" }
        intlNameKeyMap = HashMap(intlColors.size)
        for (i in intlColors.indices) {
            intlNameKeyMap[intlColors[i]] = colorKeys[i]
        }
        val typedArray = resources.obtainTypedArray(R.array.colors_ids)
        colorMap = HashMap(typedArray.length())
        for (i in colorKeys.indices) {
            colorMap[colorKeys[i]] = typedArray.getResourceId(i, R.color.purple)
        }
        typedArray.recycle()
        val rgbStrings = resources.getStringArray(R.array.colors_keys_rgb)
        check(rgbStrings.size == colorKeys.size) { "The amount of rgb Strings and color keys don't match" }
        colorRGBMap = HashMap(rgbStrings.size)
        for (i in rgbStrings.indices) {
            colorRGBMap[colorKeys[i]] = rgbStrings[i]
        }
    }

    fun colorToRGBString(color: String): String {
        return colorRGBMap[color]!!
    }

    fun colorsFromQuery(intlNameKeyMap: HashMap<String, String>, query: String): Pair<String, String> {
        val possibleColors: MutableList<String> = ArrayList(intlNameKeyMap.keys.size)
        intlNameKeyMap.keys.toCollection(possibleColors)
        val foundColors: MutableList<String?> = LinkedList()
        var searchedTo = 0
        while (searchedTo < query.length) {
            for (searchColor in possibleColors) {
                val found = (searchedTo + searchColor.length <= query.length
                        && query.substring(searchedTo, searchedTo + searchColor.length)
                        .equals(searchColor, ignoreCase = true))
                if (found) {
                    foundColors.add(intlNameKeyMap[searchColor])
                    searchedTo += searchColor.length - 1
                }
            }
            if (foundColors.size == 2) {
                break
            }
            searchedTo++
        }
        return when (foundColors.size) {
            0 -> throw IllegalArgumentException("No color found")
            1 -> Pair(foundColors[0]!!, foundColors[0]!!) // just use the color for both parts
            else -> Pair(foundColors[0]!!, foundColors[1]!!)
        }
    }

    val randomColors: Pair<String, String>
        get() {
            val rand = Random()
            val topRandom = rand.nextInt(intlNameKeyMap.size)
            val botRandom = rand.nextInt(intlNameKeyMap.size)
            return Pair(colorKeys[topRandom], colorKeys[botRandom])
        }

    companion object {
        @JvmStatic
        fun resolveColor(context: Context?, colorIDMap: HashMap<String, Int>, color: String?): Int {
            val id = colorIDMap[color]
            if (id != null) {
                return ContextCompat.getColor(context!!, id)
            }
            Timber.e("Searched colorMap for invalid String: %s", color)
            return ContextCompat.getColor(context!!, R.color.purple)
        }
    }
}
