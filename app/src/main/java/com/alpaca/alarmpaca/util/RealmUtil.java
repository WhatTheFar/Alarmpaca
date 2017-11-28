package com.alpaca.alarmpaca.util;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtil {

//    private static RealmUtil instance;

//    public static RealmUtil getInstance() {
//        if (instance == null)
//            instance = new RealmUtil();
//        return instance;
//    }

    public static Realm getRealmInstance() {
        return Realm.getInstance(getRealmConfig());
    }

    private static RealmConfiguration getRealmConfig() {
        return (new RealmConfiguration.Builder())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

}
