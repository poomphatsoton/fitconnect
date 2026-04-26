package com.example.train.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.train.R
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.FragmentTraineesBinding
import com.example.train.databinding.LayoutTopBarBinding
import com.example.train.viewmodel.Login2Activity

class TraineesFragment : Fragment() {
    private var _binding: FragmentTraineesBinding? = null
    private val binding get() = _binding!!
    private var topBarBinding: LayoutTopBarBinding? = null

    private lateinit var dbHelper: DatabaseHelper
    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTraineesBinding.inflate(inflater, container, false)
        topBarBinding = LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        loadTabData()
    }

    private fun initData() {
        dbHelper = DatabaseHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("FitConnect", Context.MODE_PRIVATE)
        userId = prefs.getInt("userId", -1)
    }

    private fun initListener() {
        topBarBinding?.btnLogout?.setOnClickListener { logout() }
    }

    private fun loadTabData() {
        val active = dbHelper.getActiveTraineesCount(userId)
        val pending = dbHelper.getPendingRequestsCount(userId)

        binding.tvActiveCount.text = "Active ($active)"
        binding.tvRequestsCount.text = "Requests ($pending)"
        binding.tvNoActiveTrainees.visibility = if (active == 0) View.VISIBLE else View.GONE
    }

    private fun logout() {
        val prefs = requireContext().getSharedPreferences("FitConnect", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        val intent = Intent(requireContext(), Login2Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        topBarBinding = null
    }
}