package br.edu.ifspsaocarlos.sdm.cuidador.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);
        RealmConfiguration config =
                new RealmConfiguration.Builder()
                        .schemaVersion(5)
                        .name("myrealm.realm")
                        .build();
        Realm.setDefaultConfiguration(config);
    }
}