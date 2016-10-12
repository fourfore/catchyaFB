package com.eoss.application.catchya;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Foremost on 4/10/2559.
 */

public class SendNotify extends Application{
    public void sendNotify(String message, String title, String token,String uid,String fUid, Context context,String from){

        final String mMessage = message;
        final String mTitle = title;
        final String mToken = token;
        final String mUid = uid;
        final String mFuid = fUid;
        final Context mContext = context;
        final String mFrom = from;

        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://catchya.96.lt/send_notification.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("pst response",response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("message",mMessage);
                params.put("title",mTitle);
                params.put("token",mToken);
                params.put("uid",mUid);
                params.put("fUid",mFuid);
                params.put("sendFrom",mFrom);
                return params;
            }

        };
        int socketTimeout = 10000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }
}
