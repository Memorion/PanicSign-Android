package de.bastianrinsche.panicsign

import android.app.Application

class PanicSign : Application() {
    override fun onCreate() {
        super.onCreate()
        colorUtils = ColorUtils(applicationContext)
    }

    companion object {
        lateinit var colorUtils: ColorUtils
        val signService by lazy {
            SignService.create()
        }
    }
}
