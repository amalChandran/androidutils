package com.dump.amalchandran.androidutil;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class App extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
