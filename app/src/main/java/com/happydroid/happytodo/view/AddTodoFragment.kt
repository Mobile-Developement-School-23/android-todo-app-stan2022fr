package com.happydroid.happytodo.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem
import com.happydroid.happytodo.viewModel.AddTodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddTodoFragment : Fragment() {

    private lateinit var viewModel: AddTodoViewModel
    private var selectedDate: Date? = null
    private var todoItem : TodoItem? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val monthNames = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_todo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTodoViewModel::class.java)
        val dateSwitch: SwitchCompat = view.findViewById(R.id.dateSwitch)
        val dateText: TextView = view.findViewById(R.id.dateTextView)
        val deleteButton: TextView = view.findViewById(R.id.deleteButton)
        val editText: EditText = view.findViewById(R.id.editText)

        // получаем TodoItem из Bundle
        val bundle = arguments
        val idTodoItem = bundle?.getString("idTodoItem")
        todoItem = idTodoItem?.let { viewModel.getTodoItem(it) }
        if (todoItem != null) {

            editText.text = Editable.Factory.getInstance().newEditable(todoItem!!.text)
            deleteButton.isEnabled = true

            val prioritySpinner : Spinner =  view.findViewById(R.id.prioritySpinner)
            prioritySpinner.setSelection(todoItem!!.priority.ordinal)

            if (todoItem!!.deadline != null){
                dateSwitch.isChecked = true

                val calendar = Calendar.getInstance()
                calendar.time = todoItem!!.deadline!!
                val formattedDate = "${calendar.get(Calendar.DAY_OF_MONTH)} ${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"
                dateText.text= formattedDate

                dateText.visibility = View.VISIBLE
            }

        }else{
            deleteButton.isEnabled = false
            deleteButton.alpha = 0.5f
        }


        // Установка обработчика клика на кнопку "Сохранить"
        val saveButton: TextView = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            if (editText.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Название задачи не должно быть пустым!", Toast.LENGTH_SHORT).show()
            }else{
                saveTodo(todoItem?.id)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // Установка обработчика клика на кнопку "Закрыть"
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
           requireActivity().supportFragmentManager.popBackStack()
        }


        // Установка обработчика клика на кнопку "Удалить"
        deleteButton.setOnClickListener {
            todoItem?.id?.let { viewModel.deleteTodoItem(todoItem?.id!!) }
            requireActivity().supportFragmentManager.popBackStack()
        }


        // Показать календарь при включении переключателя
        dateSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showCalendar(dateText)
            } else {
                hideDateText(dateText)
            }
        }

        dateText.setOnClickListener {
            showCalendar(dateText)
        }

    }

    private fun showCalendar(dateText: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { view, selectedYear, selectedMonth, selectedDay ->
            val selectedDateCalendar = Calendar.getInstance()
            selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = dateFormat.parse("$selectedDay/$selectedMonth/$selectedYear")
            val formattedDate = "$selectedDay ${monthNames[selectedMonth]} $selectedYear"
            dateText.text= formattedDate
            dateText.visibility = View.VISIBLE

        }, year, month, day)

        datePickerDialog.show()
    }

    private fun hideDateText(dateText: TextView) {
        dateText.visibility = View.GONE
    }

    fun saveTodo(oldId : String?) {
        var id = ""
        val editTextTodoText = requireView().findViewById<EditText>(R.id.editText)
        val spinnerPriority = requireView().findViewById<Spinner>(R.id.prioritySpinner)

        val todoText = editTextTodoText.text.toString()
        val priorityId = spinnerPriority.selectedItemId

        // если это редактирование, то используем прошлый id
        if (oldId == null){
            id = UUID.randomUUID().toString()
        }else{
            id = oldId
        }

        val todoItem = TodoItem(
            id = id,
            text = todoText,
            priority = TodoItem.Priority.values()[priorityId.toInt()],
            deadline = selectedDate,
            isDone = false,
            createdDate = Date(),
            modifiedDate = null
        )
        Log.i("hhh", "selectedDate $selectedDate")
        viewModel.addTodoItem(todoItem)
    }

}