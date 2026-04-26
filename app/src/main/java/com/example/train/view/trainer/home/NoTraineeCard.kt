package com.example.train.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R

@Composable
fun NoTraineesCard() {
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
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_group),
                contentDescription = "No trainees",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No trainees yet",
                fontSize = 18.sp,
                color = Color(0xFF6C757D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trainees can request to join you from the Trainers list",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}