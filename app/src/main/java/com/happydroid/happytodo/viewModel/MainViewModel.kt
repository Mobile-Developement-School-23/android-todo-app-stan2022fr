package com.happydroid.happytodo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.ToDoApplication
import com.happydroid.happytodo.data.TodoItemsRepository


class MainViewModel(application: Application) : AndroidViewModel(application)  {
    private val todolistPreviewRepository = TodoItemsRepository.getInstance()
    private val todolistAdapter: TodolistAdapter
    private val resources = application.resources

    init {
        todolistAdapter = TodolistAdapter(application as ToDoApplication)
    }
    fun setRecyclerView(todolistRecyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(getApplication<Application>().applicationContext, LinearLayoutManager.VERTICAL, false)
        todolistRecyclerView.adapter = todolistAdapter
        todolistRecyclerView.layoutManager = layoutManager
        todolistRecyclerView.addItemDecoration(TodoItemOffsetItemDecoration(bottomOffset = resources.getDimensionPixelOffset(
            R.dimen.bottomOffset_ItemDecoration)))
        todolistAdapter.todoItems = todolistPreviewRepository.getTodoItems()
    }
}

