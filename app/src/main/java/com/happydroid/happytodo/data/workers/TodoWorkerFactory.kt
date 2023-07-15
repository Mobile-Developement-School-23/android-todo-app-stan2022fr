package com.happydroid.happytodo.data.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.happydroid.happytodo.di.AppScope
import javax.inject.Inject

@AppScope
class TodoWorkerFactory @Inject constructor(
    private val notificationWorkerFactory: NotificationWorker.Factory,
    private val dataUpdateWorkerFactory: DataUpdateWorker.Factory,
    private val syncWorkerFactory: SyncWorker.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            DataUpdateWorker::class.java.name -> {
                dataUpdateWorkerFactory.create(appContext, workerParameters)
            }

            SyncWorker::class.java.name -> {
                syncWorkerFactory.create(appContext, workerParameters)
            }

            NotificationWorker::class.java.name ->
                notificationWorkerFactory.create(appContext, workerParameters)

            else -> null
        }
    }
}
