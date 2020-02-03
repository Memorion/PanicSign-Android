package de.bastianrinsche.panicsign;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pair;

import com.google.android.gms.actions.SearchIntents;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.seismic.ShakeDetector;

import de.bastianrinsche.panicsign.databinding.ActivitySignBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignActivity extends AppCompatActivity {
    static {
        // https://medium.com/@chrisbanes/appcompat-v23-2-age-of-the-vectors-91cbafa87c88
        // needed for LayerDrawables with <vector> inside < on api 21
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private ActivitySignBinding binding;
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
        binding = ActivitySignBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        topControl = new ColorControl(binding.controlTop, getString(R.string.key_light_blue));
        bottomControl = new ColorControl(binding.controlBottom, getString(R.string.key_blue));

        LayerDrawable sign = (LayerDrawable)binding.viewSign.getDrawable();
        topSign = sign.findDrawableByLayerId(R.id.sign_top);
        bottomSign = sign.findDrawableByLayerId(R.id.sign_bottom);

        binding.viewSign.setImageDrawable(sign);
        // disable all caps button for api < 14
        binding.buttonChange.setTransformationMethod(null);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(() -> {
            Pair<String, String> colors = colorUtils.getRandomColors();
            topControl.setSelected(colors.first);
            bottomControl.setSelected(colors.second);

            if (autoSendEnabled()) {
                sendChangeRequest();
            }
        });

        colorUtils = PanicSign.getColorUtils();

        changeSignColor(top, topControl.getSelected());
        changeSignColor(bottom, bottomControl.getSelected());

        topControl.setOnColorSelectedListener(color -> changeSignColor(top, color));
        bottomControl.setOnColorSelectedListener(color -> changeSignColor(bottom, color));
        binding.buttonOverflow.setOnClickListener(v -> openAbout());
        binding.buttonChange.setOnClickListener(v -> sendChangeRequest());

        if (hasVoiceExtra(getIntent())) {
            handleVoiceInteraction(getIntent());
        } else if (savedInstanceState != null) {
            topControl.setSelected(savedInstanceState.getString(getString(R.string.key_top)));
            bottomControl.setSelected(savedInstanceState.getString(getString(R.string.key_bottom)));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        shakeDetector.start(sensorManager);
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        setIntent(newIntent);
        if (hasVoiceExtra(newIntent)) {
            handleVoiceInteraction(newIntent);
        }
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

    private boolean hasVoiceExtra(Intent intent) {
        return SearchIntents.ACTION_SEARCH.equals(intent.getAction())
                && intent.hasExtra(SearchManager.QUERY);
    }

    private void handleVoiceInteraction(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        try {
            Pair<String, String> colors = colorUtils.colorsFromQuery(colorUtils.intlNameKeyMap, query);
            topControl.setSelected(colors.first);
            bottomControl.setSelected(colors.second);
            Timber.d("VOICE Pair<" + colors.first + ", " + colors.second + ">");
        } catch (IllegalArgumentException e) {
            //TODO maybe random
        }

        if (autoSendEnabled()) {
            sendChangeRequest();
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

    void openAbout() {
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

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
                .getBoolean(getString(R.string.key_pref_auto_send), false);
    }

    private void showErrorSnackbar(int messageId) {
        Snackbar snackbar = Snackbar.make(binding.controlBottom.getRoot(), messageId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
