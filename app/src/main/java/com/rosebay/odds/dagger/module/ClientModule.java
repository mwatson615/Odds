package com.rosebay.odds.dagger.module;

import android.content.SharedPreferences;

import com.rosebay.odds.dagger.component.OddsApplicationScope;
import com.rosebay.odds.network.CloudFunctionsApi;
import com.rosebay.odds.network.CloudFunctionsClient;
import com.rosebay.odds.network.FirebaseApi;
import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.network.ImageApi;
import com.rosebay.odds.network.ImageClient;
import com.rosebay.odds.util.SharedPreferencesClient;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {ServiceModule.class, SharedPreferencesModule.class})
public class ClientModule {

    @Provides
    @OddsApplicationScope
    ImageClient providesImageClient(@Named("image") Retrofit retrofit) {
        return new ImageClient(retrofit.create(ImageApi.class));
    }

    @Provides
    @OddsApplicationScope
    FirebaseClient providesFirebaseClient(@Named("firebase") Retrofit retrofit) {
        return new FirebaseClient(retrofit.create(FirebaseApi.class));
    }

    @Provides
    @OddsApplicationScope
    CloudFunctionsClient providesCloudFunctionsClient(@Named("cloudFunctions") Retrofit retrofit) {
        return new CloudFunctionsClient(retrofit.create(CloudFunctionsApi.class));
    }

    @Provides
    @OddsApplicationScope
    SharedPreferencesClient providesUsernamePrefClient(SharedPreferences sharedPreferences) {
        return new SharedPreferencesClient(sharedPreferences);
    }

}
