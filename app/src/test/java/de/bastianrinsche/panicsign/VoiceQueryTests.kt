package de.bastianrinsche.panicsign

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class VoiceQueryTests {
    private val colorUtils = ColorUtils()
    private val englishColors = arrayOf("red", "orange", "yellow", "green", "dark green",
            "teal", "light blue", "blue", "purple", "pink")
    private lateinit var englishColorMap: HashMap<String, String>

    @Before
    fun setup() {
        Assert.assertEquals(colorKeys.size.toLong(), englishColors.size.toLong())
        englishColorMap = HashMap(englishColors.size)
        for (i in englishColors.indices) {
            englishColorMap[englishColors[i]] = colorKeys[i]
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalid_query() {
        val query = "lorem ipsum"
        colorUtils.colorsFromQuery(englishColorMap, query)
    }

    @Test
    fun english_basic() {
        var query = "red and green"
        var result: Pair<String?, String?> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(RED, result.first)
        Assert.assertEquals(GREEN, result.second)
        query = "green and red"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(GREEN, result.first)
        Assert.assertEquals(RED, result.second)
        query = "yellow and orange"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(YELLOW, result.first)
        Assert.assertEquals(ORANGE, result.second)
        query = "purple and pink"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(PURPLE, result.first)
        Assert.assertEquals(PINK, result.second)
        query = "teal and orange"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(TEAL, result.first)
        Assert.assertEquals(ORANGE, result.second)
    }

    @Test
    fun english_basic_repeated() {
        val query = "red and red"
        val result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(RED, result.first)
        Assert.assertEquals(RED, result.second)
    }

    @Test
    fun english_basic_blue() {
        var query = "red and light blue"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(RED, result.first)
        Assert.assertEquals(LIGHTBLUE, result.second)
        query = "light blue and red"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(LIGHTBLUE, result.first)
        Assert.assertEquals(RED, result.second)
    }

    @Test
    fun english_variants_blue() {
        var query = "blue and light blue"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(BLUE, result.first)
        Assert.assertEquals(LIGHTBLUE, result.second)
        query = "light blue and blue"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(LIGHTBLUE, result.first)
        Assert.assertEquals(BLUE, result.second)
    }

    @Test
    fun english_basic_green() {
        var query = "red and dark green"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(RED, result.first)
        Assert.assertEquals(DARKGREEN, result.second)
        query = "dark green and red"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(DARKGREEN, result.first)
        Assert.assertEquals(RED, result.second)
    }

    @Test
    fun english_variants_green() {
        var query = "green and dark green"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(GREEN, result.first)
        Assert.assertEquals(DARKGREEN, result.second)
        query = "dark green and green"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(DARKGREEN, result.first)
        Assert.assertEquals(GREEN, result.second)
    }

    @Test
    fun english_variants_both_basic() {
        var query = "blue and green"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(BLUE, result.first)
        Assert.assertEquals(GREEN, result.second)
        query = "green and blue"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(GREEN, result.first)
        Assert.assertEquals(BLUE, result.second)
    }

    @Test
    fun english_variants_both_complex() {
        var query = "dark green and light blue"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(DARKGREEN, result.first)
        Assert.assertEquals(LIGHTBLUE, result.second)
        query = "light blue and dark green"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(LIGHTBLUE, result.first)
        Assert.assertEquals(DARKGREEN, result.second)
    }

    @Test
    fun english_variants_both_mixed() {
        var query = "blue and dark green"
        var result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(BLUE, result.first)
        Assert.assertEquals(DARKGREEN, result.second)
        query = "dark green and blue"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(DARKGREEN, result.first)
        Assert.assertEquals(BLUE, result.second)
        query = "green and light blue"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(GREEN, result.first)
        Assert.assertEquals(LIGHTBLUE, result.second)
        query = "light blue and green"
        result = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(LIGHTBLUE, result.first)
        Assert.assertEquals(GREEN, result.second)
    }

    @Test
    fun long_query() {
        val x = StringBuilder()
        x.append("light blue ")
        x.append("green")
        for (i in 0 until englishColors.size * 5000) {
            x.append(englishColors[i % 10])
        }
        val query = x.toString()
        val result: Pair<String, String> = colorUtils.colorsFromQuery(englishColorMap, query)
        Assert.assertEquals(LIGHTBLUE, result.first)
        Assert.assertEquals(GREEN, result.second)
    }

    companion object {
        private const val RED = "RED"
        private const val PINK = "PINK"
        private const val PURPLE = "PURPLE"
        private const val BLUE = "BLUE"
        private const val LIGHTBLUE = "LIGHTBLUE"
        private const val TEAL = "TEAL"
        private const val DARKGREEN = "GREEN2"
        private const val GREEN = "GREEN"
        private const val YELLOW = "YELLOW"
        private const val ORANGE = "ORANGE"
        private val colorKeys = arrayOf(
                RED, ORANGE, YELLOW, GREEN, DARKGREEN,
                TEAL, LIGHTBLUE, BLUE, PURPLE, PINK
        )
    }
}