package com.happydroid.happytodo.presentation.additem

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * This class represents a fragment that allows users to edit or add a new todo item.
 */
class AddTodoFragment : Fragment() {

    private val addTodoViewModel: AddTodoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = TodoItemsRepository.getInstance(requireActivity().application)
                if (modelClass.isAssignableFrom(AddTodoViewModel::class.java)) {
                    return AddTodoViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private var todoItem: TodoItem? = null
    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_todo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateSwitch: SwitchCompat = view.findViewById(R.id.dateSwitch)
        val dateText: TextView = view.findViewById(R.id.dateTextView)
        val deleteButton: TextView = view.findViewById(R.id.deleteButton)
        val editText: EditText = view.findViewById(R.id.editText)

        val bundle = arguments
        val idTodoItem = bundle?.getString("idTodoItem")
        todoItem = idTodoItem?.let { runBlocking { addTodoViewModel.getTodoItem(it) } }

        if (todoItem != null) {
            setDataForTodoItem(editText, deleteButton, view, dateSwitch, dateText)
        } else {
            deleteButton.isEnabled = false
            deleteButton.alpha = ITEM_DISABLE
        }

        addOnSaveAction(view, editText)

        addOnCloseAction(view)

        addOnDeleteAction(deleteButton)


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

    private fun setDataForTodoItem(
        editText: EditText,
        deleteButton: TextView,
        view: View,
        dateSwitch: SwitchCompat,
        dateText: TextView
    ) {
        editText.text = Editable.Factory.getInstance().newEditable(todoItem!!.text)
        deleteButton.isEnabled = true

        val prioritySpinner: Spinner = view.findViewById(R.id.prioritySpinner)
        prioritySpinner.setSelection(todoItem!!.priority.ordinal)

        if (todoItem!!.deadline != null) {
            dateSwitch.isChecked = true

            dateText.text = dateFormatter.format(todoItem!!.deadline!!)
            dateText.visibility = View.VISIBLE
        }
    }

    private fun addOnDeleteAction(deleteButton: TextView) {
        deleteButton.setOnClickListener {
            todoItem?.id?.let { id ->
                addTodoViewModel.deleteTodoItem(id)
            }
            hideKeyboard()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addOnCloseAction(view: View) {
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            hideKeyboard()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addOnSaveAction(view: View, editText: EditText) {
        val saveButton: TextView = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            if (editText.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.alert_vide_text),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                saveTodo(todoItem?.id)
                hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun showCalendar(dateText: TextView) {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(requireContext(), { view, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                dateText.text = dateFormatter.format(calendar.time)
                dateText.visibility = View.VISIBLE

            }, year, month, day)

        datePickerDialog.show()
    }

    private fun hideDateText(dateText: TextView) {
        dateText.visibility = View.GONE
    }

    /**
     * Сохраняем новую задачу или обновляем старую
     */
    private fun saveTodo(oldId: String?) {
        val editTextTodoText = requireView().findViewById<EditText>(R.id.editText)
        val spinnerPriority = requireView().findViewById<Spinner>(R.id.prioritySpinner)
        val selectedDate = requireView().findViewById<TextView>(R.id.dateTextView)
        val todoText = editTextTodoText.text.toString()
        val priorityId = spinnerPriority.selectedItemId
        var deadline: Date? = null
        if (selectedDate.isVisible && selectedDate.text != getString(R.string.text_choose_date)) {
            try {
                deadline = dateFormatter.parse(selectedDate.text.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        addTodoViewModel.addOrUpdateTodoItem(
            oldId,
            todoText,
            TodoItem.Priority.values()[priorityId.toInt()],
            deadline
        )
    }

    /**
     * На некоторых утройствах приходится скрывать клавиатуру вручную
     */
    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val ITEM_DISABLE = 0.5f
    }

}
