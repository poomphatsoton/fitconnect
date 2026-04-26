package com.example.train.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.train.R
import com.example.train.adapter.ExerciseAdapter
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.FragmentExercisesBinding
import com.example.train.databinding.LayoutTopBarBinding
import com.example.train.model.Exercise
import com.example.train.utils.CreateDialogUtil
import com.example.train.viewmodel.Login2Activity

class ExercisesFragment : Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    private var topBarBinding: LayoutTopBarBinding? = null

    private lateinit var dbHelper: DatabaseHelper
    private val exerciseList = mutableListOf<Exercise>()
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        topBarBinding = LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        initRecyclerView()
        loadExercises()
    }

    private fun initData() {
        dbHelper = DatabaseHelper(requireContext())
    }

    private fun initListener() {
        topBarBinding?.btnLogout?.setOnClickListener { logout() }
        binding.btnCreateExercise.setOnClickListener {
            CreateDialogUtil.showCreateExerciseDialog(requireContext()) { loadExercises() }
        }
    }

    private fun initRecyclerView() {
        binding.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        adapter = ExerciseAdapter(exerciseList)
        binding.rvExercises.adapter = adapter
        binding.rvExercises.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }

    private fun loadExercises() {
        exerciseList.clear()
        val cursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_EXERCISES, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val exercise = Exercise().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME))
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC))
                    category1 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY1))
                    category2 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY2))
                    timePerRep = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP))
                }
                exerciseList.add(exercise)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
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