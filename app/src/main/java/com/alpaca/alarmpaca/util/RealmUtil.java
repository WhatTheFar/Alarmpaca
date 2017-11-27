package com.alpaca.alarmpaca.util;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Far on 11/26/2017 AD.
 */

public class RealmUtil {

    private static RealmUtil instance;

    public static RealmUtil getInstance() {
        if (instance == null)
            instance = new RealmUtil();
        return instance;
    }

    public static Realm getRealmInstance() {
        return Realm.getInstance(getRealmConfig());
    }

    public static RealmConfiguration getRealmConfig() {
        return (new RealmConfiguration.Builder())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

}
