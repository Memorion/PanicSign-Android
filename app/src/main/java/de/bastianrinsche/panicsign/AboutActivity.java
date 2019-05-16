package de.bastianrinsche.panicsign;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.dismiss) ImageButton dismissButton;
    @BindView(R.id.pref_voice_switch) SwitchCompat voiceSwitch;
    @BindView(R.id.about_text) TextView aboutText;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String pref_key = getString(R.string.key_pref_auto_send);
        boolean autoSend = preferences.getBoolean(pref_key, false);

        voiceSwitch.setChecked(autoSend);
        voiceSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> preferences.edit().putBoolean(pref_key, isChecked).apply()
        );
    }

    @OnClick(R.id.dismiss)
    void dismiss() {
        onBackPressed();
    }
}
