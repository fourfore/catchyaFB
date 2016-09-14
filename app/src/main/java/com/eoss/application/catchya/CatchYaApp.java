package com.eoss.application.catchya;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

/**
 * Created by Foremost on 12/9/2559.
 */
public class CatchYaApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
