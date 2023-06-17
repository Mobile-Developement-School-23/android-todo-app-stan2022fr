package com.happydroid.happytodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.happydroid.happytodo.view.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Сохраняем FragmentManager в Application, чтобы использовать его в TodolistAdapter
        val application = application as ToDoApplication
        application.setFragmentManager(supportFragmentManager)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}