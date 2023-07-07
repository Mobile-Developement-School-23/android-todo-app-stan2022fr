package com.happydroid.happytodo

import android.app.Application
import androidx.fragment.app.FragmentManager

/**
 * Custom Application class allows to hold reference to [fragmentManager]
 * as long as application lives.
 */
class ToDoApplication : Application() {

    private var fragmentManager: FragmentManager? = null

    fun setFragmentManager(manager: FragmentManager) {
        fragmentManager = manager
    }

    fun getFragmentManager(): FragmentManager? {
        return fragmentManager
    }
}
