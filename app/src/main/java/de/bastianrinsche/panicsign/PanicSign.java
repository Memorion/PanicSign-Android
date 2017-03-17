package de.bastianrinsche.panicsign;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import timber.log.Timber;

public class PanicSign extends Application {
    private static ColorUtils colorUtils;
    private static SignService signService;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient client;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();
        } else {
            client = new OkHttpClient.Builder()
                            .build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://signserver.panic.com/")
                .build();

        colorUtils = new ColorUtils(getApplicationContext());

        signService = retrofit.create(SignService.class);
    }

    static ColorUtils getColorUtils() {
        return colorUtils;
    }

    static SignService getSignService() {
        return signService;
    }
}
