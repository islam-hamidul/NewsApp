package com.example.newsappdemo;

import android.app.Application;

import com.example.newsappdemo.broadcast.MyReceiver;
public class AppController extends Application {
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(MyReceiver.ConnectivityReceiverListener listener) {
        MyReceiver.connectivityReceiverListener = listener;
    }
}