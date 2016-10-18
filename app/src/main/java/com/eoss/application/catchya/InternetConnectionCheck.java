package com.eoss.application.catchya;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by noom on 18/10/2559.
 */

public class InternetConnectionCheck extends Application{

    //Check internet connection
    //if want to use also add this permission to manifest
    //<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    private boolean isNetworkConnected(Context c) {

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                //Toast.makeText(c, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                //Toast.makeText(c, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
