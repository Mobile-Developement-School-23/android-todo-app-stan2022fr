package com.happydroid.happytodo.presentation.main.rv

import androidx.recyclerview.widget.DiffUtil
import com.happydroid.happytodo.data.model.TodoItem

class CommonCallbackImpl : DiffUtil.ItemCallback<TodoItem>() {

    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.equals(newItem)
    }
}
