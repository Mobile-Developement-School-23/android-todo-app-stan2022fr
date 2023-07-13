package com.happydroid.happytodo

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.happydroid.happytodo.data.workers.DataUpdateWorker
import com.happydroid.happytodo.di.AppComponent
import com.happydroid.happytodo.di.AppModule
import com.happydroid.happytodo.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

private const val UPDATE_EVERY_HOURS = 8L

/**
 * Custom Application class allows to use WorkManager and hold reference to [fragmentManager]
 * as long as application lives.
 */
class ToDoApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private var fragmentManager: FragmentManager? = null

    override fun onCreate() {
        super.onCreate()
        startPeriodicDataUpdate()
    }

    private fun startPeriodicDataUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()

        val dataUpdateRequest = PeriodicWorkRequestBuilder<DataUpdateWorker>(UPDATE_EVERY_HOURS, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dataUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            dataUpdateRequest
        )
    }


    fun setFragmentManager(manager: FragmentManager) {
        fragmentManager = manager
    }

    fun getFragmentManager(): FragmentManager? {
        return fragmentManager
    }
}
