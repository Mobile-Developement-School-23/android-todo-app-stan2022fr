package com.happydroid.happytodo.data.repository

import android.app.Application
import android.util.Log
import com.happydroid.happytodo.data.datasource.FakeDataSource
import com.happydroid.happytodo.data.local.LocalStorage
import com.happydroid.happytodo.data.local.TodoItemDao
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.model.TodoResult
import com.happydroid.happytodo.data.model.toTodoElementRequestNW
import com.happydroid.happytodo.data.model.toTodoItemNW
import com.happydroid.happytodo.data.network.TodoApiFactory
import com.happydroid.happytodo.data.network.model.ResponseNW
import com.happydroid.happytodo.data.network.model.RevisionHolder
import com.happydroid.happytodo.data.network.model.TodoListRequestNW
import com.happydroid.happytodo.data.network.model.TodoListResponseNW
import com.happydroid.happytodo.data.network.model.toTodoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Date

class TodoItemsRepository private constructor(application: Application){

    private val fakeDataSource : FakeDataSource= FakeDataSource()
    private val todoItemDao: TodoItemDao = LocalStorage.getDatabase(application).todoItems()
    private val apiRemote = TodoApiFactory.retrofitService
    private val _todoItemsResult = MutableStateFlow(TodoResult())
    val todoItemsResult: StateFlow<TodoResult> = _todoItemsResult
    var attempt = 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            observeLocalTodoItems()
        }

        CoroutineScope(Dispatchers.IO).launch {
            fetchFromRemoteApi()
            if (todoItemDao.getTodoItemCount() == 0 && todoItemsResult.value.data.isEmpty()) {
                loadFakesTodoItems()
                todoItemDao.addAll(todoItemsResult.value.data)
            }
            saveAllTodoItemsToRemote()

            if (!isInternetAvailable()) {
                Log.i("HappyTodo", "No internet connection")
                delay(5000L)
                val message = ErrorCode.NO_CONNECTION
                _todoItemsResult.value = todoItemsResult.value.copy(errorMessages = listOf(message))
            }
        }
    }

    suspend fun observeLocalTodoItems() {
        todoItemDao.observeAll().collect { todoItems ->
            _todoItemsResult.value = TodoResult(data = todoItems)
        }
    }


    suspend fun saveAllTodoItemsToRemote(){
        try {
            val response = apiRemote.updateAll(
                TodoListRequestNW(todoItemsResult.value.data.map { it.toTodoItemNW()  }))
            handleResponse(response as Response<ResponseNW>)
        } catch (e: Exception) {
            Log.e("TodoItemsRepository.saveAllTodoItemsToRemote()", "Exception: ${e.message}")
        }
    }

    suspend fun handleResponse(response: Response<ResponseNW>) {
        if (response.isSuccessful) {
            attempt = 0
            val responseNW: ResponseNW? = response.body()
            responseNW?.revision?.let { RevisionHolder.revision = it }

        } else {
            val errorCode = response.code()
            Log.i("HappyTodo", "Код ошибки сервера: " + errorCode)

            attempt++
            var message : ErrorCode? = null

            withContext(Dispatchers.IO) {
                delay(1000L * attempt * attempt)

                if(errorCode == 400 || errorCode == 500){
                    try {
                        apiRemote.fetchAll() // запрос, чтобы просто получить Revision
                        saveAllTodoItemsToRemote()   // в идеале, бэк должен смержить все данные
                        fetchFromRemoteApi() //получаем обновленные данные с сервера
                    }catch (e: Exception) {
                        Log.e("TodoItemsRepository", "Exception: ${e.message}")
                    }

                }else if(errorCode == 401){
                    message = ErrorCode.ERROR_401
                }else if(errorCode == 404){
                    message = ErrorCode.ERROR_404
                }else if (!isInternetAvailable()){
                    message = ErrorCode.NO_CONNECTION
                }else{

                    message = ErrorCode.UNKNOW_ERROR
                }
            }
            val oldResult = todoItemsResult.value
            val newErrorMessages = todoItemsResult.value.errorMessages.toMutableList().apply {
                message?.let{add(it)} // Добавляем новый элемент в список
            }
            _todoItemsResult.value = oldResult.copy(errorMessages = newErrorMessages)

            Log.e("TodoItemsRepository.handleResponse()", "Error:" + response.errorBody()?.string())
        }
    }

    suspend fun loadFakesTodoItems() {
         withContext(Dispatchers.IO) {
             val loadedList = fakeDataSource.loadTodoItems()
             _todoItemsResult.value = TodoResult(loadedList, listOf(ErrorCode.LOAD_FROM_HARDCODED_DATASOURCE))
        }
    }

    suspend fun fetchFromRemoteApi() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiRemote.fetchAll()
                handleResponse(response as Response<ResponseNW>)

                if(response.isSuccessful){
                    val todoListResponseNW: TodoListResponseNW? = response.body()
                    val newErrorMessages = todoItemsResult.value.errorMessages.toMutableList().apply {
                        add(ErrorCode.LOAD_FROM_REMOTE)
                    }

                    // Обновляем данные
                    _todoItemsResult.value = todoListResponseNW?.toTodoResult(newErrorMessages) ?: TodoResult(
                        emptyList(), listOf(ErrorCode.UNKNOW_ERROR)
                    )

                }else{
                    // компилятор не дает удалить этот блок else
                    // обработка в handleResponse()
                }

            } catch (e: Exception) {
                Log.e("TodoItemsRepository.fetchFromRemote()", "Exception: ${e.message}")
            }
        }

    }

    fun removeMessageFromQueue(messageId : ErrorCode){
        _todoItemsResult.update { todoItemsResult ->
            val errorMessages = todoItemsResult.errorMessages
                .filterNot { it.stringResId == messageId.stringResId}
            todoItemsResult.copy(errorMessages = errorMessages)
        }
    }

    suspend fun deleteTodoItem(idTodoItem: String) {
        val newItems = withContext(Dispatchers.Default) {
            _todoItemsResult.value.data.filter { it.id != idTodoItem }
        }
        _todoItemsResult.value = _todoItemsResult.value.copy(data = newItems)

        withContext(Dispatchers.IO) {
            try{
                todoItemDao.deleteById(idTodoItem)

                val response = apiRemote.deleteItem (idTodoItem)
                handleResponse(response as Response<ResponseNW>)
            } catch (e: Exception) {
                Log.e("TodoItemsRepository.deleteTodoItem()", "Exception: ${e.message}")
            }
        }
    }


    suspend fun getTodoItem(idTodoItem: String): TodoItem? {
        return withContext(Dispatchers.Default) {
            todoItemsResult.value.data.find { it.id == idTodoItem }
        }
    }

    suspend fun getTodoItems(): List<TodoItem> {
        return withContext(Dispatchers.Default) { todoItemsResult.value.data }
    }

    suspend fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItems = withContext(Dispatchers.Default) {
            todoItemsResult.value.data.map { todoItem ->
                if (todoItem.id == idTodoItem) todoItem.copy(isDone = isDone, modifiedDate = Date())
                else todoItem
            }
        }
        _todoItemsResult.value = TodoResult(newItems)


        try{
            withContext(Dispatchers.IO) {
                val newItem = getTodoItem(idTodoItem)
                 newItem?.let{
                    todoItemDao.editTodoItem(newItem)
                    val response = apiRemote.updateItem (idTodoItem, it.toTodoElementRequestNW())
                    handleResponse(response as Response<ResponseNW>)
                }
            }
        }catch (e: Exception) {
            Log.e("TodoItemsRepository.changeStatusTodoItem()", "Exception: ${e.message}")
        }
    }

    suspend fun addOrUpdateTodoItem(todoItem: TodoItem) {
        var isExisted = false
        var response : Response<ResponseNW>

        val newItems = withContext(Dispatchers.Default) {

            val currentList = todoItemsResult.value.data.toMutableList()
            val index = currentList.indexOfFirst { it.id == todoItem.id }
            if (index != -1) {
                // Элемент найден, перезаписываем его
                currentList[index] = todoItem
                isExisted = true
            } else {
                currentList.add(todoItem)
            }
            currentList.toList()
        }
        _todoItemsResult.value = TodoResult(newItems)
        try{
            withContext(Dispatchers.IO) {
                if (isExisted){
                    todoItemDao.editTodoItem(todoItem)
                    response = apiRemote.updateItem(todoItem.id, todoItem.toTodoElementRequestNW()) as Response<ResponseNW>
                }else{
                    todoItemDao.addTodoItem(todoItem)
                    response = apiRemote.addItem(todoItem.toTodoElementRequestNW()) as Response<ResponseNW>
                }
                handleResponse(response)
            }
        }catch (e: Exception) {
            Log.e("TodoItemsRepository.addOrUpdateTodoItem()", "Exception: ${e.message}")
        }
    }

    suspend fun updateTodoItem(newTodoItem: TodoItem)  {
        val newItems = withContext(Dispatchers.Default) {
            todoItemsResult.value.data.map { todoItem ->
                if (todoItem.id == newTodoItem.id) newTodoItem
                else todoItem
            }
        }
        _todoItemsResult.value = TodoResult(newItems)
        try{
            withContext(Dispatchers.IO) {
                val newItem = getTodoItem(newTodoItem.id)
                newItem?.let{
                    todoItemDao.editTodoItem(newItem)
                    val response = apiRemote.updateItem (newItem.id, it.toTodoElementRequestNW())
                    handleResponse(response as Response<ResponseNW>)
                }
            }
        }catch (e: Exception) {
            Log.e("TodoItemsRepository.updateTodoItem()", "Exception: ${e.message}")
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val address = InetAddress.getByName("ya.ru")
            !address.equals("")
        } catch (e: UnknownHostException) {
            Log.e("TodoItemsRepository.isInternetAvailable()", "Exception: ${e.message}")
            false
        }
    }



    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null

        fun getInstance(application: Application): TodoItemsRepository {
            return instance ?: synchronized(this) {
                instance ?: TodoItemsRepository(application).also { instance = it }
            }
        }
    }
}
