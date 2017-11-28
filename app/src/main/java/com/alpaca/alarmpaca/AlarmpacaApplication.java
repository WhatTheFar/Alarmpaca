package com.alpaca.alarmpaca;

import android.app.Application;

import com.alpaca.alarmpaca.util.Contextor;

import io.realm.Realm;

public class AlarmpacaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
        Realm.init(getApplicationContext());
    }
}
