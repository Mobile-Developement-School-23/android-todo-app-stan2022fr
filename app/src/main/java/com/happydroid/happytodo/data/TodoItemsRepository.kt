package com.happydroid.happytodo.data
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoItemsRepository {
    private val todoItems: MutableList<TodoItem> = mutableListOf()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    init {
        // Заглушка для списка дел
        todoItems.addAll(getValuesItems())
    }

    fun getTodoItems(): List<TodoItem> {
        return todoItems.toList()
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }

    private fun getValuesItems(): List<TodoItem> {
        val items = mutableListOf<TodoItem>()


        items.add(TodoItem("1", "Купить продукты", TodoItem.Priority.NORMAL, null, false, dateFormat.parse("10/06/2023")!!, null))
        items.add(TodoItem("2", "Записаться на занятия йогой в спортзале", TodoItem.Priority.LOW, null, true, dateFormat.parse("10/06/2023")!!, null))
        items.add(TodoItem("3", "Составить план действий на ближайшую неделю", TodoItem.Priority.HIGH, null, false, dateFormat.parse("11/06/2023")!!, null))
        items.add(TodoItem("4", "Подготовить презентацию для встречи с клиентом", TodoItem.Priority.HIGH, dateFormat.parse("10/07/2023")!!, false, dateFormat.parse("11/06/2023")!!, null))
        items.add(TodoItem("5", "Позвонить другу и поздравить с днем рождения", TodoItem.Priority.NORMAL, null, true, dateFormat.parse("11/06/2023")!!, dateFormat.parse("12/06/2023")!!))
        items.add(TodoItem("6", "Организовать встречу с коллегами по проекту", TodoItem.Priority.NORMAL, null, false, dateFormat.parse("11/06/2023")!!, null))
        items.add(TodoItem("7", "Отправить важное письмо по электронной почте", TodoItem.Priority.HIGH, dateFormat.parse("25/06/2023")!!, false, dateFormat.parse("11/06/2023")!!, null))
        items.add(TodoItem("8", "Сделать список задач", TodoItem.Priority.LOW, null, false, dateFormat.parse("11/06/2023")!!, null))
        items.add(TodoItem("9", "Подготовить документы для собеседования", TodoItem.Priority.NORMAL, null, false, dateFormat.parse("12/06/2023")!!, null))
        items.add(TodoItem("10", "Заказать билеты на концерт", TodoItem.Priority.LOW, dateFormat.parse("13/06/2023")!!, true, dateFormat.parse("12/06/2023")!!, dateFormat.parse("12/06/2023")!!))
        items.add(TodoItem("11", "Забрать паспорт", TodoItem.Priority.HIGH, null, false, dateFormat.parse("12/06/2023")!!, null))
        items.add(TodoItem("12", "Найти рецепт нового блюда для ужина", TodoItem.Priority.NORMAL, null, false, dateFormat.parse("12/06/2023")!!, null))
        items.add(TodoItem("13", "Проверить расписание автобусов до города", TodoItem.Priority.NORMAL, null, false, dateFormat.parse("13/06/2023")!!, null))
        items.add(TodoItem("14", "Подготовиться к собранию совета директоров", TodoItem.Priority.HIGH, dateFormat.parse("20/06/2023")!!, false, dateFormat.parse("13/06/2023")!!, null))
        items.add(TodoItem("15", "Закончить чтение новой книги", TodoItem.Priority.LOW, null, false, dateFormat.parse("13/06/2023")!!, null))

        return items
    }
}
