package de.bastianrinsche.panicsign;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

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

        final String pref_key = getString(R.string.key_pref_send_after_voice);
        boolean sendAfterVoice = preferences.getBoolean(pref_key, true);

        voiceSwitch.setChecked(sendAfterVoice);
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(pref_key, isChecked).apply();
            }
        });
    }

    @OnClick(R.id.dismiss)
    void dismiss() {
        onBackPressed();
    }
}
