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
import com.example.train.adapter.WorkoutAdapter
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.FragmentWorkoutsBinding
import com.example.train.databinding.LayoutTopBarBinding
import com.example.train.model.Exercise
import com.example.train.model.Workout
import com.example.train.utils.CreateDialogUtil
import com.example.train.viewmodel.Login2Activity

class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!
    private var topBarBinding: LayoutTopBarBinding? = null

    private lateinit var dbHelper: DatabaseHelper
    private val workoutList = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        topBarBinding = LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        initRecyclerView()
        loadWorkouts()
    }

    private fun initData() {
        dbHelper = DatabaseHelper(requireContext())
    }

    private fun initListener() {
        topBarBinding?.btnLogout?.setOnClickListener { logout() }
        binding.btnCreateWorkout.setOnClickListener {
            CreateDialogUtil.showCreateWorkoutDialog(requireContext()) { loadWorkouts() }
        }
    }

    private fun initRecyclerView() {
        binding.rvWorkouts.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorkoutAdapter(requireContext(), workoutList)
        binding.rvWorkouts.adapter = adapter
        binding.rvWorkouts.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }

    private fun loadWorkouts() {
        workoutList.clear()
        val workoutCursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_WORKOUTS, null, null, null, null, null, null)

        if (workoutCursor.moveToFirst()) {
            do {
                val workout = Workout().apply {
                    id = workoutCursor.getInt(workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID))
                    name = workoutCursor.getString(workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME))
                    description = workoutCursor.getString(workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DESC))
                    duration = workoutCursor.getInt(workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DURATION))
                }

                val exercises = mutableListOf<Exercise>()
                val weSel = "${DatabaseHelper.COL_WE_WORKOUT_ID} = ?"
                val weArgs = arrayOf(workout.id.toString())
                val weCursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_WORKOUT_EXERCISES, null, weSel, weArgs, null, null, null)

                if (weCursor.moveToFirst()) {
                    do {
                        val eid = weCursor.getInt(weCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WE_EXERCISE_ID))
                        val eSel = "${DatabaseHelper.COL_EXERCISE_ID} = ?"
                        val eArgs = arrayOf(eid.toString())
                        val eCursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_EXERCISES, null, eSel, eArgs, null, null, null)

                        if (eCursor.moveToFirst()) {
                            val e = Exercise().apply {
                                id = eCursor.getInt(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                                name = eCursor.getString(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME))
                                description = eCursor.getString(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC))
                                category1 = eCursor.getString(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY1))
                                category2 = eCursor.getString(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY2))
                                timePerRep = eCursor.getInt(eCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP))
                            }
                            exercises.add(e)
                        }
                        eCursor.close()
                    } while (weCursor.moveToNext())
                }
                weCursor.close()
                workout.exercises = exercises
                workoutList.add(workout)
            } while (workoutCursor.moveToNext())
        }
        workoutCursor.close()
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