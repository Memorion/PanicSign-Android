package de.bastianrinsche.panicsign;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PanicSign extends Application {
    private static ColorUtils colorUtils;
    private static SignService signService;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://signserver.panic.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
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
