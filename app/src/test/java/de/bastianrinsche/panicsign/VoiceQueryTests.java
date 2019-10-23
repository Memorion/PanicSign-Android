package de.bastianrinsche.panicsign;

import androidx.core.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;


public class VoiceQueryTests {
    private final String RED = "RED";
    private final String ORANGE = "ORANGE";
    private final String YELLOW = "YELLOW";
    private final String GREEN = "GREEN";
    private final String DARKGREEN = "GREEN2";
    private final String TEAL = "TEAL";
    private final String LIGHTBLUE = "LIGHTBLUE";
    private final String BLUE = "BLUE";
    private final String PURPLE = "PURPLE";
    private final String PINK = "PINK";

    private final String[] colorKeys = { RED, ORANGE, YELLOW, GREEN, DARKGREEN,
                                   TEAL, LIGHTBLUE, BLUE, PURPLE, PINK };

    private final ColorUtils colorUtils = new ColorUtils();

    private final String[] englishColors = {"red", "orange", "yellow", "green", "dark green",
            "teal", "light blue", "blue", "purple", "pink"};

    private HashMap<String, String> englishColorMap;

    @Before
    public void setup() {
        assertEquals(colorKeys.length, englishColors.length);

        englishColorMap = new HashMap<>(englishColors.length);
        for (int i = 0; i < englishColors.length; i++) {
            englishColorMap.put(englishColors[i], colorKeys[i]);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_query() {
        String query = "lorem ipsum";
        colorUtils.colorsFromQuery(englishColorMap, query);
    }

    @Test
    public void english_basic() {
        String query = "red and green";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(RED, result.first);
        assertEquals(GREEN, result.second);

        query = "green and red";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(GREEN, result.first);
        assertEquals(RED, result.second);

        query = "yellow and orange";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(YELLOW, result.first);
        assertEquals(ORANGE, result.second);

        query = "purple and pink";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(PURPLE, result.first);
        assertEquals(PINK, result.second);

        query = "teal and orange";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(TEAL, result.first);
        assertEquals(ORANGE, result.second);
    }

    @Test
    public void english_basic_repeated() {
        String query = "red and red";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(RED, result.first);
        assertEquals(RED, result.second);
    }

    @Test
    public void english_basic_blue() {
        String query = "red and light blue";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(RED, result.first);
        assertEquals(LIGHTBLUE, result.second);

        query = "light blue and red";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(LIGHTBLUE, result.first);
        assertEquals(RED, result.second);
    }

    @Test
    public void english_variants_blue() {
        String query = "blue and light blue";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(BLUE, result.first);
        assertEquals(LIGHTBLUE, result.second);

        query = "light blue and blue";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(LIGHTBLUE, result.first);
        assertEquals(BLUE, result.second);
    }

    @Test
    public void english_basic_green() {
        String query = "red and dark green";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(RED, result.first);
        assertEquals(DARKGREEN, result.second);

        query = "dark green and red";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(DARKGREEN, result.first);
        assertEquals(RED, result.second);
    }

    @Test
    public void english_variants_green() {
        String query = "green and dark green";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(GREEN, result.first);
        assertEquals(DARKGREEN, result.second);

        query = "dark green and green";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(DARKGREEN, result.first);
        assertEquals(GREEN, result.second);
    }

    @Test
    public void english_variants_both_basic() {
        String query = "blue and green";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(BLUE, result.first);
        assertEquals(GREEN, result.second);

        query = "green and blue";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(GREEN, result.first);
        assertEquals(BLUE, result.second);
    }

    @Test
    public void english_variants_both_complex() {
        String query = "dark green and light blue";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(DARKGREEN, result.first);
        assertEquals(LIGHTBLUE, result.second);

        query = "light blue and dark green";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(LIGHTBLUE, result.first);
        assertEquals(DARKGREEN, result.second);
    }

    @Test
    public void english_variants_both_mixed() {
        String query = "blue and dark green";
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(BLUE, result.first);
        assertEquals(DARKGREEN, result.second);

        query = "dark green and blue";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(DARKGREEN, result.first);
        assertEquals(BLUE, result.second);

        query = "green and light blue";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(GREEN, result.first);
        assertEquals(LIGHTBLUE, result.second);

        query = "light blue and green";
        result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(LIGHTBLUE, result.first);
        assertEquals(GREEN, result.second);
    }

    @Test
    public void long_query() {
        StringBuilder x = new StringBuilder();
        x.append("light blue ");
        x.append("green");
        for (int i = 0; i < englishColors.length*5000; i++) {
            x.append(englishColors[i % 10]);
        }

        String query = x.toString();
        Pair<String, String> result = colorUtils.colorsFromQuery(englishColorMap, query);
        assertEquals(LIGHTBLUE, result.first);
        assertEquals(GREEN, result.second);
    }

}
