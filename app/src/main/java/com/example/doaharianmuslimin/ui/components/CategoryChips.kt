package com.example.doaharianmuslimin.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.model.DoaCategory
import com.example.doaharianmuslimin.ui.theme.AccentGold

@Composable
fun CategoryChips(
    selectedCategory: DoaCategory,
    onCategorySelected: (DoaCategory) -> Unit,
    isFavoritesSelected: Boolean = false,
    onFavoritesClick: () -> Unit = {},
    isHistorySelected: Boolean = false,
    onHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Special Chip: ⭐ Favorit
        FilterChip(
            selected = isFavoritesSelected,
            onClick = onFavoritesClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = if (isFavoritesSelected) Color.White else AccentGold,
                    modifier = Modifier.size(15.dp)
                )
            },
            label = {
                Text(
                    text = "Favorit",
                    fontWeight = if (isFavoritesSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.5.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AccentGold,
                selectedLabelColor = Color.White,
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Special Chip: 🕒 Riwayat
        FilterChip(
            selected = isHistorySelected,
            onClick = onHistoryClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = if (isHistorySelected) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
            },
            label = {
                Text(
                    text = "Riwayat",
                    fontWeight = if (isHistorySelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.5.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White,
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        DoaCategory.entries.forEach { category ->
            val isSelected = !isFavoritesSelected && !isHistorySelected && (category == selectedCategory)
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.5.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
