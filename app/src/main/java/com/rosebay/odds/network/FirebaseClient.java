package com.rosebay.odds.network;

import com.rosebay.odds.model.SingleOdd;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class FirebaseClient {

    private FirebaseApi mFirebaseApi;

    public FirebaseClient(FirebaseApi firebaseApi) {
        mFirebaseApi = firebaseApi;
    }

    public Single<SingleOdd> fetchSingleOdd(String postId) {
        return mFirebaseApi.getSingleOdd(postId);
    }

    public Maybe<String> getUsername(String username) {
        return mFirebaseApi.getUsername(username);
    }

    public Completable addUser(String username) {
        return mFirebaseApi.addUser(username);
    }

}
