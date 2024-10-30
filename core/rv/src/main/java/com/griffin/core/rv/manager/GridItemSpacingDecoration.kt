package com.griffin.core.rv.manager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemSpacingDecoration(
    private val columnCount: Int,
    private val verticalSpacing: Int,
    private val horizontalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val columnIndex = itemPosition % columnCount

        outRect.left = columnIndex * horizontalSpacing / columnCount
        outRect.right = horizontalSpacing - (columnIndex + 1) * horizontalSpacing / columnCount

        if (itemPosition >= columnCount) {
            outRect.top = verticalSpacing
        }
    }
}