package com.adryde.mobile.displaysdk.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adryde.mobile.displaysdk.data.user.PackageRepositoryFactory
import com.adryde.mobile.displaysdk.data.user.model.PackageDataResponse
import com.adryde.mobile.displaysdk.locationtrack.repository.LocationRepository
import com.adryde.mobile.displaysdk.networkmodel.Either
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class DisplayViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MainCattleProfileVM"
    }

   // val appContext = application.applicationContext

    private val packageDataResponseInternal: MutableLiveData<PackageDataResponse?> = MutableLiveData()
    val packageDataResponse: LiveData<PackageDataResponse?> = packageDataResponseInternal

    val packageRepo = PackageRepositoryFactory.createRemoteRepository(application)

    /**
     * Method for fetching Metadata
     *
     * @author Yagna Joshi
     */
    fun fetchMetadata() {
      /*  val authRepo = AuthRepositoryFactory.create(appContext)
        var isMetaFetched = false

        viewModelScope.launch {
            masterDataRepository.fetchMasterData(languageCode = prefferedLang)
        }

            var farmMeta: FarmMetadata? = null
            viewModelScope.launch {
                val result = authRepo.getFarmerInfo()

                when (result) {
                    is Either.Success -> {
                        farmId = result.success.farmId
                        farmMeta = metaRepo.getFarmMetadata(farmId)

                        metaDataSuccessInternal.postValue(farmMeta)
                        farmMeta?.let {
                            if (it.cattle.isNotEmpty()) {
                                isMetaFetched = true
                            }
                        }
                    }
                    is Either.Error -> {
                        isMetaFetched = false
                        Log.d(TAG, "Fetching metadata failed: ${result.error.nitaraError?.message}")
                    }
                }
            }.invokeOnCompletion {
                metadataErrorResultInternal.postValue(isMetaFetched)
            }
            */

    }

    private val locationRepository = LocationRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    val receivingLocationUpdates: LiveData<Boolean> = locationRepository.receivingLocationUpdates

    val locationListLiveData = locationRepository.getLocations()

    fun startLocationUpdates() = locationRepository.startLocationUpdates()

    fun stopLocationUpdates() = locationRepository.stopLocationUpdates()

    fun fetchPackage(s: String, map: Map<String, String>) {
        viewModelScope.launch {
            when (val result = packageRepo.fetchPackageFromRemote(s,map)) {
                is Either.Success -> {
                    val packageData = result.success
                    packageDataResponseInternal.postValue(packageData)
                }
                is Either.Error -> {
                    Log.d(TAG, "Fetching package failed: ${result.error.adrydeError?.message}")
                }
            }
        }.invokeOnCompletion {
         //Do Nothing
        }

        }

}