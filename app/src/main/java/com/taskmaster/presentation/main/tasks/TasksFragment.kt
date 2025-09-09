package com.taskmaster.presentation.main.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskmaster.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {
    
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TasksViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }
    
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { },
            onTaskComplete = { task ->
                viewModel.updateTaskCompletion(task)
            },
            onTaskDelete = { task ->
                viewModel.deleteTask(task)
            }
        )
        
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tasks.collect { tasks ->
                taskAdapter.submitList(tasks)
            }
        }
    }
    
    private fun showAddTaskDialog() {
        AddTaskDialog { title, description ->
            viewModel.addTask(title, description)
        }.show(childFragmentManager, "AddTaskDialog")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
