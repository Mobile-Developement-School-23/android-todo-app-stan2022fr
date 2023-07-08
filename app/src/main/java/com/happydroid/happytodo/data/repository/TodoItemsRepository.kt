package com.happydroid.happytodo.data.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.happydroid.happytodo.data.datasource.FakeDataSource
import com.happydroid.happytodo.data.local.LocalStorage
import com.happydroid.happytodo.data.local.TodoItemDao
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.Mapper
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.model.TodoItemsResult
import com.happydroid.happytodo.data.model.toTodoElementRequestNW
import com.happydroid.happytodo.data.model.toTodoItemNetwork
import com.happydroid.happytodo.data.network.TodoApiFactory
import com.happydroid.happytodo.data.network.model.ResponseNetwork
import com.happydroid.happytodo.data.network.model.RevisionHolder
import com.happydroid.happytodo.data.network.model.TodoListRequestNetwork
import com.happydroid.happytodo.data.network.model.TodoListResponseNetwork
import com.happydroid.happytodo.data.network.model.toTodoItemsResult
import com.happydroid.happytodo.data.workers.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.UnknownHostException
import java.util.Date


private const val DELEAY_NOTIFICATION = 5000L

/**
 * This class is responsible for managing the persistence and retrieval of todo items.
 */
@Suppress("UNCHECKED_CAST")
class TodoItemsRepository private constructor(application: Application) {

    private val fakeDataSource: FakeDataSource = FakeDataSource()
    private val todoItemDao: TodoItemDao = LocalStorage.getDatabase(application).todoItems()
    private val apiRemote = TodoApiFactory.retrofitService
    private val workManager = WorkManager.getInstance(application)
    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _todoItemsResult = MutableStateFlow(TodoItemsResult())
    val todoItemsResult: StateFlow<TodoItemsResult> = _todoItemsResult
    private var attempt = 0

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

