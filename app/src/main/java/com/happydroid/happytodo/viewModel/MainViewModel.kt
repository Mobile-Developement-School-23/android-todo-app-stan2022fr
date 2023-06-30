package com.happydroid.happytodo.viewModel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.ToDoApplication
import com.happydroid.happytodo.data.TodoItemsRepository
import com.happydroid.happytodo.view.AddTodoFragment


class MainViewModel(private val application: Application) : AndroidViewModel(application)  {
    private val todoItemsRepository = TodoItemsRepository.getInstance()
    private val todolistAdapter: TodolistAdapter = TodolistAdapter()
    private val resources = application.resources

    fun setRecyclerView(todolistRecyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(getApplication<Application>().applicationContext, LinearLayoutManager.VERTICAL, false)
        todolistRecyclerView.adapter = todolistAdapter

        todolistRecyclerView.layoutManager = layoutManager
        todolistRecyclerView.addItemDecoration(TodoItemOffsetItemDecoration(bottomOffset = resources.getDimensionPixelOffset(
            R.dimen.bottomOffset_ItemDecoration)))
        todolistAdapter.todoItems = todoItemsRepository.getTodoItems()

        todolistAdapter.checkboxClickListener = { todoId, isChecked ->
            changeStatusTodoItem(todoId, isChecked)
        }

        todolistAdapter.infoClickListener = { todoId ->
            editTodoItem(todoId)
        }
    }
    private fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean){
        todoItemsRepository.changeStatusTodoItem(idTodoItem, isDone)
    }

    private fun editTodoItem(idTodoItem: String){

        val addTodoFragment = AddTodoFragment()
        val fragmentManager = (application as ToDoApplication).getFragmentManager()
        // Создаем Bundle и добавляем данные
        val bundle  = Bundle()
        bundle.putString("idTodoItem", idTodoItem)
        addTodoFragment.arguments = bundle

        if (fragmentManager != null){
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, addTodoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}

