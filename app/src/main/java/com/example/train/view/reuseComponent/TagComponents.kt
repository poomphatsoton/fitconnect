package com.example.train.view.reuseComponent

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TraineeTag(
    tag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF3F4F6),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = tag,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SlotStatusTag(
    status: Int,
    modifier: Modifier = Modifier
) {
    val text = when (status) {
        0 -> "IDEAL"
        1 -> "MAYBE"
        else -> "BUSY"
    }
    val color = when (status) {
        0 -> Color(0xFFD1FAE5)
        1 -> Color(0xFFFEF3C7)
        else -> Color(0xFFF3F4F6)
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
