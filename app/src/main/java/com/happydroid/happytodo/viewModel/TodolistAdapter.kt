package com.happydroid.happytodo.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.ToDoApplication
import com.happydroid.happytodo.data.TodoItem
import com.happydroid.happytodo.view.AddTodoFragment


class TodolistAdapter(private val application: ToDoApplication) : RecyclerView.Adapter<TodolistViewHolder>() {

    var todoItems = listOf<TodoItem>()
        set(value) {
            val callback = CommonCallbackImpl(
                oldItems = field,
                newItems = value,
                { oldItem: TodoItem, newItem -> oldItem.id == newItem.id })
            field = value
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return  TodolistViewHolder(
                layoutInflater.inflate(
                    R.layout.todoitem,
                    parent,
                    false
                )
            )
    }


    override fun getItemCount() = todoItems.size

    override fun onBindViewHolder(holder: TodolistViewHolder, position: Int) {

        val buttonInfo: ImageView = holder.itemView.findViewById(R.id.button_info)
        buttonInfo.setOnClickListener {

            // запускаем fragment для изменения задачи (пока просто заглушка)
            val addTodoFragment = AddTodoFragment()
            val fragmentManager = application.getFragmentManager()
            if (fragmentManager != null){
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, addTodoFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                }
        }

        holder.onBind(todoItems[position])

    }

}