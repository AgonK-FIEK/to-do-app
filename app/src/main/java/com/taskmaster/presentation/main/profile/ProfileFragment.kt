package com.taskmaster.presentation.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.taskmaster.data.database.dao.UserDao
import com.taskmaster.databinding.FragmentProfileBinding
import com.taskmaster.presentation.auth.login.LoginActivity
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var userDao: UserDao
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.text = sessionManager.getUserEmail()

        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            val user = userDao.getUserById(userId)
            user?.let {
                binding.switch2FA.isChecked = it.twoFactorEnabled
            }
        }

        binding.switch2FA.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val userId = sessionManager.getUserId()
                userDao.updateTwoFactorEnabled(userId, isChecked)
                val message = if (isChecked) {
                    "2FA enabled. You'll need to verify your email on next login."
                } else {
                    "2FA disabled"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
