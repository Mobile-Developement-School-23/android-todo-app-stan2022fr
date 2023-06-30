package com.happydroid.happytodo.viewModel

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem
import com.happydroid.happytodo.data.TodoItem.Priority.HIGH
import com.happydroid.happytodo.data.TodoItem.Priority.LOW
import com.happydroid.happytodo.data.TodoItem.Priority.NORMAL
import java.text.SimpleDateFormat
import java.util.Locale


class TodolistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val todoTextView: TextView = itemView.findViewById(R.id.text_todo)
    private val isDone: CheckBox = itemView.findViewById(R.id.checkbox_done)
    private val priority: ImageView = itemView.findViewById(R.id.priority)
    private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    fun onBind(todoItem: TodoItem) {

        // Обработака цвета и формата текста в зависимости от приоритета и статуса задачи
        isDone.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.i("hhh", "Holder   isDone.setOnCheckedChangeListener ${todoItem.id}")
            if (isChecked) {
                isDone.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color_green))
                todoTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.label_tertiary))
                todoTextView.paintFlags = todoTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                dateTextView.visibility = View.GONE
                priority.visibility = View.GONE
            } else {

                if (todoItem.priority == HIGH) {
                    isDone.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_red
                        )
                    )

                } else {
                    isDone.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.label_tertiary
                        )
                    )
                }
                todoTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.label_primary))
                todoTextView.paintFlags = todoTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                if (todoItem.deadline != null){
                    dateTextView.visibility = View.VISIBLE
                }
                if (todoItem.priority != NORMAL  ){
                    priority.visibility = View.VISIBLE
                }
            }
        }

        // Иконка для приоритета задачи
        when (todoItem.priority) {
            LOW -> {
                val typedValue = TypedValue()
                itemView.context.theme.resolveAttribute(R.attr.label_tertiary, typedValue, true)
                val color = typedValue.data
                priority.setColorFilter(color, PorterDuff.Mode.SRC_IN)

                priority.visibility = View.VISIBLE
                priority.setImageResource(R.drawable.low_priority)
            }
            NORMAL -> {
                priority.visibility = View.GONE
            }
            HIGH -> {
                val typedValue = TypedValue()
                itemView.context.theme.resolveAttribute(R.attr.color_red, typedValue, true)
                val color = typedValue.data
                priority.setColorFilter(color, PorterDuff.Mode.SRC_IN)

                priority.visibility = View.VISIBLE
                priority.setImageResource(R.drawable.high_priority)

            }
        }
        if (todoItem.deadline != null){
            dateTextView.text = dateFormatter.format(todoItem.deadline)
            dateTextView.visibility = View.VISIBLE
        }
        todoTextView.text = todoItem.text
        isDone.isChecked = todoItem.isDone
    }
}