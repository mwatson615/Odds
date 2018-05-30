package com.rosebay.odds.dagger.module;


import com.rosebay.odds.dagger.component.OddsApplicationScope;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {

    @Provides
    @OddsApplicationScope
    OkHttpClient providesHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build();
    }

    @Provides
    @OddsApplicationScope
    HttpLoggingInterceptor providesInterceptor() {
        return new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
