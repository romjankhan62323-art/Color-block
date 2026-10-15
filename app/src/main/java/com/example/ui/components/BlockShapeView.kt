package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.BlockColor
import com.example.model.BlockShapeCatalog
import com.example.model.CellCoord

@Composable
fun BlockShapeView(
    cells: List<CellCoord>,
    color: BlockColor,
    cellSize: Dp,
    gap: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val bounds = BlockShapeCatalog.getBounds(cells)
    val totalWidth = cellSize * bounds.cols + gap * (bounds.cols - 1)
    val totalHeight = cellSize * bounds.rows + gap * (bounds.rows - 1)

    Box(
        modifier = modifier.size(totalWidth, totalHeight)
    ) {
        cells.forEach { coord ->
            val rIdx = coord.r - bounds.minR
            val cIdx = coord.c - bounds.minC
            val xOffset = (cellSize + gap) * cIdx
            val yOffset = (cellSize + gap) * rIdx

            ColorBlockView(
                color = color,
                size = cellSize,
                modifier = Modifier.offset(x = xOffset, y = yOffset)
            )
        }
    }
}
