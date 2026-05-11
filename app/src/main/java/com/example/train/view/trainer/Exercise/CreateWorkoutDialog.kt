package com.example.train.view.trainer.exercise

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
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
                    val message = if (editingWorkoutId != null) {
                        "Workout updated successfully"
                    } else {
                        "Workout created successfully"
                    }

                    Toast.makeText(
                        context,
                        message,
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
    } else {
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
    var selectedExerciseIds by remember(isEdit, initialName, initialDescription) {
        mutableStateOf(exercises.filter { it.isSelected }.map { it.id })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
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
            modifier = Modifier.fillMaxWidth()
        ) {
            val selectedExercises = selectedExerciseIds.mapNotNull { selectedId ->
                exercises.firstOrNull { it.id == selectedId && it.isSelected }
            }
            Button(
                onClick = {
                    exercises.firstOrNull { !it.isSelected && it.id !in selectedExerciseIds }?.let { exercise ->
                        selectedExerciseIds = selectedExerciseIds + exercise.id
                        onExerciseSelectedChange(exercise.id, true)
                    }
                },
                enabled = exercises.any { !it.isSelected && it.id !in selectedExerciseIds },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "+ Add exercise",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            selectedExercises.forEach { exercise ->
                ExerciseSelectRow(
                    exercise = exercise,
                    exerciseOptions = exercises,
                    onExerciseChange = { selectedExercise ->
                        if (selectedExercise.id != exercise.id) {
                            selectedExerciseIds = selectedExerciseIds.map { selectedId ->
                                if (selectedId == exercise.id) selectedExercise.id else selectedId
                            }
                            onExerciseSelectedChange(exercise.id, false)
                            onExerciseRepsChange(exercise.id, "")
                            onExerciseSelectedChange(selectedExercise.id, true)
                            onExerciseRepsChange(selectedExercise.id, exercise.reps)
                        }
                    },
                    onRepsChange = { reps ->
                        onExerciseRepsChange(exercise.id, reps)
                    },
                    onRemoveClick = {
                        selectedExerciseIds = selectedExerciseIds.filter { it != exercise.id }
                        onExerciseSelectedChange(exercise.id, false)
                        onExerciseRepsChange(exercise.id, "")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectRow(
    exercise: ExerciseSelectUiItem,
    exerciseOptions: List<ExerciseSelectUiItem>,
    onExerciseChange: (ExerciseSelectUiItem) -> Unit,
    onRepsChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val availableOptions = exerciseOptions.filter { !it.isSelected || it.id == exercise.id }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = exercise.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF7F7F7),
                    unfocusedContainerColor = Color(0xFFF7F7F7),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                availableOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.name) },
                        onClick = {
                            onExerciseChange(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = Color.Black)
                    )
                }
            }
        }
        OutlinedTextField(
            value = exercise.reps,
            onValueChange = { value ->
                onRepsChange(value.filter { it.isDigit() })
            },
            placeholder = {
                Text(
                    text = "Reps",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .width(90.dp)
                .height(56.dp),
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
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .width(48.dp)
                .height(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Remove exercise",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
