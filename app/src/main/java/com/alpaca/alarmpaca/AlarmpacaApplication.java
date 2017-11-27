package com.alpaca.alarmpaca;

import android.app.Application;

import com.alpaca.alarmpaca.util.Contextor;

import io.realm.Realm;

/**
 * Created by Far on 11/26/2017 AD.
 */

public class AlarmpacaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
        Realm.init(getApplicationContext());
    }
}
