package de.bastianrinsche.panicsign;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.bastianrinsche.panicsign.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_key = getString(R.string.key_pref_auto_send);
        boolean autoSend = preferences.getBoolean(pref_key, false);

        binding.aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        binding.prefVoiceSwitch.setChecked(autoSend);
        binding.dismiss.setOnClickListener(v -> onBackPressed());
        binding.prefVoiceSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> preferences.edit().putBoolean(pref_key, isChecked).apply()
        );
    }
}
