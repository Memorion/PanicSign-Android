package de.bastianrinsche.panicsign;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static de.bastianrinsche.panicsign.PanicSign.getAppContext;

class ColorUtils {

    private static String[] colorKeys = getAppContext().getResources()
            .getStringArray(R.array.colors_keys);

    static HashMap<String, String> intlNameKeyMap;
    static {
        String[] intlColors = getAppContext().getResources().getStringArray(R.array.colors_spoken);
        if (intlColors.length != colorKeys.length) {
            throw new IllegalStateException("The amount of spoken colors and color keys don't match");
        }
        intlNameKeyMap = new HashMap<>(intlColors.length);
        for (int i = 0; i < intlColors.length; i++) {
            intlNameKeyMap.put(intlColors[i], colorKeys[i]);
        }
    }

    static HashMap<String, Integer> colorMap;
    static {
        TypedArray typedArray = getAppContext().getResources().obtainTypedArray(R.array.colors_ids);
        colorMap = new HashMap<>(typedArray.length());
        for (int i = 0; i < colorKeys.length; i++) {
            colorMap.put(colorKeys[i], typedArray.getResourceId(i, R.color.purple));
        }
        typedArray.recycle();
    }

    private static HashMap<String, String> colorRGBMap;
    static {
        String[] rgbStrings = getAppContext().getResources().getStringArray(R.array.colors_keys_rgb);
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

    static String colorToRGBString(String color) {
        return colorRGBMap.get(color);
    }

    static Pair<String, String> colorsFromQuery(HashMap<String, String> intlNameKeyMap, String query) {
        Log.d("QUERY", query);

        List<String> possibleColors = new ArrayList<>(intlNameKeyMap.keySet().size());
        possibleColors.addAll(intlNameKeyMap.keySet());

        List<String> foundColors = new LinkedList<>();

        for (int i = 0; i < query.length(); i++) {
            Iterator<String> searchIter = possibleColors.iterator();
            while (searchIter.hasNext()) {
                String search = searchIter.next();
                if (i + search.length() <= query.length()
                        && query.substring(i, i + search.length()).equalsIgnoreCase(search)) {
                    searchIter.remove();
                    foundColors.add(intlNameKeyMap.get(search));
                }
            }
        }

        switch (foundColors.size()) {
            case 0:
                throw new IllegalArgumentException("No color found");
            case 1:
                // just use the color for both parts
                return new Pair<>(foundColors.get(0), foundColors.get(0));
            case 2:
                return new Pair<>(foundColors.get(0), foundColors.get(1));
            case 3:
                // handles light blue and dark green matching again for and blue and green for now
                return new Pair<>(foundColors.get(0), foundColors.get(foundColors.size()-1));
            case 4:
                // handles light blue and dark green in the same query
                return new Pair<>(foundColors.get(0), foundColors.get(foundColors.size()-2));
            default:
                return new Pair<>(foundColors.get(0), foundColors.get(1));
        }
    }

    static Pair<String, String> getRandomColors() {
        Random rand = new Random();

        int topRandom = rand.nextInt(intlNameKeyMap.size());
        int botRandom = rand.nextInt(intlNameKeyMap.size());

        return new Pair<>(colorKeys[topRandom], colorKeys[botRandom]);
    }

}
