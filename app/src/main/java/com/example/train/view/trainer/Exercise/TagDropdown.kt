package com.example.train.view.trainer.Exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R

@Composable
fun TagDropdown(
    selectedTags: List<String>,
    items: List<String>,
    onTagSelected: (String) -> Unit,
    onTagRemoved: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var menuWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        menuWidth = with(density) {
                            it.size.width.toDp()
                        }
                    }
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF7F7F7))
                    .clickable { expanded = true },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = if (selectedTags.isEmpty()) "Select tags" else "${selectedTags.size} tags selected",
                    color = if (selectedTags.isEmpty()) Color(0xFF555555) else Color.Black,
                    fontSize = 16.sp
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(menuWidth)
                        .background(Color(0xFFF1F1F1))
                ) {
                    items.forEach { tag ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        modifier = Modifier.padding(horizontal = 5.dp),
                                        checked = selectedTags.contains(tag),
                                        onCheckedChange = null
                                    )

                                    Text(tag)
                                }
                            },
                            onClick = {
                                onTagSelected(tag)
                            }
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFE0E0E0)
                        )
                    }
                }
            }


            if (selectedTags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedTags) { tag ->
                        Surface(
                            color = Color(0xFFEFEFEF),
                            shape = RoundedCornerShape(20),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Icon(
                                    painter = painterResource(id = R.drawable.x),
                                    contentDescription = "Remove tag",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            onTagRemoved(tag)
                                        },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}