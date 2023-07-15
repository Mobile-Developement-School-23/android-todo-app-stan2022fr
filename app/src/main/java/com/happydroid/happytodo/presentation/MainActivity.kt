package com.happydroid.happytodo.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.happydroid.happytodo.R
import com.happydroid.happytodo.ToDoApplication
import com.happydroid.happytodo.domain.NotificationUseCase
import com.happydroid.happytodo.presentation.main.MainFragment
import javax.inject.Inject

/**
 * This class represents the main activity of the application.
 */
class MainActivity @Inject constructor() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationUseCase(this).initNotifications()

        val application = application as ToDoApplication
        application.setFragmentManager(supportFragmentManager)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

    }

}
