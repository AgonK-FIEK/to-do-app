package com.taskmaster.presentation.main.tasks

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.taskmaster.databinding.DialogAddTaskBinding

class AddTaskDialog(
    private val onTaskAdded: (String, String) -> Unit
) : DialogFragment() {
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddTaskBinding.inflate(layoutInflater)
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Task")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                val title = binding.etTitle.text.toString()
                val description = binding.etDescription.text.toString()
                
                if (title.isNotEmpty()) {
                    onTaskAdded(title, description)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
