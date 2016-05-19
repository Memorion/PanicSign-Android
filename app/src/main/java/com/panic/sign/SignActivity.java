package com.panic.sign;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.actions.SearchIntents;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignActivity extends AppCompatActivity {
    static {
        // https://medium.com/@chrisbanes/appcompat-v23-2-age-of-the-vectors-91cbafa87c88
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @BindView(R.id.button_change) Button change;
    @BindView(R.id.view_sign) ImageView signView;
    @BindView(R.id.control_top) View topColorView;
    @BindView(R.id.control_bottom) View bottomColorView;

    private ColorControl topControl;
    private ColorControl bottomControl;

    private final boolean top = true;
    private final boolean bottom = false;

    private Drawable topSign;
    private Drawable bottomSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        String topSelection;
        String bottomSelection;

        if (savedInstanceState != null) {
            topSelection = savedInstanceState.getString(getString(R.string.key_top));
            bottomSelection = savedInstanceState.getString(getString(R.string.key_bottom));
        } else {
            topSelection = getString(R.string.key_light_blue);
            bottomSelection = getString(R.string.key_blue);
        }

        ButterKnife.bind(this);
        topControl = new ColorControl(topColorView, topSelection);
        bottomControl = new ColorControl(bottomColorView, bottomSelection);

        LayerDrawable sign = (LayerDrawable)signView.getDrawable();
        topSign = sign.findDrawableByLayerId(R.id.sign_top);
        bottomSign = sign.findDrawableByLayerId(R.id.sign_bottom);

        signView.setImageDrawable(sign);

        if (hasVoiceExtra()) {
            handleVoiceInteraction();
        }

        changeSignColor(top, topControl.getSelected());
        changeSignColor(bottom, bottomControl.getSelected());

        topControl.setOnColorSelectedListener(new ColorControl.OnColorSelectedListener() {
            @Override
            public void onColorSelected(String color) {
                changeSignColor(top, color);
            }
        });
        bottomControl.setOnColorSelectedListener(new ColorControl.OnColorSelectedListener() {
            @Override
            public void onColorSelected(String color) {
                changeSignColor(bottom, color);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.key_top), topControl.getSelected());
        outState.putString(getString(R.string.key_bottom), bottomControl.getSelected());
        super.onSaveInstanceState(outState);
    }

    private boolean hasVoiceExtra() {
        Intent intent = getIntent();
        return SearchIntents.ACTION_SEARCH.equals(intent.getAction())
                && intent.hasExtra(SearchManager.QUERY);
    }

    private void handleVoiceInteraction() {
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        try {
            Pair<String, String> colors = ColorUtils.colorsFromQuery(ColorUtils.intlNameKeyMap, query);
            topControl.setSelected(colors.first);
            bottomControl.setSelected(colors.second);
            Log.d("VOICE", "Pair<" + colors.first + ", " + colors.second + ">");
        } catch (IllegalArgumentException e) {
            //TODO maybe random
        }
    }

    private void changeSignColor(boolean top, String colorString) {
        int color = ColorUtils.resolveColor(this, ColorUtils.colorMap, colorString);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (top) {
                topSign.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            } else {
                bottomSign.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        } else {
            if (top) {
                topSign.setTint(color);
            } else {
                bottomSign.setTint(color);
            }
        }
    }

}
