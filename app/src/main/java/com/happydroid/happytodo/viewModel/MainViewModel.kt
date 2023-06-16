package com.happydroid.happytodo.viewModel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.data.TodoItemsRepository


class MainViewModel(application: Application) : AndroidViewModel(application)  {
//    private val context: Context
    private val todolistPreviewRepository = TodoItemsRepository()
    private val context = getApplication<Application>().applicationContext

    fun setRecyclerView(todolistRecyclerView: RecyclerView) {
        val todolistAdapter = TodolistAdapter()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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