package com.happydroid.happytodo.viewModel

import android.app.Application
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.ToDoApplication
import com.happydroid.happytodo.data.TodoItemsRepository


class MainViewModel(application: Application) : AndroidViewModel(application)  {
    private val todolistPreviewRepository = TodoItemsRepository()
    private val todolistAdapter: TodolistAdapter

    init {
        todolistAdapter = TodolistAdapter(application as ToDoApplication)
    }
    fun setRecyclerView(todolistRecyclerView: RecyclerView) {

        val layoutManager = LinearLayoutManager(getApplication<Application>().applicationContext, LinearLayoutManager.VERTICAL, false)
        todolistRecyclerView.adapter = todolistAdapter
        todolistRecyclerView.layoutManager = layoutManager
        todolistRecyclerView.addItemDecoration(TodoItemOffsetItemDecoration(bottomOffset = 16f.toPx.toInt()))
        todolistAdapter.todoItems = todolistPreviewRepository.getTodoItems()
        Log.i("setRecyclerView", todolistAdapter.todoItems[0].text)

    }
}


val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )