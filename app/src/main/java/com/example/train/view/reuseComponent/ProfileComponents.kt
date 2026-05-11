package com.example.train.view.reuseComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
import com.example.train.model.Tag
import com.example.train.view.trainer.exercise.TagDropdown

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileCard(
    name: String,
    bio: String,
    tags: List<Tag>,
    isTrainer: Boolean,
    activeTrainees: Int = 0,
    maxTrainees: Int = 0,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = if (isTrainer) "Trainer avatar" else "Trainee avatar",
                    modifier = Modifier.size(84.dp)
                )

                Spacer(modifier = Modifier.width(24.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Name",
                            fontSize = 12.sp,
                            color = Color(0xFF6C757D)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Bio",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = bio,
                    fontSize = 15.sp,
                    color = Color(0xFF495057),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tags",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                shape = RoundedCornerShape(8.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(12.dp),
                ) {
                    val displayTags = tags.ifEmpty {
                        listOf(Tag(0, "No tags"))
                    }
                    displayTags.forEach { tag ->
                        SkillChip(tag.tagName)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isTrainer) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Trainee Capacity",
                            fontSize = 12.sp,
                            color = Color(0xFF6C757D)
                        )

                        Text(
                            text = "$activeTrainees / $maxTrainees trainees",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Edit Profile", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun EditUserProfileScreen(
    initialName: String,
    initialBio: String,
    initialTags: List<Tag>,
    availableTags: List<Tag>,
    isTrainer: Boolean,
    modifier: Modifier = Modifier,
    initialMaxTrainees: Int = 0,
    onCancel: () -> Unit,
    onSave: (name: String, bio: String, maxTrainees: String, password: String, tags: List<Tag>) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var bio by remember(initialBio) { mutableStateOf(initialBio) }
    var maxTrainees by remember(initialMaxTrainees) { mutableStateOf(initialMaxTrainees.toString()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedTags by remember(initialTags) { mutableStateOf(initialTags) }
    val isChangingPassword = password.isNotBlank() || confirmPassword.isNotBlank()
    val passwordsDoNotMatch = isChangingPassword && password != confirmPassword

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isTrainer) {
            OutlinedTextField(
                value = maxTrainees,
                onValueChange = { value ->
                    maxTrainees = value.filter { it.isDigit() }
                },
                label = { Text("Max Trainees") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "Tags",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        TagDropdown(
            selectedTags = selectedTags,
            items = availableTags,
            onTagSelected = { tag ->
                selectedTags = if (selectedTags.any { it.tagId == tag.tagId }) {
                    selectedTags.filterNot { it.tagId == tag.tagId }
                } else {
                    selectedTags + tag
                }
            },
            onTagRemoved = { tag ->
                selectedTags = selectedTags.filterNot { it.tagId == tag.tagId }
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = passwordsDoNotMatch,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        if (passwordsDoNotMatch) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Passwords do not match",
                color = Color(0xFFDC2626),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE9ECEF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    if (!passwordsDoNotMatch) {
                        onSave(name, bio, maxTrainees, password.trim(), selectedTags)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun SkillChip(
    text: String
) {
    AssistChip(
        onClick = {},
        label = {
            Text(
                text = text,
                fontSize = 12.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFE9ECEF)
        ),
        border = null
    )
}
