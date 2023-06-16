package com.happydroid.happytodo.viewModel

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem

class TodolistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val text_todo: TextView = itemView.findViewById(R.id.text_todo)
    private val isDone: CheckBox = itemView.findViewById(R.id.checkbox_done)

    fun onBind(todoItem: TodoItem) {

        isDone.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isDone.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color_green))
                text_todo.setTextColor(ContextCompat.getColor(itemView.context, R.color.label_tertiary))
                text_todo.paintFlags = text_todo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                isDone.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.label_tertiary))
                text_todo.setTextColor(ContextCompat.getColor(itemView.context, R.color.label_primary))
                text_todo.paintFlags = text_todo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        text_todo.text = todoItem.text
        isDone.isChecked = todoItem.isDone
    }
}