package com.example.train.ui

import com.example.train.ui.components.CreateExerciseDialog
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.Exercise
import com.example.train.viewmodel.ExercisesViewModel

@Composable
fun ExercisesScreen(
    viewModel: ExercisesViewModel = viewModel()
) {
    val context = LocalContext.current
    val exercises = viewModel.exercises

    var showCreateDialog by remember { mutableStateOf(false) }

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
                    ExerciseCard(exercise = exercise)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateExerciseDialog(
            onDismiss = {
                showCreateDialog = false
            },
            onConfirm = { name, description, category1, category2, time ->
                val errorMessage = viewModel.createExercise(
                    name = name,
                    description = description,
                    category1 = category1,
                    category2 = category2,
                    timePerRepText = time
                )

                if (errorMessage == null) {
                    Toast.makeText(
                        context,
                        "Created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    showCreateDialog = false
                } else {
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
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Create Exercise",
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Create")
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = exercise.name ?: "",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = exercise.description ?: "",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            CategoryTag(text = exercise.category1 ?: "")

            Spacer(modifier = Modifier.width(4.dp))

            CategoryTag(text = exercise.category2 ?: "")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${exercise.timePerRep}s/rep",
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun CategoryTag(
    text: String
) {
    Text(
        text = text,
        fontSize = 12.sp,
        modifier = Modifier
            .background(Color(0xFFE0E0E0))
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    )
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
