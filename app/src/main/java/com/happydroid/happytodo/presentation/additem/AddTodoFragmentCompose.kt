package com.happydroid.happytodo.presentation.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.happydroid.happytodo.presentation.additem.compose.AddTodoScreen
import com.happydroid.happytodo.presentation.additem.compose.AppTheme
import javax.inject.Inject

/**
 * This class represents a fragment that allows users to edit or add a new todo item.
 */
class AddTodoFragmentCompose : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val addTodoViewModel: AddTodoViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val composeView = ComposeView(requireContext())
        val isDarkTheme =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        composeView.setContent {
            AppTheme(isDarkTheme) {
                AddTodoScreen()
            }
        }

        return composeView
    }


}
