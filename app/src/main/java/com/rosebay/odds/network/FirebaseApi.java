package com.rosebay.odds.network;

import com.rosebay.odds.model.SingleOdd;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FirebaseApi {

    @GET("posts/{postId}.json")
    Single<SingleOdd> getSingleOdd(@Path("postId") String postId);

    @GET("users/{username}.json")
    Maybe<String> getUsername(@Path("username") String username);

    @POST("users.json")
    Completable addUser(@Body String username);

}
