package de.bastianrinsche.panicsign;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class ColorUtils {

    private String[] colorKeys;
    private HashMap<String, String> colorRGBMap;
    HashMap<String, String> intlNameKeyMap;
    HashMap<String, Integer> colorMap;

    //Used in Unit Tests
    ColorUtils() {}

    ColorUtils(Context context) {

        Resources resources = context.getResources();

        colorKeys = resources.getStringArray(R.array.colors_keys);

        String[] intlColors = resources.getStringArray(R.array.colors_spoken);
        if (intlColors.length != colorKeys.length) {
            throw new IllegalStateException("The amount of spoken colors and color keys don't match");
        }
        intlNameKeyMap = new HashMap<>(intlColors.length);
        for (int i = 0; i < intlColors.length; i++) {
            intlNameKeyMap.put(intlColors[i], colorKeys[i]);
        }

        TypedArray typedArray = resources.obtainTypedArray(R.array.colors_ids);
        colorMap = new HashMap<>(typedArray.length());
        for (int i = 0; i < colorKeys.length; i++) {
            colorMap.put(colorKeys[i], typedArray.getResourceId(i, R.color.purple));
        }
        typedArray.recycle();

        String[] rgbStrings = resources.getStringArray(R.array.colors_keys_rgb);
        if (rgbStrings.length != colorKeys.length) {
            throw new IllegalStateException("The amount of rgb Strings and color keys don't match");
        }
        colorRGBMap = new HashMap<>(rgbStrings.length);
        for (int i = 0; i < rgbStrings.length; i++) {
            colorRGBMap.put(colorKeys[i], rgbStrings[i]);
        }
    }

    static int resolveColor(Context context, HashMap<String, Integer> colorIDMap, String color) {
        Integer id = colorIDMap.get(color);
        if (id != null) { return ContextCompat.getColor(context, id); }
        Log.e("ColorUtils", "Searched colorMap for invalid String: " + color);
        return ContextCompat.getColor(context, R.color.purple);
    }

    String colorToRGBString(String color) {
        return colorRGBMap.get(color);
    }

    Pair<String, String> colorsFromQuery(HashMap<String, String> intlNameKeyMap, String query) {

        List<String> possibleColors = new ArrayList<>(intlNameKeyMap.keySet().size());
        possibleColors.addAll(intlNameKeyMap.keySet());

        List<String> foundColors = new LinkedList<>();

        int searchedTo = 0;
        while (searchedTo < query.length()) {
            for (String searchColor : possibleColors) {
                boolean found = (searchedTo + searchColor.length() <= query.length()
                        && query.substring(searchedTo, searchedTo + searchColor.length())
                                .equalsIgnoreCase(searchColor));

                if (found) {
                    foundColors.add(intlNameKeyMap.get(searchColor));
                    searchedTo += searchColor.length();
                }
            }
            searchedTo++;
        }

        switch (foundColors.size()) {
            case 0:
                throw new IllegalArgumentException("No color found");
            case 1:
                // just use the color for both parts
                return new Pair<>(foundColors.get(0), foundColors.get(0));
            default:
                return new Pair<>(foundColors.get(0), foundColors.get(1));
        }
    }

    Pair<String, String> getRandomColors() {
        Random rand = new Random();

        int topRandom = rand.nextInt(intlNameKeyMap.size());
        int botRandom = rand.nextInt(intlNameKeyMap.size());

        return new Pair<>(colorKeys[topRandom], colorKeys[botRandom]);
    }

}
