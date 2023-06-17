package com.happydroid.happytodo.viewModel

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem

class TodolistViewHolder(itemView: View, val application: Application) : RecyclerView.ViewHolder(itemView) {

    private val text_todo: TextView = itemView.findViewById(R.id.text_todo)
    private val isDone: CheckBox = itemView.findViewById(R.id.checkbox_done)
    private val priority: ImageView = itemView.findViewById(R.id.priority)

    fun onBind(todoItem: TodoItem) {

        // Обработака цвета и формата текста при нажатии на Checkbox
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

        // Иконка для приоритета задачи
        when (todoItem.priority) {
            TodoItem.Priority.LOW -> {
                // Получение цвета из аттрибутов
                val typedValue = TypedValue()
                itemView.context.theme.resolveAttribute(R.attr.label_tertiary, typedValue, true)
                val color = typedValue.data
                priority.setColorFilter(color, PorterDuff.Mode.SRC_IN)

                priority.visibility = View.VISIBLE
                priority.setImageResource(R.drawable.low_priority)
            }
            TodoItem.Priority.NORMAL -> {
                priority.visibility = View.GONE
            }
            TodoItem.Priority.HIGH -> {
                // Получение цвета из аттрибутов
                val typedValue = TypedValue()
                itemView.context.theme.resolveAttribute(R.attr.color_red, typedValue, true)
                val color = typedValue.data
                priority.setColorFilter(color, PorterDuff.Mode.SRC_IN)

                priority.visibility = View.VISIBLE
                priority.setImageResource(R.drawable.high_priority)
            }
        }

        text_todo.text = todoItem.text
        isDone.isChecked = todoItem.isDone
    }
}