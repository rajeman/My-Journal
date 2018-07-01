package com.rajeman.myjournal;

import com.google.firebase.database.FirebaseDatabase;
// this class enables firebase persistence.
public class MyFireBaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //enable offline capability
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
