package com.happydroid.happytodo.presentation.main.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.model.TodoItem


class TodolistAdapter : ListAdapter<TodoItem, TodolistViewHolder>(CommonCallbackImpl()) {

    var checkboxClickListener: ((todoId: String, isChecked: Boolean) -> Unit)? = null
    var infoClickListener: ((todoId: String) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodolistViewHolder(
            layoutInflater.inflate(
                R.layout.todoitem,
                parent,
                false
            )
        )
    }

    /**
     * Fix для правильного отображения элементов в RV
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Fix для правильного отображения элементов в RV
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = this.currentList.size

    override fun onBindViewHolder(holder: TodolistViewHolder, position: Int) {
        val todoItem = getItem(position)

        // Смена статуса задачи
        val checkboxDone: CheckBox = holder.itemView.findViewById(R.id.checkbox_done)
        checkboxDone.setOnClickListener {
            checkboxClickListener?.invoke(todoItem.id, (it as CheckBox).isChecked)
        }

        // Редактирование задачи
        val buttonInfo: ImageView = holder.itemView.findViewById(R.id.button_info)
        buttonInfo.setOnClickListener {
            infoClickListener?.invoke(todoItem.id)
        }
        holder.onBind(todoItem)
    }
}
