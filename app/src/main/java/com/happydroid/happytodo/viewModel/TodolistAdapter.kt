package com.happydroid.happytodo.viewModel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem


class TodolistAdapter() : RecyclerView.Adapter<TodolistViewHolder>() {

    var checkboxClickListener: ((todoId: String, isChecked : Boolean) -> Unit)? = null
    var infoClickListener: ((todoId: String) -> Unit)? = null

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
        val todoItem = todoItems[position]
        Log.i("hhh", todoItem.id + " " + todoItem.text)

        val checkboxDone: CheckBox = holder.itemView.findViewById(R.id.checkbox_done)
        checkboxDone.setOnClickListener {
            Log.i("hhh", " Adapter isDone.setOnClickListener ${todoItem.id}")
            checkboxClickListener?.invoke(todoItem.id, (it as CheckBox).isChecked)
        }
        val buttonInfo: ImageView = holder.itemView.findViewById(R.id.button_info)
        buttonInfo.setOnClickListener {
            Log.i("hhh", " Adapter buttonInfo.setOnClickListener")
            infoClickListener?.invoke(todoItem.id)
        }

        holder.onBind(todoItems[position])

    }

}