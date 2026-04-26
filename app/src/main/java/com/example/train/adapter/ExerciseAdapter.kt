package com.example.train.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.train.databinding.ItemExerciseBinding
import com.example.train.model.Exercise

class ExerciseAdapter(
    private val exerciseList: List<Exercise>
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.binding.tvExerciseName.text = exercise.name
        holder.binding.tvExerciseDesc.text = exercise.description
        holder.binding.tvCategory1.text = exercise.category1
        holder.binding.tvCategory2.text = exercise.category2
        holder.binding.tvTimePerRep.text = "${exercise.timePerRep}s/rep"
    }

    override fun getItemCount() = exerciseList.size

    class ExerciseViewHolder(val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root)
}