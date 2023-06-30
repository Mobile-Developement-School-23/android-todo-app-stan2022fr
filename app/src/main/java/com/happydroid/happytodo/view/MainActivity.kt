package com.happydroid.happytodo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.happydroid.happytodo.R
import com.happydroid.happytodo.ToDoApplication

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Сохраняем FragmentManager в Application, чтобы использовать его в MainViewModel
        val application = application as ToDoApplication
        application.setFragmentManager(supportFragmentManager)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}