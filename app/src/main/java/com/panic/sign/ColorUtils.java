package com.panic.sign;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class ColorUtils {
    public static int resolveColor(Context context, String color) {
        int id;
        switch (color) {
            case "RED":
                id = R.color.red;
                break;
            case "ORANGE":
                id = R.color.orange;
                break;
            case "YELLOW":
                id = R.color.yellow;
                break;
            case "GREEN":
                id = R.color.green;
                break;
            case "GREEN2":
                id = R.color.green2;
                break;
            case "TEAL":
                id = R.color.teal;
                break;
            case "LIGHTBLUE":
                id = R.color.lightblue;
                break;
            case "BLUE":
                id = R.color.blue;
                break;
            case "PURPLE":
                id = R.color.purple;
                break;
            case "PINK":
                id = R.color.pink;
                break;
            default:
                id = R.color.purple;
                break;
        }
        return ContextCompat.getColor(context, id);
    }
}
