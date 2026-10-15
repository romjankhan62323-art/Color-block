package com.example.model

data class CellCoord(val r: Int, val c: Int)

data class BlockShapeDefinition(
    val id: String,
    val name: String,
    val defaultColor: BlockColor,
    val cells: List<CellCoord>
)

data class TrayPiece(
    val id: String,
    val shapeId: String,
    val name: String,
    val cells: List<CellCoord>,
    val color: BlockColor,
    val slotIndex: Int,
    val placed: Boolean = false
)

object BlockShapeCatalog {
    val SHAPES = listOf(
        // 1. Single square
        BlockShapeDefinition("single_1x1", "Single Square", BlockColor.CYAN, listOf(CellCoord(0, 0))),

        // 2. Horizontal Lines
        BlockShapeDefinition("h_line_2", "Horizontal 2-Line", BlockColor.GREEN, listOf(CellCoord(0, 0), CellCoord(0, 1))),
        BlockShapeDefinition("h_line_3", "Horizontal 3-Line", BlockColor.BLUE, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2))),
        BlockShapeDefinition("h_line_4", "Horizontal 4-Line", BlockColor.GREEN, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2), CellCoord(0, 3))),
        BlockShapeDefinition("h_line_5", "Horizontal 5-Line", BlockColor.PURPLE, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2), CellCoord(0, 3), CellCoord(0, 4))),

        // 3. Vertical Lines
        BlockShapeDefinition("v_line_2", "Vertical 2-Line", BlockColor.ORANGE, listOf(CellCoord(0, 0), CellCoord(1, 0))),
        BlockShapeDefinition("v_line_3", "Vertical 3-Line", BlockColor.PURPLE, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0))),
        BlockShapeDefinition("v_line_4", "Vertical 4-Line", BlockColor.CYAN, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0), CellCoord(3, 0))),
        BlockShapeDefinition("v_line_5", "Vertical 5-Line", BlockColor.PINK, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0), CellCoord(3, 0), CellCoord(4, 0))),

        // 4. Square Pieces
        BlockShapeDefinition("square_2x2", "Square 2x2", BlockColor.YELLOW, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 0), CellCoord(1, 1))),
        BlockShapeDefinition("square_3x3", "Square 3x3", BlockColor.RED, listOf(
            CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2),
            CellCoord(1, 0), CellCoord(1, 1), CellCoord(1, 2),
            CellCoord(2, 0), CellCoord(2, 1), CellCoord(2, 2)
        )),

        // 5. Small L-Shaped pieces
        BlockShapeDefinition("small_l_1", "Small L (BR)", BlockColor.ORANGE, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(1, 1))),
        BlockShapeDefinition("small_l_2", "Small L (BL)", BlockColor.ORANGE, listOf(CellCoord(0, 1), CellCoord(1, 1), CellCoord(1, 0))),
        BlockShapeDefinition("small_l_3", "Small L (TR)", BlockColor.ORANGE, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 0))),
        BlockShapeDefinition("small_l_4", "Small L (TL)", BlockColor.ORANGE, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 1))),

        // 6. Large L and corner pieces
        BlockShapeDefinition("large_l_1", "Large L (3x2)", BlockColor.RED, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0), CellCoord(2, 1))),
        BlockShapeDefinition("large_l_2", "Large L Inverted", BlockColor.RED, listOf(CellCoord(0, 1), CellCoord(1, 1), CellCoord(2, 1), CellCoord(2, 0))),
        BlockShapeDefinition("large_corner_3x3", "Corner 3x3", BlockColor.BLUE, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0), CellCoord(2, 1), CellCoord(2, 2))),

        // 7. T-Shaped pieces
        BlockShapeDefinition("t_shape_down", "T-Shape Down", BlockColor.PINK, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2), CellCoord(1, 1))),
        BlockShapeDefinition("t_shape_up", "T-Shape Up", BlockColor.PINK, listOf(CellCoord(1, 0), CellCoord(1, 1), CellCoord(1, 2), CellCoord(0, 1))),
        BlockShapeDefinition("t_shape_right", "T-Shape Right", BlockColor.PINK, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0), CellCoord(1, 1))),

        // 8. Z and S pieces
        BlockShapeDefinition("z_shape", "Z-Shape", BlockColor.GREEN, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 1), CellCoord(1, 2))),
        BlockShapeDefinition("s_shape", "S-Shape", BlockColor.GREEN, listOf(CellCoord(1, 0), CellCoord(1, 1), CellCoord(0, 1), CellCoord(0, 2)))
    )

    fun getBounds(cells: List<CellCoord>): ShapeBounds {
        if (cells.isEmpty()) return ShapeBounds(0, 0, 0, 0, 1, 1)
        val minR = cells.minOf { it.r }
        val maxR = cells.maxOf { it.r }
        val minC = cells.minOf { it.c }
        val maxC = cells.maxOf { it.c }
        return ShapeBounds(minR, maxR, minC, maxC, maxR - minR + 1, maxC - minC + 1)
    }

    private val SMALL_IDS = listOf("single_1x1", "h_line_2", "v_line_2", "small_l_1", "small_l_2")
    private val MEDIUM_IDS = listOf("h_line_3", "v_line_3", "square_2x2", "small_l_3", "small_l_4", "t_shape_down", "t_shape_up", "z_shape", "s_shape")
    private val LARGE_IDS = listOf("h_line_4", "v_line_4", "large_l_1", "large_l_2", "square_3x3", "large_corner_3x3")

    fun generateBalancedTrayDeal(batchSeed: Long = System.currentTimeMillis()): List<TrayPiece> {
        fun pickFrom(ids: List<String>): BlockShapeDefinition {
            val id = ids.random()
            return SHAPES.find { it.id == id } ?: SHAPES[0]
        }
        val pool = listOf(
            pickFrom(SMALL_IDS),
            pickFrom(MEDIUM_IDS),
            if (Math.random() < 0.65) pickFrom(MEDIUM_IDS) else pickFrom(LARGE_IDS)
        ).shuffled()

        return pool.mapIndexed { index, def ->
            TrayPiece(
                id = "piece_${batchSeed}_$index",
                shapeId = def.id,
                name = def.name,
                cells = def.cells,
                color = BlockColor.ALL.random(),
                slotIndex = index,
                placed = false
            )
        }
    }
}

data class ShapeBounds(
    val minR: Int,
    val maxR: Int,
    val minC: Int,
    val maxC: Int,
    val rows: Int,
    val cols: Int
)
