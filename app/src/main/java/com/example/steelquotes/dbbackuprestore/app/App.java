package com.example.steelquotes.dbbackuprestore.app;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AndroidThreeTen.init(this);
    }
}
