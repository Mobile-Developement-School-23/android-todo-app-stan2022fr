package com.happydroid.happytodo.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import com.happydroid.happytodo.presentation.additem.AddTodoFragment
import com.happydroid.happytodo.presentation.main.rv.TodoItemOffsetItemDecoration
import com.happydroid.happytodo.presentation.main.rv.TodolistAdapter
import com.happydroid.happytodo.presentation.settings.SettingsFragment
import kotlinx.coroutines.launch

/**
 * This class represents the main fragment of the application.
 */
class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = TodoItemsRepository.getInstance(requireActivity().application)
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private val todolistAdapter: TodolistAdapter = TodolistAdapter()
    private lateinit var doneTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.startObservingLifecycle(viewLifecycleOwner.lifecycle)

        doneTextView = view.findViewById(R.id.title_done_number)

        val todolistRecyclerView: RecyclerView = view.findViewById(R.id.todolist)
        setRecyclerView(todolistRecyclerView)

        lifecycleScope.launch {
            mainViewModel.todoItemsResult.collect { todoItemsResult ->
                updateAdapterData(todoItemsResult.data)

                if (todoItemsResult.errorMessages.isNotEmpty()) {
                    showMessage(view, todoItemsResult.errorMessages[0])
                }
            }
        }

        addFinishedItemSwitchIcon(view)
        addFloatingAddTaskButton(view)
    }

    private fun addFloatingAddTaskButton(view: View) {
        val fabAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            // запускаем fragment для добавления задачи
            val addTodoFragment = AddTodoFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, addTodoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun addFinishedItemSwitchIcon(view: View) {
        val finishedItemsSwitchIcon = view.findViewById<ImageView>(R.id.switchIconImageView)
        finishedItemsSwitchIcon.setOnClickListener {
            if (mainViewModel.showOnlyUnfinishedItems) {
                finishedItemsSwitchIcon.setImageResource(R.drawable.visibility)
            } else {
                finishedItemsSwitchIcon.setImageResource(R.drawable.visibility_off)
            }
            mainViewModel.showOnlyUnfinishedItems = !mainViewModel.showOnlyUnfinishedItems
            updateAdapterData(mainViewModel.todoItemsResult.value.data)
        }
    }

    private fun showMessage(view: View, errorCode: ErrorCode) {
        val errorMessagesText = getString(errorCode.stringResId)
        val retryMessageText = getString(R.string.retry)
        val snackbar = Snackbar.make(
            view.findViewById(R.id.mainCoordinatorLayout),
            errorMessagesText,
            Snackbar.LENGTH_LONG
        )

        // Кнопка повтор
        if (errorCode == ErrorCode.NO_CONNECTION) {
            snackbar.setAction(retryMessageText) {
                mainViewModel.fetchFromRemote()
            }
            snackbar.duration = Snackbar.LENGTH_INDEFINITE
        }

        // Убираем сообщение из очереди
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                mainViewModel.onErrorDismiss(errorCode)
            }
        })
        snackbar.show()
    }

    private fun updateAdapterData(todoItems: List<TodoItem>) {
        if (mainViewModel.showOnlyUnfinishedItems) {
            todolistAdapter.submitList(todoItems.filter { it.isDone == false })
        } else {
            todolistAdapter.submitList(todoItems)
        }
        updateDoneText()
    }

    fun updateDoneText() {
        val textDone =
            getString(R.string.text_done) + mainViewModel.todoItemsResult.value.data.filter { it.isDone }.size.toString()
        doneTextView.text = textDone
    }

    /**
     * Иконка для запуска фрагмента настроек
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingsFragment = SettingsFragment()
                val fragmentManager = requireActivity().supportFragmentManager

                fragmentManager.beginTransaction()
                    .replace(R.id.container, settingsFragment)
                    .addToBackStack(null)
                    .commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setRecyclerView(todolistRecyclerView: RecyclerView) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        todolistRecyclerView.adapter = todolistAdapter
        todolistRecyclerView.layoutManager = layoutManager
        todolistRecyclerView.addItemDecoration(
            TodoItemOffsetItemDecoration(
                bottomOffset = resources.getDimensionPixelOffset(
                    R.dimen.bottomOffset_ItemDecoration
                )
            )
        )

        // Смена статуса задачи
        todolistAdapter.checkboxClickListener = { todoId, isChecked ->
            mainViewModel.changeStatusTodoItem(todoId, isChecked)
        }

        // Редактирование задачи
        todolistAdapter.infoClickListener = { todoId ->
            onEditTodoItem(todoId)
        }
    }

    private fun onEditTodoItem(idTodoItem: String) {
        val addTodoFragment = AddTodoFragment()
        val fragmentManager =
            requireActivity().supportFragmentManager

        val bundle = Bundle()
        bundle.putString("idTodoItem", idTodoItem)
        addTodoFragment.arguments = bundle

        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, addTodoFragment)
            addToBackStack(null)
            commit()
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