            if (!isOnline()) {
                Log.i("HappyTodo", "No internet connection")
                delay(DELEAY_NOTIFICATION)
                val message = ErrorCode.NO_CONNECTION
                _todoItemsResult.value = todoItemsResult.value.copy(errorMessages = listOf(message))
            }
        }
    }

    private suspend fun observeLocalTodoItems() {
        todoItemDao.observeAll().collect { todoItems ->
            _todoItemsResult.value = TodoItemsResult(data = todoItems)
        }
    }

    private suspend fun syncOnConnection() {
        val message = ErrorCode.NO_CONNECTION
        val oldResult = todoItemsResult.value
        val newErrorMessages = todoItemsResult.value.errorMessages.toMutableList().apply {
            add(message)
        }

        _todoItemsResult.value = oldResult.copy(errorMessages = newErrorMessages)


        val myWorkRequest = OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueueUniqueWork("HappySyncWorker", ExistingWorkPolicy.REPLACE, myWorkRequest)
    }

    suspend fun saveAllTodoItemsToRemote() {
        try {
            if (isOnline()){
                val response = apiRemote.updateAll(
                    TodoListRequestNetwork(todoItemsResult.value.data.map { it.toTodoItemNetwork() })
                )
                handleResponse(response as Response<ResponseNetwork>)
            }else{
                syncOnConnection()
            }
        }catch (e: UnknownHostException){
            syncOnConnection()
        } catch (e: Exception) {
            Log.e("TodoItemsRepository.saveAllTodoItemsToRemote()", "Exception: ${e.message}")
        }
    }

    private suspend fun handleResponse(response: Response<ResponseNetwork>) {
        if (response.isSuccessful) {
            attempt = 0
            val responseNetwork: ResponseNetwork? = response.body()
            responseNetwork?.revision?.let { RevisionHolder.revision = it }

        } else {
            val errorCode = Mapper().mapToErrorCode(response.code())
            Log.i("HappyTodo", "Код ошибки сервера: " + errorCode)

            attempt++
            var message: ErrorCode? = null

            withContext(Dispatchers.IO) {
                delay(1000L * attempt * attempt)

                if (errorCode == ErrorCode.ERROR_401) {
                    try {
                        apiRemote.fetchAll() // запрос, чтобы просто получить Revision
                        saveAllTodoItemsToRemote()   // в идеале, бэк должен смержить все данные
                        fetchFromRemoteApi() //получаем обновленные данные с сервера
                    } catch (e: Exception) {
                        Log.e("TodoItemsRepository", "Exception: ${e.message}")
                    }

                } else if (errorCode == ErrorCode.ERROR_500) {
                    message = ErrorCode.ERROR_500
                } else if (errorCode == ErrorCode.ERROR_404) {
                    message = ErrorCode.ERROR_404
                } else if (!isOnline()) {
                    message = ErrorCode.NO_CONNECTION
                    syncOnConnection()
                } else {
                    message = ErrorCode.UNKNOW_ERROR
                }
            }
            val oldResult = todoItemsResult.value
            val newErrorMessages = todoItemsResult.value.errorMessages.toMutableList().apply {
                message?.let { add(it) } // Добавляем новый элемент в список
            }
            _todoItemsResult.value = oldResult.copy(errorMessages = newErrorMessages)

            Log.e("TodoItemsRepository.handleResponse()", "Error:" + response.errorBody()?.string())
        }
    }

    private suspend fun loadFakesTodoItems() {
        withContext(Dispatchers.IO) {
            val loadedList = fakeDataSource.loadTodoItems()
            _todoItemsResult.value =
                TodoItemsResult(loadedList, listOf(ErrorCode.LOAD_FROM_HARDCODED_DATASOURCE))
        }
    }

    suspend fun fetchFromRemoteApi() {
        withContext(Dispatchers.IO) {
            try {
                if(isOnline()){

                    val response = apiRemote.fetchAll()
                    handleResponse(response as Response<ResponseNetwork>)

                    if (response.isSuccessful) {
                        val todoListResponseNW: TodoListResponseNetwork? = response.body()
                        val newErrorMessages =
                            todoItemsResult.value.errorMessages.toMutableList().apply {
                                add(ErrorCode.LOAD_FROM_REMOTE)
                            }

                        // Обновляем данные
                        _todoItemsResult.value =
                            todoListResponseNW?.toTodoItemsResult(newErrorMessages) ?: TodoItemsResult(
                                emptyList(), listOf(ErrorCode.UNKNOW_ERROR)
                            )
                        todoItemDao.addAll(todoItemsResult.value.data)

                    } else {
                        // компилятор не дает удалить этот блок else
                        // обработка в handleResponse()
                    }
                }else{
                    syncOnConnection()
                }

            }catch (e: UnknownHostException){
                syncOnConnection()
            } catch (e: Exception) {
                Log.e("TodoItemsRepository.fetchFromRemote()", "Exception: ${e.message}")
            }
        }

    }

    fun removeMessageFromQueue(messageId: ErrorCode) {
        _todoItemsResult.update { todoItemsResult ->
            val errorMessages = todoItemsResult.errorMessages
                .filterNot { it.stringResId == messageId.stringResId }
            todoItemsResult.copy(errorMessages = errorMessages)
        }
    }

    suspend fun deleteTodoItem(idTodoItem: String) {
        val newItems = withContext(Dispatchers.Default) {
            _todoItemsResult.value.data.filter { it.id != idTodoItem }
        }
        _todoItemsResult.value = _todoItemsResult.value.copy(data = newItems)

        withContext(Dispatchers.IO) {
            try {
                todoItemDao.deleteById(idTodoItem)
                if (isOnline()) {
                    val response = apiRemote.deleteItem(idTodoItem)
                    handleResponse(response as Response<ResponseNetwork>)
                }else{
                    syncOnConnection()
                }
            }catch (e: UnknownHostException){
                syncOnConnection()
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

    suspend fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItems = withContext(Dispatchers.Default) {
            todoItemsResult.value.data.map { todoItem ->
                if (todoItem.id == idTodoItem) todoItem.copy(isDone = isDone, modifiedDate = Date())
                else todoItem
            }
        }
        _todoItemsResult.value = TodoItemsResult(newItems)

        try {
            withContext(Dispatchers.IO) {
                val newItem = getTodoItem(idTodoItem)
                newItem?.let {
                    todoItemDao.updateTodoItem(newItem)
                    if (isOnline()) {
                        val response = apiRemote.updateItem(idTodoItem, it.toTodoElementRequestNW())
                        handleResponse(response as Response<ResponseNetwork>)
                    }else{
                        syncOnConnection()
                    }
                }
            }

        }catch (e: UnknownHostException){
                syncOnConnection()
        } catch (e: Exception) {
            Log.e("TodoItemsRepository.changeStatusTodoItem()", "Exception: ${e.message}")
        }
    }

    suspend fun addTodoItem(todoItem: TodoItem) {
        try {
            withContext(Dispatchers.IO) {
                todoItemDao.addTodoItem(todoItem)
                if (isOnline()) {
                    val response =
                        apiRemote.addItem(todoItem.toTodoElementRequestNW()) as Response<ResponseNetwork>
                    handleResponse(response)
                }else{
                    syncOnConnection()
                }
            }
        }
        catch (e: UnknownHostException){
            syncOnConnection()
        }
        catch (e: Exception) {
            Log.e("TodoItemsRepository.addOrUpdateTodoItem()", "Exception: ${e.message}")
        }
    }

    suspend fun updateTodoItem(todoItem: TodoItem) {
        try {
            withContext(Dispatchers.IO) {
                todoItemDao.updateTodoItem(todoItem)
                if (isOnline()) {
                    val response =
                        apiRemote.updateItem(todoItem.id, todoItem.toTodoElementRequestNW()) as Response<ResponseNetwork>
                    handleResponse(response)
                }else{
                    syncOnConnection()
                }
            }
        }
        catch (e: UnknownHostException){
            syncOnConnection()
        }
        catch (e: Exception) {
            Log.e("TodoItemsRepository.addOrUpdateTodoItem()", "Exception: ${e.message}")
        }
    }


    private fun isOnline(): Boolean {
        val netInfo = connectivityManager.activeNetworkInfo
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
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
