package com.eoss.application.catchya;

import android.app.Application;

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
}
