package com.taskmaster.presentation.main.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taskmaster.data.database.entity.TaskEntity
import com.taskmaster.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onTaskComplete: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(task: TaskEntity) {
            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description
            binding.cbCompleted.isChecked = task.isCompleted
            
            task.dueDate?.let {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.tvDueDate.text = "Due: ${dateFormat.format(Date(it))}"
            }
            
            binding.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.isCompleted) {
                    onTaskComplete(task.copy(isCompleted = isChecked))
                }
            }
            
            binding.root.setOnClickListener {
                onTaskClick(task)
            }
            
            binding.btnDelete.setOnClickListener {
                onTaskDelete(task)
            }
        }
    }
    
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
