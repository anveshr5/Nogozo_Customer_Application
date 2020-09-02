package com.anvesh.nogozocustomerapplication.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpacingItemDecoration(
    private val spacing: Int
): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State){
        outRect.bottom = spacing
        outRect.left = spacing
        outRect.right = spacing
        outRect.top = spacing
    }
}