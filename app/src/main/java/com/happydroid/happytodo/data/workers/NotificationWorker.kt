package com.happydroid.happytodo.data.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import com.happydroid.happytodo.presentation.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * A worker class responsible for showing the notifications.
 */

class Foo @Inject constructor()

class NotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val todoItemsRepository: TodoItemsRepository,
) :
    Worker(appContext, workerParams) {


    override fun doWork(): Result {
        try {

            todoItemsRepository.todoItemsResult.value.data.onEach {
                if (isToday(it.deadline)) {
                    sendNotification(it.id, it.priority, it.text)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Exception: ${e.message}")
            return Result.failure()
        }
    }

    private fun isToday(date: Date?): Boolean {
        if (date == null)
            return false
        val cal1 = Calendar.getInstance()
        cal1.time = date
        val cal2 = Calendar.getInstance()

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }


    private fun sendNotification(
        idTodoItem: String,
        priority: TodoItem.Priority,
        textTodoItem: String
    ) {

        var suffix = ""
        when (priority) {
            TodoItem.Priority.HIGH -> {
                suffix = appContext.getString(R.string.suffix_priority_high)
            }

            TodoItem.Priority.LOW -> {
                suffix = appContext.getString(R.string.suffix_priority_low)
            }

            else -> {
                suffix = appContext.getString(R.string.suffix_priority_default)
            }
        }

        val idNotification = System.currentTimeMillis().toInt()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(idNotification.toString(), idTodoItem)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_white)
            .setContentTitle(appContext.getString(R.string.notification_title) + suffix)
            .setContentText(textTodoItem)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).setGroup(NOTIFICATION_GROUP)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)
        }

        notificationManager.notify(idNotification, notification.build())
//        Log.i("hhh", idNotification.toString())
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "happy_channel_01"
        const val NOTIFICATION_GROUP = "happy_group_01"
    }


    @AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): NotificationWorker
    }
}

