package com.happydroid.happytodo.domain

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.workers.NotificationWorker
import com.happydroid.happytodo.presentation.MainActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationUseCase constructor(private val mainActivity: MainActivity) {

    private val notificationManager = NotificationManagerCompat.from(mainActivity)

    fun initNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeChannels(notificationManager)
        }

        val areNotificationsEnabled =
            NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()
        Log.i("hhh", "areNotificationsEnabled " + areNotificationsEnabled)

        initNotificationWorkManager()
    }

    fun initNotificationWorkManager() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 24)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        // Расчет времени до следующей полуночи
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        Log.i("hhh", "initNotificationWorkManager start in " + timeDiff)
        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(mainActivity).enqueueUniquePeriodicWork(
            "notificationWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationRequest
        )

// Для теста! Показ уведомлений сразу при запуске.
        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(2000L, TimeUnit.MILLISECONDS).build()

        val instanceWorkManager = WorkManager.getInstance(mainActivity)
//        очистка кэша workmanager
//        instanceWorkManager.cancelAllWork()
//        instanceWorkManager.pruneWork()

        instanceWorkManager.beginUniqueWork(
            "notificationWork2",
            ExistingWorkPolicy.REPLACE, notificationWork
        ).enqueue()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        val permissionId = Manifest.permission.POST_NOTIFICATIONS
        when {
            ActivityCompat.checkSelfPermission(
                mainActivity,
                permissionId
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("hhh", "PackageManager.PERMISSION_GRANTED")

                return
            }

            mainActivity.shouldShowRequestPermissionRationale(permissionId) -> {
                showPermissionRationaleDialog(permissionId)
            }

            else -> {
                requestNotificationPermission.launch(permissionId)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeChannels(notificationManager: NotificationManagerCompat) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
        val name = mainActivity.getString(R.string.chanel_title)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = mainActivity.getString(R.string.chanel_description)
        notificationManager.createNotificationChannel(mChannel)

        val channel = notificationManager.getNotificationChannel(CHANNEL_ID)

        if (channel != null && channel.importance == NotificationManager.IMPORTANCE_NONE) {
            // Уведомления отключены для этой категории - показать диалог
            showEnableNotificationsDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEnableNotificationsDialog() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setMessage(mainActivity.getString(R.string.message_dialog_permition1))
            .setPositiveButton(mainActivity.getString(R.string.open_settings)) { dialog, id ->
                openNotificationSettings()
            }
            .setNegativeButton(mainActivity.getString(R.string.cancel)) { dialog, id ->

                Log.i("hhh", "User cancelled the dialog")
            }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, mainActivity.packageName)
        }
        mainActivity.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog(permissionId: String) {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setMessage(mainActivity.getString(R.string.message_dialog_permition2))
            .setPositiveButton(mainActivity.getString(R.string.allow)) { dialog, id ->
                requestNotificationPermission.launch(permissionId)
            }
            .setNegativeButton(mainActivity.getString(R.string.cancel)) { dialog, id ->
                Log.i("hhh", "User cancelled the dialog")
            }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestNotificationPermission =
        mainActivity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("hhh", "requestNotificationPermission isGranted")
            } else {
                Toast.makeText(
                    mainActivity,
                    mainActivity.getString(R.string.message_no_notify),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    companion object {
        const private val CHANNEL_ID = "happy_channel_01"
    }


}
