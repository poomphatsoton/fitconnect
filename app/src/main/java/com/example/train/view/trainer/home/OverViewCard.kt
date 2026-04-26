package com.example.train.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R

@Composable
fun OverviewCard(
    activeTrainees: Int,
    exercises: Int,
    workouts: Int,
    pendingRequests: Int,
    onPendingClick: () -> Unit
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
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OverviewRow(
                icon = R.drawable.ic_group,
                label = "Active Trainees",
                value = activeTrainees.toString()
            )

            OverviewRow(
                icon = R.drawable.ic_list,
                label = "Exercises",
                value = exercises.toString()
            )

            OverviewRow(
                icon = R.drawable.ic_fitness,
                label = "Workouts",
                value = workouts.toString()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onPendingClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Pending Requests"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$pendingRequests Pending Request${if (pendingRequests > 1) "s" else ""}",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun OverviewRow(
    icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}