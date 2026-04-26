package com.example.train.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.train.R
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.FragmentCalendarBinding
import com.example.train.databinding.LayoutTopBarBinding
import com.example.train.viewmodel.Login2Activity
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var topBarBinding: LayoutTopBarBinding? = null

    private lateinit var dbHelper: DatabaseHelper
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        topBarBinding = LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        initCalendar()
    }

    private fun initData() {
        dbHelper = DatabaseHelper(requireContext())
        val today = Calendar.getInstance()
        selectedDate = dateFormat.format(today.time)
    }

    private fun initListener() {
        topBarBinding?.btnLogout?.setOnClickListener { logout() }
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            selectedDate = dateFormat.format(cal.time)
            updateDisplay()
        }
    }

    private fun initCalendar() {
        updateDisplay()
    }

    private fun updateDisplay() {
        try {
            val displayFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            val cal = Calendar.getInstance()
            cal.time = dateFormat.parse(selectedDate!!)!!
            binding.tvSelectedDate.text = displayFormat.format(cal.time)

            val sel = "${DatabaseHelper.COL_SCHEDULE_DATE} = ?"
            val args = arrayOf(selectedDate)
            val cursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_WORKOUT_SCHEDULES, null, sel, args, null, null, null)

            binding.tvStatus.text = if (cursor.count > 0) {
                "Scheduled workouts found for this day"
            } else {
                "No workouts scheduled for this day"
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvSelectedDate.text = "Date Error"
        }
    }

    private fun logout() {
        val prefs = requireContext().getSharedPreferences("FitConnect", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        val intent = Intent(requireContext(), Login2Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
        Toast.makeText(requireContext(), "Logout Success!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        topBarBinding = null
    }
}