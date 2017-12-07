package com.alpaca.alarmpaca.util;

import android.annotation.SuppressLint;
import android.content.Context;

public class Contextor {

    @SuppressLint("StaticFieldLeak")
    private static Contextor instance;

    public static Contextor getInstance() {
        if (instance == null)
            instance = new Contextor();
        return instance;
    }

    public static Context getContextInstance() {
        return getInstance().getContext();
    }

    private Context mContext;

    private Contextor() {

    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

}
