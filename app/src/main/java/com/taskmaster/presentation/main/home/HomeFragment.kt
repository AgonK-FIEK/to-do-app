package com.taskmaster.presentation.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.taskmaster.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        observeViewModel()
        viewModel.loadDashboard()
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                when (state) {
                    is DashboardState.Success -> {
                        binding.tvWelcome.text = "Welcome, ${state.userName}!"
                        binding.tvTotalTasks.text = "Total Tasks: ${state.totalTasks}"
                        binding.tvCompletedTasks.text = "Completed: ${state.completedTasks}"
                        binding.tvPendingTasks.text = "Pending: ${state.pendingTasks}"
                    }
                    else -> {}
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
