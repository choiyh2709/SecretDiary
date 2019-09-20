package com.photocard.secretdiary.custom

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema

class Migration: RealmMigration {
    /**
     * This method will be called if a migration is needed. The entire method is wrapped in a
     * write transaction so it is possible to create, update or delete any existing objects
     * without wrapping it in your own transaction.
     *
     * @param realm the Realm schema on which to perform the migration.
     * @param oldVersion the schema version of the Realm at the start of the migration.
     * @param newVersion the schema version of the Realm after executing the migration.
     */
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema: RealmSchema = realm.schema


        if (oldVersion == 1L){
            val userSchema = schema.get("UserInfo")

            userSchema?.addField("isLock", Boolean::class.java)?.transform { obj -> obj.set("isLock", false) }
            userSchema?.addField("password", String::class.java)?.setRequired("password", true)?.transform { obj -> obj.set("password", "") }

            val writeSchema = schema.get("WriteInfo")
            writeSchema?.addField("insDate", String::class.java)?.setRequired("insDate", true)?.transform { obj -> obj.set("password", "") }
        }
    }
}