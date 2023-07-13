package com.happydroid.happytodo.data.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * A worker class responsible for updating data on the server when the device is connected.
 */
class SyncWorker @Inject constructor(appContext: Context, workerParams: WorkerParameters, private val todoItemsRepository: TodoItemsRepository) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
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

