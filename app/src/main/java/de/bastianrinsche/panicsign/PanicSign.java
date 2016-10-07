package de.bastianrinsche.panicsign;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PanicSign extends Application {
    private static Context context;
    private static SignService signService;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

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

        signService = retrofit.create(SignService.class);
    }

    static Context getAppContext() {
        return context;
    }

    static SignService getSignService() {
        return signService;
    }
}
