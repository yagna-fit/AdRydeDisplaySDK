package com.adryde.mobile.displaysdk.util


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Helper functions to simplify permission checks/requests.
 */
fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    ) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

/**
 * Requests permission and if the user denied a previous request, but didn't check
 * "Don't ask again", we provide additional rationale.
 *
 * Note: The Snackbar should have an action to request the permission.
 */
fun Fragment.requestPermissionWithRationale(
    permission: String,
    requestCode: Int,
    snackbar: Snackbar
) {
    val provideRationale = shouldShowRequestPermissionRationale(permission)

    if (provideRationale) {
        snackbar.show()
    } else {
        requestPermissions(arrayOf(permission), requestCode)
    }
}


fun appLog(message: String) {
    Log.v("AdRyde", message)
}

private val SYNC_WORKER = "SyncWorker-${UUID.randomUUID()}"
private var workId: UUID? = null
internal fun downloadFile(context: Context, url: String, fileName: String): UUID? {
    KEY_FILE_URL = url
    KEY_FILE_NAME = fileName
    Log.d("KEY_FILE_URL", "url: ${url} | ${fileName}")

    Log.d("KEY_FILE_URL", "KEY_FILE_URL: ${KEY_FILE_URL} | ${KEY_FILE_NAME}")

    /**
     * cattle registered in offline mode are not submitting to server when user comes in online
     * @author Nishikanta
     */
    Log.d("downloadFile", "Creating background sync worker...")
    val workConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<FileDownloadWorker>()
        .setInitialDelay(1, TimeUnit.SECONDS)
        .setConstraints(workConstraints)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        SYNC_WORKER,
        ExistingWorkPolicy.REPLACE,
        oneTimeWorkRequest
    )

    workId = oneTimeWorkRequest.id

    return workId
}

var KEY_FILE_URL: String? = null
var KEY_FILE_NAME: String? = null


class FileDownloadWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val fileUrl = KEY_FILE_URL ?: ""
        val fileName = KEY_FILE_NAME ?: ""

        Log.d("FileDownloadWorker", "doWork: $fileUrl | $fileName")


        if (fileName.isEmpty()
            || fileUrl.isEmpty()
        ) {
            return  Result.failure()
        } else {

            val filePath = getSavedFileUri(
                fileName = fileName,
                fileUrl = fileUrl,
                context = context
            )

            Log.d("FileDownloadWorker", "filePath: $filePath")


            return if (filePath != null) {
                Result.success(workDataOf("KEY_FILE_URI" to filePath))
            } else {
                Result.failure()
            }
        }
    }
}


private fun getSavedFileUri(
    fileName: String,
    fileUrl: String,
    context: Context
): String {
    val target = File(context.cacheDir, fileName)

    Log.e("fileUrl", fileUrl)

    URL(fileUrl).openStream().use { input ->
        FileOutputStream(target).use { output ->
            input.copyTo(output)
        }
    }

    return target.absolutePath;

}
