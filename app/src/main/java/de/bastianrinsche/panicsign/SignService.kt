package de.bastianrinsche.panicsign

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import timber.log.Timber

interface SignService {
    @Headers("Origin: https://sign.panic.com", "User-Agent: Panic Sign Android")
    @GET("set/{topColor}/{bottomColor}")
    suspend fun setSignColors(
            @Path(value = "topColor", encoded = true) topColor: String,
            @Path(value = "bottomColor", encoded = true) bottomColor: String
    ): Response<Unit>

    companion object {
        fun create(): SignService {
            val client = if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
                OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build()
            } else {
                OkHttpClient.Builder().build()
            }

            val retrofit: Retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://signserver.panic.com/")
                    .build()

            return retrofit.create(SignService::class.java)
        }
    }
}
