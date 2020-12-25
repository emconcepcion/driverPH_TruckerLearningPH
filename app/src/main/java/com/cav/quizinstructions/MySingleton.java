package com.cav.quizinstructions;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    public MySingleton(Context context) {
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    //return an object of request queue
    private RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    //return an instance of this class
    public static synchronized MySingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    //add to request queue
    public<T> void addToRequestQueue (Request<T> request){
        getRequestQueue().add(request);
    }

}