package com.adryde.mobile.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.adryde.mobile.displaysdk.util.AESEncoder
import com.adryde.mobile.displaysdk.util.Decompress
import com.adryde.mobile.util.Constants
import com.adryde.mobile.util.EncSharedPreferences
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

object CoreData {

    private const val TAG = "CoreData"
    private lateinit var applicationContext: Context
    private val SYNC_WORKER = "SyncWorker-${UUID.randomUUID()}"
    private var workId: UUID? = null
    private val _lastSyncOn = MutableLiveData<String?>()
    val lastSyncOn: LiveData<String?> = _lastSyncOn
    private val _decompressImage = MutableLiveData<String?>()
    val decompressImage: LiveData<String?> = _decompressImage

    internal fun init(context: Context){
        applicationContext = context

        EncSharedPreferences.saveLastSyncInMillis(1668921218)

        synchronized(this) {
            if (EncSharedPreferences.getStringData(Constants.KEY_SECRET).isNotEmpty()) {
                createSyncWorker()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun decompressAsset() {

        val folderPath = EncSharedPreferences.getStringData(Constants.KEY_PROFILE_DIR_PATH)

        if (EncSharedPreferences.getStringData(Constants.KEY_PROFILE_DIR_PATH).isNotEmpty() && File(folderPath).exists()) {
            _decompressImage.postValue(Constants.DONE)
            return
        }
        val ctx: Context = applicationContext
        _decompressImage.postValue(Constants.DECOMPRESSING)
        GlobalScope.launch {
            val folder: File = ctx.getDir("BPAProfiles", Context.MODE_PRIVATE)
            val file = AESEncoder.decrypt(applicationContext,"bpaprofiles", folder)
            Decompress(file.absolutePath, folder.absolutePath).unzip()
            file.delete()
            EncSharedPreferences.setStringData(Constants.KEY_PROFILE_DIR_PATH, folder.absolutePath)
            _decompressImage.postValue(Constants.DONE)
        }

    }

    internal fun createSyncWorker() {
        /**
         * cattle registered in offline mode are not submitting to server when user comes in online
         * @author Nishikanta
         */

        //Todo - Uncomment below part when want to start sync worker
       /* Log.d(TAG, "Creating background sync worker...")
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            SYNC_WORKER,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
        workId = oneTimeWorkRequest.id*/
    }

    internal fun nullifyWorkId() {
        workId = null
        _lastSyncOn.postValue(Constants.DONE)
    }

    fun clearCaches() {
        _decompressImage.postValue(null)
    }

    fun setSyncStatus(state: String) {
        _lastSyncOn.postValue(state)
    }
}