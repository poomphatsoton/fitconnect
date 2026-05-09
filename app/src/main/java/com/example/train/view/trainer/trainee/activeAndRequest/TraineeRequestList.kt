package com.example.train.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
import com.example.train.model.trainer.Trainee
import com.example.train.view.trainer.trainee.activeAndRequest.TraineeBaseCard

@Composable
fun TraineeRequestList(
    trainees: List<Trainee>,
    trainerId: Int,
    modifier: Modifier = Modifier,
    onApproveClick: (Int, Int) -> Boolean,
    onDenyClick: (Int, Int) -> Boolean,
) {
    if (trainees.isEmpty()) {
        EmptyState(message = "No pending requests")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(trainees) { trainee ->
                TraineeRequestCard(
                    trainee = trainee,
                    trainerId = trainerId,
                    onApproveClick = onApproveClick,
                    onDenyClick = onDenyClick
                )
            }
        }
    }
}

@Composable
fun TraineeRequestCard(
    trainee: Trainee,
    trainerId: Int,
    onApproveClick: (Int, Int) -> Boolean,
    onDenyClick: (Int, Int) -> Boolean,
) {
    TraineeBaseCard(
        name = trainee.name,
        bio = trainee.bio,
        tags = trainee.tags,
        imageRes = trainee.imageRes
    ) {
        Button(
            onClick = { onApproveClick(trainerId, trainee.id) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Approve", fontSize = 15.sp)
        }

        OutlinedButton(
            onClick = { onDenyClick(trainerId, trainee.id) },
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.x),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Deny", color = Color.Black, fontSize = 15.sp)
        }
    }
}
