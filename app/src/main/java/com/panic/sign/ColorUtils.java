package com.panic.sign;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class ColorUtils {
    // check for a way to reference the xml ids directly
    private final static String RED = "RED";
    private final static String ORANGE = "ORANGE";
    private final static String YELLOW = "YELLOW";
    private final static String GREEN = "GREEN";
    private final static String GREEN2 = "GREEN2";
    private final static String TEAL = "TEAL";
    private final static String LIGHTBLUE = "LIGHTBLUE";
    private final static String BLUE = "BLUE";
    private final static String PURPLE = "PURPLE";
    private final static String PINK = "PINK";

    public static int resolveColor(Context context, String color) {
        int id;
        switch (color) {
            case RED:
                id = R.color.red;
                break;
            case ORANGE:
                id = R.color.orange;
                break;
            case YELLOW:
                id = R.color.yellow;
                break;
            case GREEN:
                id = R.color.green;
                break;
            case GREEN2:
                id = R.color.green2;
                break;
            case TEAL:
                id = R.color.teal;
                break;
            case LIGHTBLUE:
                id = R.color.lightblue;
                break;
            case BLUE:
                id = R.color.blue;
                break;
            case PURPLE:
                id = R.color.purple;
                break;
            case PINK:
                id = R.color.pink;
                break;
            default:
                id = R.color.purple;
                break;
        }
        return ContextCompat.getColor(context, id);
    }
}
