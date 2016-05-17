package com.panic.sign;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.panic.sign.PanicSign.getAppContext;

public class ColorUtils {

    private static String[] intlColors = getAppContext().getResources()
            .getStringArray(R.array.colors_spoken);
    static String[] colorKeys = getAppContext().getResources()
            .getStringArray(R.array.colors_keys);

    public static HashMap<String, String> intlNameKeyMap;
    static {
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
        Log.e("ColorUtils", "Searched colordIDMap for invalid String: " + color);
        return ContextCompat.getColor(context, R.color.purple);
    }

    public static List<String> colorsFromQuery(HashMap<String, String> spokenToKey, String query) {
        Log.d("QUERY", query);
        List<String> foundColors = new ArrayList<>();
        for (String s : spokenToKey.keySet()) {
            if (query.contains(s)) {
                foundColors.add(spokenToKey.get(s));
            }
        }
        return foundColors;
    }
}
