package com.example.train.view.trainee.trainer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainee.TrainerProfile
import com.example.train.view.reuseComponent.TraineeTag
import com.example.train.viewmodel.trainee.TraineeTrainerViewModel

@Composable
fun TraineeTrainerScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineeTrainerViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadTrainers()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Search trainers") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            TrainerTagFilter(
                tags = uiState.availableTags,
                selectedTags = uiState.selectedTags,
                onTagClick = viewModel::onTagClick,
                onSelectAll = viewModel::onSelectAllTags
            )
        }

        item {
            SectionTitle(text = "My Trainer")
        }

        item {
            val trainer = uiState.myTrainer
            if (trainer == null) {
                EmptyTrainerText(text = "No trainer enrolled")
            } else {
                TrainerCard(
                    trainer = trainer,
                    showRequestStatus = trainer.requestStatus == DatabaseHelper.STATUS_PENDING,
                    actionText = if (trainer.requestStatus == DatabaseHelper.STATUS_PENDING) "Cancel" else "Unroll",
                    onActionClick = {
                        if (trainer.requestStatus == DatabaseHelper.STATUS_PENDING) {
                            viewModel.cancelTrainerRequest(trainer.id)
                        } else {
                            viewModel.unrollTrainer(trainer.id)
                        }
                    }
                )
            }
        }

        item {
            HorizontalDivider(
                color = Color.Black,
                thickness = 2.dp
            )
        }

        item {
            SectionTitle(text = "Other Trainers")
        }

        if (uiState.otherTrainers.isEmpty()) {
            item {
                EmptyTrainerText(text = "No trainers found")
            }
        } else {
            items(uiState.otherTrainers) { trainer ->
                TrainerCard(
                    trainer = trainer,
                    actionText = "Request",
                    actionEnabled = !uiState.hasTrainerOrPendingRequest,
                    onActionClick = {
                        viewModel.requestTrainer(trainer.id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainerTagFilter(
    tags: List<Tag>,
    selectedTags: List<Tag>,
    onTagClick: (Tag) -> Unit,
    onSelectAll: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var menuWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val label = if (selectedTags.isEmpty()) {
        "Select all"
    } else {
        "${selectedTags.size} tags selected"
    }

    Column {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .onGloballyPositioned {
                    menuWidth = with(density) {
                        it.size.width.toDp()
                    }
                },
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF374151)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(menuWidth)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedTags.isEmpty(),
                                onCheckedChange = null
                            )

                            Text(text = "Select all")
                        }
                    },
                    onClick = onSelectAll
                )

                HorizontalDivider(color = Color(0xFFE5E7EB))

                tags.forEach { tag ->
                    val selected = selectedTags.any { it.tagId == tag.tagId }
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = null
                                )

                                Text(text = tag.tagName)
                            }
                        },
                        onClick = { onTagClick(tag) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
private fun EmptyTrainerText(
    text: String
) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainerCard(
    trainer: TrainerProfile,
    showRequestStatus: Boolean = false,
    actionText: String? = null,
    actionEnabled: Boolean = true,
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = Color.Black,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = trainer.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = trainer.bio,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${trainer.availableTrainees} / ${trainer.maxTrainees} available trainees",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )

                    if (showRequestStatus) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Status: Request",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2563EB)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trainer.tags.forEach { tag ->
                            TraineeTag(tag = tag.tagName)
                        }
                    }
                }
            }

            if (actionText != null) {
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onActionClick,
                    enabled = actionEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actionText == "Request") Color(0xFF0F172A) else Color(0xFFE5E7EB),
                        contentColor = if (actionText == "Request") Color.White else Color.Black,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = actionText, fontSize = 15.sp)
                }
            }
        }
    }
}
