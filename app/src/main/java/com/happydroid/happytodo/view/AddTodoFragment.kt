package com.happydroid.happytodo.view

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.happydroid.happytodo.viewModel.AddTodoViewModel
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItem
import java.util.*

class AddTodoFragment : Fragment() {

    companion object {
        fun newInstance() = AddTodoFragment()
    }

    private lateinit var viewModel: AddTodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_todo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTodoViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение NavController из NavHostFragment
//        val navController = Navigation.findNavController(requireActivity(), R.id.container)


        // Установка обработчика клика на кнопку "Сохранить"
        val saveButton: TextView = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveTodo()
            // Выполнение действия навигации назад
           requireActivity().supportFragmentManager.popBackStack()
        }

        // Установка обработчика клика на кнопку "Закрыть"
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            // Выполнение действия навигации назад
           requireActivity().supportFragmentManager.popBackStack()
        }

        // Установка обработчика клика на иконку "Удалить"
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
        deleteIcon.setOnClickListener {
            // Выполнение действия навигации назад
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Установка обработчика клика на кнопку "Удалить"
        val deleteButton: TextView = view.findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            // Выполнение действия навигации назад
            requireActivity().supportFragmentManager.popBackStack()
        }


        // Показать календарь при включении переключателя
        val dateSwitch: SwitchCompat = view.findViewById(R.id.dateSwitch)
        val dateText: TextView = view.findViewById(R.id.dateTextView)

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
            val selectedDate = Calendar.getInstance()

            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val monthNames = arrayOf(
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"
            )

            val formattedDate = "$selectedDay ${monthNames[selectedMonth]} $selectedYear"

            dateText.text= formattedDate

            dateText.visibility = View.VISIBLE

        }, year, month, day)

        datePickerDialog.show()
    }

    private fun hideDateText(dateText: TextView) {
        dateText.visibility = View.GONE
    }

    // Добавляем TodoItem в AddTodoViewModel
    fun saveTodo() {
        val addTodoViewModel = ViewModelProvider(this).get(AddTodoViewModel::class.java)

        // ссылки на элементы пользовательского интерфейса
        val editTextTodoText = requireView().findViewById<EditText>(R.id.editText)
        val spinnerPriority = requireView().findViewById<Spinner>(R.id.prioritySpinner)
        // Извлекаем значения
        val todoText = editTextTodoText.text.toString()
        val priority = spinnerPriority.selectedItem.toString()

        // Создаем объект с полученными данными
        val todoItem = TodoItem(
            id = UUID.randomUUID().toString(),
            text = todoText,
            priority = TodoItem.Priority.fromString(priority),
            deadline = null,
            isDone = false,
            createdDate = Date(),
            modifiedDate = null
        )


        addTodoViewModel.addTodoItem(todoItem)
    }

}