//package com.example.train.ui
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.example.train.R
//import com.example.train.database.DatabaseHelper
//import com.example.train.databinding.FragmentHomeBinding
//import com.example.train.databinding.LayoutTopBarBinding
//import com.example.train.utils.CreateDialogUtil
//import com.example.train.viewmodel.Login2Activity
//
//class HomeFragment : Fragment() {
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//    private var topBarBinding: LayoutTopBarBinding? = null
//
//    private lateinit var dbHelper: DatabaseHelper
//    private var userId = -1
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        topBarBinding = LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initData()
//        initListener()
//        loadTrainerProfile()
//        loadOverviewData()
//    }
//
//    private fun initData() {
//        dbHelper = DatabaseHelper(requireContext())
//        val prefs = requireContext().getSharedPreferences("FitConnect", Context.MODE_PRIVATE)
//        userId = prefs.getInt("userId", -1)
//    }
//
//    private fun initListener() {
//        topBarBinding?.btnLogout?.setOnClickListener { logout() }
//        binding.btnCreateExercise.setOnClickListener {
//            CreateDialogUtil.showCreateExerciseDialog(requireContext()) { loadOverviewData() }
//        }
//        binding.btnCreateWorkout.setOnClickListener {
//            CreateDialogUtil.showCreateWorkoutDialog(requireContext()) { loadOverviewData() }
//        }
//        binding.btnPendingRequests.setOnClickListener {
//            Toast.makeText(requireContext(), "Pending Requests Clicked!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        loadOverviewData()
//    }
//
//    private fun loadTrainerProfile() {
//        val projection = arrayOf(DatabaseHelper.COL_USER_NAME, DatabaseHelper.COL_USER_BIO)
//        val selection = "${DatabaseHelper.COL_USER_ID} = ?"
//        val args = arrayOf(userId.toString())
//
//        val cursor = dbHelper.readableDatabase.query(
//            DatabaseHelper.TABLE_USERS, projection, selection, args, null, null, null
//        )
//
//        if (cursor.moveToFirst()) {
//            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME))
//            val bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO))
//            binding.tvTrainerName.text = name
//            binding.tvTrainerBio.text = bio
//        }
//        cursor.close()
//    }
//
//    private fun loadOverviewData() {
//        val active = dbHelper.getActiveTraineesCount(userId)
//        val exercises = dbHelper.getExercisesCount()
//        val workouts = dbHelper.getWorkoutsCount()
//        val pending = dbHelper.getPendingRequestsCount(userId)
//
//        binding.tvActiveTrainees.text = active.toString()
//        binding.tvExercises.text = exercises.toString()
//        binding.tvWorkouts.text = workouts.toString()
//        binding.btnPendingRequests.text = "$pending Pending Request${if (pending > 1) "s" else ""}"
//    }
//
//    private fun logout() {
//        val prefs = requireContext().getSharedPreferences("FitConnect", Context.MODE_PRIVATE)
//        prefs.edit().clear().apply()
//        val intent = Intent(requireContext(), Login2Activity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        requireActivity().finish()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        topBarBinding = null
//    }
//}