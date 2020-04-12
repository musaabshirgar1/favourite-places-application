package com.example.favouriteplacesapplication;

import android.app.Application;

import com.firebase.client.Firebase;

public class FirebaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
