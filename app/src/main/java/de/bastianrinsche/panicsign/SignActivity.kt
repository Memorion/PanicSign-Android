package de.bastianrinsche.panicsign

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.actions.SearchIntents
import com.google.android.material.snackbar.Snackbar
import com.squareup.seismic.ShakeDetector
import de.bastianrinsche.panicsign.ColorControl.OnColorSelectedListener
import de.bastianrinsche.panicsign.ColorUtils.Companion.resolveColor
import de.bastianrinsche.panicsign.PanicSign.Companion.colorUtils
import de.bastianrinsche.panicsign.PanicSign.Companion.signService
import de.bastianrinsche.panicsign.databinding.ActivitySignBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SignActivity : AppCompatActivity() {
    companion object {
        init { // https://medium.com/@chrisbanes/appcompat-v23-2-age-of-the-vectors-91cbafa87c88
               // needed for LayerDrawables with <vector> inside < on api 21
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    private lateinit var binding: ActivitySignBinding
    private lateinit var topControl: ColorControl
    private lateinit var bottomControl: ColorControl
    private lateinit var topSign: Drawable
    private lateinit var bottomSign: Drawable
    private val top = true
    private val bottom = false
    private var sensorManager: SensorManager? = null
    private var shakeDetector: ShakeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        topControl = ColorControl(binding.controlTop, getString(R.string.key_light_blue))
        bottomControl = ColorControl(binding.controlBottom, getString(R.string.key_blue))

        val sign = binding.viewSign.drawable as LayerDrawable
        topSign = sign.findDrawableByLayerId(R.id.sign_top)
        bottomSign = sign.findDrawableByLayerId(R.id.sign_bottom)
        binding.viewSign.setImageDrawable(sign)

        // disable all caps button for api < 14
        binding.buttonChange.transformationMethod = null
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(ShakeDetector.Listener {
            val colors = colorUtils.randomColors
            topControl.selected = colors.first
            bottomControl.selected = colors.second
            if (autoSendEnabled()) {
                sendChangeRequest()
            }
        })

        changeSignColor(top, topControl.selected)
        changeSignColor(bottom, bottomControl.selected)
        val colorListener = object : OnColorSelectedListener {
            override fun onColorSelected(colorString: String) {
                changeSignColor(top, colorString)
            }
        }
        topControl.setOnColorSelectedListener(colorListener)
        bottomControl.setOnColorSelectedListener(colorListener)
        binding.buttonOverflow.setOnClickListener { openAbout() }
        binding.buttonChange.setOnClickListener { sendChangeRequest() }

        if (hasVoiceExtra(intent)) {
            handleVoiceInteraction(intent)
        } else if (savedInstanceState != null) {
            topControl.selected = savedInstanceState.getString(getString(R.string.key_top))!!
            bottomControl.selected = savedInstanceState.getString(getString(R.string.key_bottom))!!
        }
    }

    override fun onStart() {
        super.onStart()
        shakeDetector?.start(sensorManager)
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        intent = newIntent
        if (hasVoiceExtra(newIntent)) {
            handleVoiceInteraction(newIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        shakeDetector?.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(getString(R.string.key_top), topControl.selected)
        outState.putString(getString(R.string.key_bottom), bottomControl.selected)
        super.onSaveInstanceState(outState)
    }

    private fun hasVoiceExtra(intent: Intent): Boolean {
        return SearchIntents.ACTION_SEARCH == intent.action && intent.hasExtra(SearchManager.QUERY)
    }

    private fun handleVoiceInteraction(intent: Intent) {
        val query = intent.getStringExtra(SearchManager.QUERY)
        try {
            val colors = colorUtils.colorsFromQuery(colorUtils.intlNameKeyMap, query)
            topControl.selected = colors.first
            bottomControl.selected = colors.second
            Timber.d("VOICE Pair<%s, %s>", colors.first, colors.second)
        } catch (e: IllegalArgumentException) {
            //TODO maybe random
        }
        if (autoSendEnabled()) {
            sendChangeRequest()
        }
    }

    private fun changeSignColor(top: Boolean, colorString: String) {
        val color = resolveColor(this, colorUtils.colorMap, colorString)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (top) {
                topSign.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            } else {
                bottomSign.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        } else {
            if (top) {
                topSign.setTint(color)
            } else {
                bottomSign.setTint(color)
            }
        }
    }

    private fun openAbout() {
        val about = Intent(this, AboutActivity::class.java)
        startActivity(about)
    }

    private fun sendChangeRequest() {
        val topRGB = colorUtils.colorToRGBString(topControl.selected)
        val bottomRGB = colorUtils.colorToRGBString(bottomControl.selected)
        val request = signService.setSignColors(topRGB, bottomRGB)
        request.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    if (response.code() == 429) {
                        showErrorSnackbar(R.string.error_rate_limited)
                    } else {
                        showErrorSnackbar(R.string.error_generic)
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorSnackbar(R.string.error_generic)
            }
        })
    }

    private fun autoSendEnabled(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.key_pref_auto_send), false)
    }

    private fun showErrorSnackbar(messageId: Int) {
        Snackbar.make(binding.controlBottom.root, messageId, Snackbar.LENGTH_LONG).show()
    }
}
