package com.example.train.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.train.view.trainer.Exercise.TagDropdown

@Composable
fun CreateExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        description: String,
        category1: String,
        category2: String,
        timePerRep: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category1 by remember { mutableStateOf("") }
    var category2 by remember { mutableStateOf("") }
    var timePerRep by remember { mutableStateOf("") }

    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    val sampleTags = listOf("s1", "s2", "s3")

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
                text = "Create Exercise",
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
                placeholder = "Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DialogInputField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Description"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TagDropdown(
                selectedTags = selectedTags,
                items = sampleTags,
                onTagSelected = { tag ->
                    selectedTags =
                        if (selectedTags.contains(tag)) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                },
                onTagRemoved = { tag ->
                    selectedTags = selectedTags - tag
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DialogInputField(
                value = timePerRep,
                onValueChange = { timePerRep = it },
                placeholder = "Time per rep"
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Create",
                    onClick = {
                        onConfirm(
                            name,
                            description,
                            category1,
                            category2,
                            timePerRep
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
