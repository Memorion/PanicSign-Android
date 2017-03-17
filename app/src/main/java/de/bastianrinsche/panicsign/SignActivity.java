package de.bastianrinsche.panicsign;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.actions.SearchIntents;
import com.squareup.seismic.ShakeDetector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignActivity extends AppCompatActivity implements ShakeDetector.Listener {
    static {
        // https://medium.com/@chrisbanes/appcompat-v23-2-age-of-the-vectors-91cbafa87c88
        // needed for LayerDrawables with <vector> inside < on api 21
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

    private ColorUtils colorUtils;
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;

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
        // disable all caps button for api < 14
        change.setTransformationMethod(null);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        colorUtils = PanicSign.getColorUtils();

        changeSignColor(top, topControl.getSelected());
        changeSignColor(bottom, bottomControl.getSelected());

        topControl.setOnColorSelectedListener(color -> changeSignColor(top, color));
        bottomControl.setOnColorSelectedListener(color -> changeSignColor(bottom, color));

        if (hasVoiceExtra()) {
            handleVoiceInteraction();
            if (autoSendEnabled()) {
                sendChangeRequest();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        shakeDetector.start(sensorManager);
    }

    @Override
    protected void onStop() {
        super.onStop();
        shakeDetector.stop();
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
            Pair<String, String> colors = colorUtils.colorsFromQuery(colorUtils.intlNameKeyMap, query);
            topControl.setSelected(colors.first);
            bottomControl.setSelected(colors.second);
            Timber.d("VOICE", "Pair<" + colors.first + ", " + colors.second + ">");
        } catch (IllegalArgumentException e) {
            //TODO maybe random
        }
    }

    private void changeSignColor(boolean top, String colorString) {
        int color = ColorUtils.resolveColor(this, colorUtils.colorMap, colorString);
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

    @Override
    public void hearShake() {
        Pair<String, String> colors = colorUtils.getRandomColors();
        topControl.setSelected(colors.first);
        bottomControl.setSelected(colors.second);

        if (autoSendEnabled()) {
            sendChangeRequest();
        }
    }

    @OnClick(R.id.button_overflow)
    void openAbout() {
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

    @OnClick(R.id.button_change)
    void sendChangeRequest() {
        String topRGB = colorUtils.colorToRGBString(topControl.getSelected());
        String bottomRGB = colorUtils.colorToRGBString(bottomControl.getSelected());

        Call<Void> request = PanicSign.getSignService().setSignColors(topRGB, bottomRGB);
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 429) {
                        showErrorSnackbar(R.string.error_rate_limited);
                    } else {
                        showErrorSnackbar(R.string.error_generic);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showErrorSnackbar(R.string.error_generic);
            }
        });
    }

    private boolean autoSendEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.key_pref_auto_send), true);
    }

    private void showErrorSnackbar(int messageId) {
        Snackbar snackbar = Snackbar.make(bottomColorView, messageId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
