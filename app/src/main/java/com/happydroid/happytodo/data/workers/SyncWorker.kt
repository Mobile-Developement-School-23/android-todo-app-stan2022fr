package com.happydroid.happytodo.data.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.happydroid.happytodo.ToDoApplication
import kotlinx.coroutines.runBlocking

/**
 * A worker class responsible for updating data on the server when the device is connected.
 */
class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            val todoItemsRepository =
                (applicationContext as ToDoApplication).appComponent.todoItemsRepository()
            runBlocking {
                todoItemsRepository.saveAllTodoItemsToRemote()
                todoItemsRepository.fetchFromRemoteApi()
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("DataUpdateWorker", "Exception: ${e.message}")
            return Result.failure()
        }
    }
}

