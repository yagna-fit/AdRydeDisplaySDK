package com.adryde.mobile.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class AdrSharedPreferences {
    companion object {
        lateinit var editor: SharedPreferences.Editor
        lateinit var sharedPreferences: SharedPreferences

        fun init(context: Context) {
            val masterKey: MasterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "adr_shared_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            // use the shared preferences and editor as you normally would
            editor = sharedPreferences.edit()
        }

        fun setIsStoreSetupDone(value: Boolean) {
            editor.putBoolean(Constants.KEY_STORE, value).apply()
        }

        fun isStoreSetupDone(): Boolean {
            return sharedPreferences.getBoolean(Constants.KEY_STORE, false)
        }

        fun setLanguage(value: String) {
            editor.putString(Constants.KEY_LANGUAGE, value).apply()
        }

        fun getLanguage(): String {
            return sharedPreferences.getString(Constants.KEY_LANGUAGE, "en") ?: "en";
        }

        fun setTutorialScreen(value: Boolean) {
            editor.putBoolean(Constants.KEY_TUTORIAL, value).apply()
        }

        fun isTutorialSetupDone(): Boolean {
            return sharedPreferences.getBoolean(Constants.KEY_TUTORIAL, false)
        }
    }
}