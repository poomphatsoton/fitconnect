package com.example.train.view.trainer.trainee.activeAndRequest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.train.ui.TraineeTabItem

@Composable
fun TraineeTabSelector(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    tabs: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3F4F6))
            .padding(4.dp)
    ) {
        tabs.forEach { (label, value) ->
            TraineeTabItem(
                label = label,
                isSelected = selectedTab == value,
                onClick = { onTabSelected(value) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}