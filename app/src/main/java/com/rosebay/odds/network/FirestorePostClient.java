package com.rosebay.odds.network;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestorePostClient {

    private FirebaseFirestore mFirestore;

    public FirestorePostClient(FirebaseFirestore firebaseFirestore) {
        mFirestore = firebaseFirestore;
    }
}
