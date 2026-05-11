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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.Tag
import com.example.train.model.trainer.Exercise
import com.example.train.viewmodel.trainer.ExercisesViewModel

@Composable
fun ExercisesScreen(
    viewModel: ExercisesViewModel = viewModel()
) {
    val context = LocalContext.current
    val exercises = viewModel.exercises

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    var isSavingExercise by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadExercises()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        ExerciseHeader(
            onCreateExerciseClick = {
                editingExercise = null
                showCreateDialog = true
            }
        )

        if (exercises.isEmpty()) {
            EmptyExerciseMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = exercises,
                    key = { it.id }
                ) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        tags = viewModel.exerciseTagsMap[exercise.id].orEmpty(),
                        onEditClick = {
                            editingExercise = exercise
                            showCreateDialog = true
                        },
                        onDeleteClick = {
                            viewModel.deleteExercise(exercise.id)
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        val exerciseToEdit = editingExercise

        CreateExerciseDialog(
            initialExercise = exerciseToEdit,
            initialTags = if (exerciseToEdit != null) viewModel.exerciseTagsMap[exerciseToEdit.id].orEmpty() else emptyList(),
            availableTags = viewModel.availableTags,
            isSaving = isSavingExercise,
            onDismiss = {
                showCreateDialog = false
                editingExercise = null
                isSavingExercise = false
            },
            onConfirm = { name, description, time, tags, videoUri ->
                isSavingExercise = true

                val afterSave: (Boolean) -> Unit = { uploadSuccess ->
                    val message = when {
                        !uploadSuccess -> "Exercise saved, but video upload failed"
                        exerciseToEdit != null -> "Updated successfully"
                        else -> "Created successfully"
                    }

                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    showCreateDialog = false
                    editingExercise = null
                    isSavingExercise = false
                }

                val errorMessage = if (exerciseToEdit != null) {
                    viewModel.updateExercise(
                        id = exerciseToEdit.id,
                        name = name,
                        description = description,
                        timePerRepText = time,
                        tags = tags,
                        videoUri = videoUri,
                        onFinished = afterSave
                    )
                } else {
                    viewModel.createExercise(
                        name = name,
                        description = description,
                        timePerRepText = time,
                        tags = tags,
                        videoUri = videoUri,
                        onFinished = afterSave
                    )
                }

                if (errorMessage != null) {
                    isSavingExercise = false

                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@Composable
fun ExerciseHeader(
    onCreateExerciseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Exercise Library",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onCreateExerciseClick,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Create")
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    tags: List<Tag>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.name ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit Exercise",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete Exercise",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = exercise.description ?: "",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        if (tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    Surface(
                        color = Color(0xFFEFEFEF),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = tag.tagName,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${exercise.timePerRep.toMinuteText()}/rep",
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
    }
}

private fun Int.toMinuteText(): String {
    val minutes = this / 60.0
    val value = if (minutes % 1.0 == 0.0) {
        minutes.toInt().toString()
    } else {
        String.format("%.2f", minutes).trimEnd('0').trimEnd('.')
    }
    return "$value min"
}

@Composable
fun EmptyExerciseMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No exercises yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C757D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create your first exercise",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF)
        )
    }
}
