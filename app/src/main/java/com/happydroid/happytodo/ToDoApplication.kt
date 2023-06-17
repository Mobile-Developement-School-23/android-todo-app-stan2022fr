package com.happydroid.happytodo

import android.app.Application
import androidx.fragment.app.FragmentManager

class ToDoApplication: Application() {

    private var fragmentManager: FragmentManager? = null

    fun setFragmentManager(manager: FragmentManager) {
        fragmentManager = manager
    }

    fun getFragmentManager(): FragmentManager? {
        return fragmentManager
    }
}