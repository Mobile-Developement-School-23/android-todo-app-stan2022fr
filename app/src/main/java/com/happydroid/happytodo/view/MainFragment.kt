package com.happydroid.happytodo.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happydroid.happytodo.R
import com.happydroid.happytodo.data.TodoItemsRepository
import com.happydroid.happytodo.viewModel.MainViewModel

class MainFragment : Fragment() {

    private lateinit var todolistRecyclerView: RecyclerView


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        val todolistRecyclerView: RecyclerView = rootView.findViewById(R.id.todolist)
        viewModel.setRecyclerView(todolistRecyclerView) // Передаем RecyclerView в нашу ViewModel
        return rootView
    }

}