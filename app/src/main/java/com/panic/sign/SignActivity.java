package com.panic.sign;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SignActivity extends AppCompatActivity {
    static {
        // https://medium.com/@chrisbanes/appcompat-v23-2-age-of-the-vectors-91cbafa87c88
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @BindView(R.id.button_change) Button change;
    @BindView(R.id.view_sign) ImageView signView;
    @BindView(R.id.control_top) View topColors;
    @BindView(R.id.control_bottom) View bottomColors;
    // this is necessary to disambiguate the layout IDs
    // may be fixed in the future https://github.com/JakeWharton/butterknife/issues/394
    static class ColorControl {
        @BindViews({R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.green2,
                R.id.teal, R.id.light_blue, R.id.blue, R.id.purple, R.id.pink})
        List<ImageView> colors;

        public String getSelected() {
            for (View v : colors) {
                if (v.isSelected()) {
                    return v.getTag().toString();
                }
            }
            throw new IllegalStateException("Some color has to be selected at all times");
        }

        public void setSelected(String selection) {
            if (selection == null) {
                return;
            }
            for (View v : colors) {
                if (v.getTag().toString().equals(selection)) {
                    v.setSelected(true);
                } else {
                    v.setSelected(false);
                }
            }
        }
    }

    ColorControl topControl = new ColorControl();
    ColorControl bottomControl = new ColorControl();

    boolean top = true;
    boolean bottom = false;
    String topSelection = "LIGHTBLUE";
    String bottomSelection = "BLUE";

    LayerDrawable sign;
    Drawable topSign;
    Drawable bottomSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        ButterKnife.bind(this);
        ButterKnife.bind(topControl, topColors);
        ButterKnife.bind(bottomControl, bottomColors);

        sign = (LayerDrawable)signView.getDrawable();
        topSign = sign.findDrawableByLayerId(R.id.sign_top);
        bottomSign = sign.findDrawableByLayerId(R.id.sign_bottom);

        signView.setImageDrawable(sign);

        if (savedInstanceState != null) {
            topSelection = savedInstanceState.getString("TOP");
            bottomSelection = savedInstanceState.getString("BOTTOM");
        }
        topControl.setSelected(topSelection);
        changeSignColor(top, topSelection);
        bottomControl.setSelected(bottomSelection);
        changeSignColor(bottom, bottomSelection);

        View.OnClickListener topListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = view.getTag().toString();
                if (!topSelection.equals(current)) {
                    topSelection = current;
                    topControl.setSelected(topSelection);
                    changeSignColor(top, topSelection);
                }
                Log.d("topSelection", topSelection);
            }
        };

        View.OnClickListener bottomListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = view.getTag().toString();
                if (!bottomSelection.equals(current)) {
                    bottomSelection = current;
                    bottomControl.setSelected(bottomSelection);
                    changeSignColor(bottom, bottomSelection);
                }
                Log.d("bottomSelection", bottomSelection);
            }
        };

        for (ImageView i : topControl.colors) {
            i.setOnClickListener(topListener);
        }

        for (ImageView i : bottomControl.colors) {
            i.setOnClickListener(bottomListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("TOP", topControl.getSelected());
        outState.putString("BOTTOM", bottomControl.getSelected());
        super.onSaveInstanceState(outState);
    }

    private void changeSignColor(boolean top, String colorString) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (top) {
                topSign.setColorFilter(resolveColor(colorString), PorterDuff.Mode.MULTIPLY);
            } else {
                bottomSign.setColorFilter(resolveColor(colorString), PorterDuff.Mode.MULTIPLY);
            }
        } else {
            if (top) {
                topSign.setTint(resolveColor(colorString));
            } else {
                bottomSign.setTint(resolveColor(colorString));
            }
        }
    }

    private int resolveColor(String color) {
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
        return ContextCompat.getColor(this, id);
    }

}
