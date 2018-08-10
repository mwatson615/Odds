package com.rosebay.odds.dagger.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rosebay.odds.BuildConfig;
import com.rosebay.odds.dagger.component.OddsApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = NetworkModule.class)
public class ServiceModule {

    @Provides
    @OddsApplicationScope
    Gson providesGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }

    @Provides
    @OddsApplicationScope
    @Named("image")
    Retrofit providesRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BaseImageUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @OddsApplicationScope
    @Named("firebase")
    Retrofit providesFirebaseRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BaseFirebaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @OddsApplicationScope
    @Named("cloudFunctions")
    Retrofit providesCloudFunctionsRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BaseGCFUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    @Provides
    @OddsApplicationScope
    DatabaseReference providesFirebaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Provides
    @OddsApplicationScope
    FirebaseFirestore providesUserFirestore() {
        return FirebaseFirestore.getInstance();
    }

}
