package com.example.train.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.train.model.trainer.ExerciseSelectUiItem
import com.example.train.viewmodel.trainer.WorkoutsViewModel

@Composable
fun CreateWorkoutDialogHost(
    viewModel: WorkoutsViewModel,
    onCreated: () -> Unit = {},
    content: @Composable (
        () -> Unit,
        (Int) -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var editingWorkoutId by remember { mutableStateOf<Int?>(null) }

    content(
        {
            editingWorkoutId = null
            viewModel.loadAvailableExercises()
            showDialog = true
        },
        { id ->
            editingWorkoutId = id
            viewModel.loadWorkoutForEdit(id)
            showDialog = true
        }
    )

    if (showDialog) {
        val initialName = if (editingWorkoutId != null) {
            viewModel.workouts.find { it.workout.id == editingWorkoutId }?.workout?.name ?: ""
        } else ""

        val initialDesc = if (editingWorkoutId != null) {
            viewModel.workouts.find { it.workout.id == editingWorkoutId }?.workout?.description ?: ""
        } else ""

        CreateWorkoutDialog(
            exercises = viewModel.availableExercises,
            initialName = initialName,
            initialDescription = initialDesc,
            isEdit = editingWorkoutId != null,
            onDismiss = {
                showDialog = false
            },
            onExerciseSelectedChange = { id, selected ->
                viewModel.updateExerciseSelected(id, selected)
            },
            onExerciseRepsChange = { id, reps ->
                viewModel.updateExerciseReps(id, reps)
            },
            onConfirm = { name, description ->
                val error = if (editingWorkoutId != null) {
                    viewModel.updateWorkout(editingWorkoutId!!, name, description)
                } else {
                    viewModel.createWorkout(
                        name = name,
                        description = description
                    )
                }

                if (error == null) {
                    Toast.makeText(
                        context,
                        if (editingWorkoutId != null) "Workout updated successfully" else "Workout created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    showDialog = false
                    viewModel.loadWorkouts()
                    onCreated()
                } else {
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@Composable
fun CreateWorkoutDialog(
    exercises: List<ExerciseSelectUiItem>,
    initialName: String = "",
    initialDescription: String = "",
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onExerciseSelectedChange: (id: Long, selected: Boolean) -> Unit,
    onExerciseRepsChange: (id: Long, reps: String) -> Unit,
    onConfirm: (
        name: String,
        description: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(32.dp)
        ) {
            Text(
                text = if (isEdit) "Edit Workout" else "Create Workout",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                textAlign = TextAlign.Center
            )

            DialogInputField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Workout Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DialogInputField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Description"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                exercises.forEach { exercise ->
                    ExerciseSelectRow(
                        exercise = exercise,
                        onSelectedChange = { selected ->
                            onExerciseSelectedChange(exercise.id, selected)
                        },
                        onRepsChange = { reps ->
                            onExerciseRepsChange(exercise.id, reps)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                DialogBlackButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                DialogBlackButton(
                    text = if (isEdit) "Update" else "Create",
                    onClick = {
                        onConfirm(name, description)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ExerciseSelectRow(
    exercise: ExerciseSelectUiItem,
    onSelectedChange: (Boolean) -> Unit,
    onRepsChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = exercise.isSelected,
            onCheckedChange = onSelectedChange
        )

        Text(
            text = exercise.name,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        OutlinedTextField(
            value = exercise.reps,
            onValueChange = onRepsChange,
            placeholder = {
                Text(
                    text = "Reps",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .width(80.dp)
                .height(40.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F7F7),
                unfocusedContainerColor = Color(0xFFF7F7F7),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}
