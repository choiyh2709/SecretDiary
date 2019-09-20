package com.photocard.secretdiary.custom

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import io.realm.Realm
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class RealmBackupRestore(private val activity: Activity) {
    private val EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    private val EXPORT_REALM_FILE_NAME = "diary.realm"
    private val IMPORT_REALM_FILE_NAME = "default.realm"
    private var realm: Realm = Realm.getDefaultInstance()

    fun backup() {
        // First check if we have storage permissions
        checkStoragePermissions(activity)
        val exportRealmFile: File

        try {
            EXPORT_REALM_PATH.mkdirs()

            // create a backup file
            exportRealmFile = File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME)

            // if backup file already exists, delete it
            exportRealmFile.delete()

            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(activity.applicationContext, "데이터 백업이 완료되었습니다.", Toast.LENGTH_SHORT).show()

        realm.close()
    }

    fun restore() {
        checkStoragePermissions(activity)

        //Restore
        val restoreFilePath = "$EXPORT_REALM_PATH/$EXPORT_REALM_FILE_NAME"
        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME)

        Handler().postDelayed({ System.exit(0) },300)
    }

    private fun copyBundledRealmFile(oldFilePath: String, outFileName: String): String? {
        try {
            val file = File(activity.applicationContext.filesDir, outFileName)

            val outputStream = FileOutputStream(file)

            val inputStream = FileInputStream(File(oldFilePath))

            val buf = ByteArray(1024)
            var bytesRead: Int

            do {
                bytesRead = inputStream.read(buf)
                if (bytesRead != -1) outputStream.write(buf, 0, bytesRead)
            }while (bytesRead > 0)

            Toast.makeText(activity.applicationContext, "데이터 복원이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun checkStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun dbPath(): String {
        return realm.path
    }

    companion object {

        private val TAG = RealmBackupRestore::class.java.name

        // Storage Permissions
        private val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE =
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
