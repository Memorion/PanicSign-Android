package de.bastianrinsche.panicsign

import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import timber.log.Timber.DebugTree

class PanicSign : Application() {
    override fun onCreate() {
        super.onCreate()
        colorUtils = ColorUtils(applicationContext)
    }

    companion object {
        @JvmStatic
        var colorUtils: ColorUtils? = null
            private set

        private val client = if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
        } else {
            OkHttpClient.Builder().build()
        }

        private val retrofit: Retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://signserver.panic.com/")
                .build()

        @JvmStatic
        var signService: SignService = retrofit.create(SignService::class.java)
    }
}
