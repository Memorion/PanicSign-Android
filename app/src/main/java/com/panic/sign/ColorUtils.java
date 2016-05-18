package com.panic.sign;

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

import static com.panic.sign.PanicSign.getAppContext;

public class ColorUtils {

    static String[] colorKeys = getAppContext().getResources()
            .getStringArray(R.array.colors_keys);

    public static HashMap<String, String> intlNameKeyMap;
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

    public static HashMap<String, Integer> colorMap;
    static {
        TypedArray typedArray = getAppContext().getResources().obtainTypedArray(R.array.colors_ids);
        colorMap = new HashMap<>(typedArray.length());
        for (int i = 0; i < colorKeys.length; i++) {
            colorMap.put(colorKeys[i], typedArray.getResourceId(i, R.color.purple));
        }
        typedArray.recycle();
    }

    public static int resolveColor(Context context, HashMap<String, Integer> colorIDMap, String color) {
        Integer id = colorIDMap.get(color);
        if (id != null) { return ContextCompat.getColor(context, id); }
        Log.e("ColorUtils", "Searched colorMap for invalid String: " + color);
        return ContextCompat.getColor(context, R.color.purple);
    }

    public static Pair<String, String> colorsFromQuery(HashMap<String, String> intlNameKeyMap, String query) {
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

}
