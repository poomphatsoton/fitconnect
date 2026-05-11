package com.example.train.view.trainer.exercise

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.train.model.Tag
import com.example.train.model.trainer.Exercise

@Composable
fun CreateExerciseDialog(
    initialExercise: Exercise? = null,
    initialTags: List<Tag> = emptyList(),
    availableTags: List<Tag> = emptyList(),
    isSaving: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        description: String,
        timePerRep: String,
        tags: List<Tag>,
        videoUri: Uri?
    ) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    var description by remember { mutableStateOf(initialExercise?.description ?: "") }
    var timePerRep by remember {
        mutableStateOf(initialExercise?.timePerRep?.toMinuteText() ?: "")
    }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var newVideoName by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf(initialTags) }

    Dialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        }
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
                text = if (initialExercise != null) "Edit Exercise" else "Create Exercise",
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
                placeholder = "Exercise name"
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
                items = availableTags,
                onTagSelected = { tag ->
                    selectedTags =
                        if (selectedTags.any { it.tagId == tag.tagId }) {
                            selectedTags.filterNot { it.tagId == tag.tagId }
                        } else {
                            selectedTags + tag
                        }
                },
                onTagRemoved = { tag ->
                    selectedTags = selectedTags.filterNot { it.tagId == tag.tagId }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Time per rep",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                DialogInputField(
                    value = timePerRep,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            timePerRep = value
                        }
                    },
                    placeholder = "Enter minutes",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "mins",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            VideoPickerField(
                fileName = newVideoName ?: initialExercise?.videoName,
                enabled = !isSaving,
                onVideoSelected = { uri ->
                    videoUri = uri
                    newVideoName = uri?.getFileName(context)
                }
            )
            if (isSaving) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Uploading video...",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                DialogBlackButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                )
                Spacer(modifier = Modifier.width(16.dp))
                DialogBlackButton(
                    text = if (initialExercise != null) "Update" else "Create",
                    onClick = {
                        onConfirm(
                            name,
                            description,
                            timePerRep,
                            selectedTags,
                            videoUri
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                )
            }
        }
    }
}

@Composable
private fun VideoPickerField(
    fileName: String?,
    enabled: Boolean,
    onVideoSelected: (Uri?) -> Unit
) {
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onVideoSelected(uri)
    }
    Text(
        text = fileName ?: "No video",
        fontSize = 13.sp,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        onClick = {
            videoPicker.launch("video/*")
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(text = "Upload Video")
    }
}

private fun Uri.getFileName(context: Context): String {
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return lastPathSegment ?: "video.mp4"
}

private fun Int.toMinuteText(): String {
    return (this / 60).coerceAtLeast(1).toString()
}
