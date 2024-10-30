package com.griffin.core.rv

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.griffin.core.rv.manager.GridItemSpacingDecoration
import com.griffin.core.rv.manager.RVGridLayoutManager
import com.griffin.core.rv.manager.RVLinearLayoutManager
import com.griffin.core.rv.manager.RVStaggeredGridLayoutManager

/**
 * Retrieves a custom adapter.
 */
val RecyclerView.reuseAdapter
    get() = adapter as? ReuseAdapter
        ?: throw NullPointerException("RecyclerView has no ReuseAdapter")

/**
 * Retrieves the dataset of the adapter.
 */
val RecyclerView.data get() = reuseAdapter.list

/**
 * Adds a collection of data.
 *
 * @receiver RecyclerView
 * @param data Dataset
 * @param index Adds data from a specified index
 */
fun RecyclerView.addData(data: List<*>, index: Int = -1) {
    reuseAdapter.addData(data, index)
}

/**
 * Adds data.
 *
 * @receiver RecyclerView
 * @param item Data
 * @param index Adds data from a specified index
 */
fun RecyclerView.addData(item: Any, index: Int = -1) {
    reuseAdapter.addData(item, index)
}

/**
 * Deletes data at the specified index.
 *
 * @receiver RecyclerView
 * @param index Index
 */
fun RecyclerView.removeAt(index: Int) {
    reuseAdapter.removeAt(index)
}

/**
 * Sets the dataset.
 *
 * @receiver RecyclerView
 * @param data Dataset
 */
fun RecyclerView.setData(data: List<*>) {
    reuseAdapter.setData(data)
}

/**
 * Sets the data.
 *
 * @receiver RecyclerView
 * @param index Index
 * @param data Data
 */
fun RecyclerView.setData(index: Int, data: Any) {
    reuseAdapter.setData(index, data)
}

/**
 * Quickly sets up the adapter.
 *
 * @receiver RecyclerView
 * @param block [@kotlin.ExtensionFunctionType] Function2<ReuseAdapter, RecyclerView, Unit>
 */
fun RecyclerView.setup(
    block: ReuseAdapter.(RecyclerView) -> Unit
): ReuseAdapter {
    val adapter = ReuseAdapter()
    adapter.block(this)
    this.adapter = adapter
    return adapter
}

/**
 * Sets the RecyclerView to use LinearLayoutManager.
 *
 * @receiver RecyclerView
 * @param orientation Scroll direction [RecyclerView.VERTICAL] or [RecyclerView.HORIZONTAL]
 * @param reverseLayout Whether to reverse the list
 * @param scrollEnabled Whether scrolling is allowed
 * @param stackFromEnd true to pin the view's content to the bottom edge, false to pin the view's content to the top edge
 */
fun RecyclerView.linear(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    scrollEnabled: Boolean = true,
    stackFromEnd: Boolean = false
): RecyclerView {
    layoutManager = RVLinearLayoutManager(context, orientation, reverseLayout).apply {
        setScrollEnabled(scrollEnabled)
        this.stackFromEnd = stackFromEnd
    }
    val animator: RecyclerView.ItemAnimator = itemAnimator ?: return this
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
    return this
}

/**
 * Sets the RecyclerView to use GridLayoutManager.
 *
 * @receiver RecyclerView
 * @param spanCount Number of grid spans
 * @param horizontalSpacing Horizontal spacing
 * @param verticalSpacing Vertical spacing
 * @param orientation Scroll direction [RecyclerView.VERTICAL] or [RecyclerView.HORIZONTAL]
 * @param reverseLayout Whether to reverse the list
 * @param scrollEnabled Whether scrolling is allowed
 */
fun RecyclerView.grid(
    spanCount: Int = -1,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    scrollEnabled: Boolean = true
): RecyclerView {
    layoutManager = RVGridLayoutManager(context, spanCount, orientation, reverseLayout).apply {
        setScrollEnabled(scrollEnabled)
    }
    if (horizontalSpacing > 0 || verticalSpacing > 0) {
        addItemDecoration(GridItemSpacingDecoration(spanCount, verticalSpacing, horizontalSpacing))
    }
    val animator: RecyclerView.ItemAnimator = itemAnimator ?: return this
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
    return this
}

/**
 * Sets the RecyclerView to use StaggeredGridLayoutManager.
 *
 * @receiver RecyclerView
 * @param spanCount Number of grid spans
 * @param orientation Scroll direction [RecyclerView.VERTICAL] or [RecyclerView.HORIZONTAL]
 * @param reverseLayout Whether to reverse the list
 * @param scrollEnabled Whether scrolling is allowed
 */
fun RecyclerView.staggered(
    spanCount: Int,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    scrollEnabled: Boolean = true
): RecyclerView {
    layoutManager = RVStaggeredGridLayoutManager(spanCount, orientation).apply {
        setScrollEnabled(scrollEnabled)
        this.reverseLayout = reverseLayout
    }
    val animator: RecyclerView.ItemAnimator = itemAnimator ?: return this
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
    return this
}