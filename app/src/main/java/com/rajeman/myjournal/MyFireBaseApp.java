package com.rajeman.myjournal;

import com.google.firebase.database.FirebaseDatabase;
// this class e the fire
public class MyFireBaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //enable offline capability
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
