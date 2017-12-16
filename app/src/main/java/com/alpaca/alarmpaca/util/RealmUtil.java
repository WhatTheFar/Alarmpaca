package com.alpaca.alarmpaca.util;

import com.alpaca.alarmpaca.model.Alarm;

import java.io.Closeable;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtil {

    public static Realm getRealmInstance() {
        return Realm.getInstance(getRealmConfig());
    }

    private static RealmConfiguration getRealmConfig() {
        return (new RealmConfiguration.Builder())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

}
