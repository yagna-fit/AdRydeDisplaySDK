package com.adryde.mobile.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class EncSharedPreferences {

    companion object {
        lateinit var editor: SharedPreferences.Editor
        lateinit var sharedPreferences: SharedPreferences

        fun init(context: Context) {
            val masterKey: MasterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

             sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "secret_shared_prefs1",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            // use the shared preferences and editor as you normally would

            // use the shared preferences and editor as you normally would
            editor = sharedPreferences.edit()
        }

        fun setStringData(key : String, value: String) {
            editor.putString(key, value).apply()
        }

        fun getStringData(key: String): String {
            return sharedPreferences.getString(key,"")?:"";
        }

        fun saveLastSyncInMillis(value: Long) {
            editor.putLong(Constants.KEY_SYNC_DATE, value).apply()
        }

        fun getLastSyncInMillis(): Long {
            return sharedPreferences.getLong(Constants.KEY_SYNC_DATE,1668921218);
        }

        fun saveBooleanData(key : String, value: Boolean) {
            editor.putBoolean(key, value).apply()
        }

        fun getBooleanData(key: String) :Boolean {
            return sharedPreferences.getBoolean(key, false);
        }

    }
}