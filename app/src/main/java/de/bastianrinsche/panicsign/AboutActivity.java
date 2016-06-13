package de.bastianrinsche.panicsign;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pref_voice_switch) SwitchCompat voiceSwitch;
    @BindView(R.id.about_text) TextView aboutText;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
