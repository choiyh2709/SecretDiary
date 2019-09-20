package com.photocard.secretdiary.custom

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Fabric.with(this, Crashlytics())

        val config = RealmConfiguration.Builder()
            //.deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .migration(Migration())
            .build()

        Realm.setDefaultConfiguration(config)
    }
}