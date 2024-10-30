package com.griffin.core.rv

import android.annotation.SuppressLint
import android.util.NoSuchPropertyException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.griffin.core.data.model.ICheckedEntity
import com.griffin.core.rv.model.ItemExpand
import com.griffin.core.rv.model.SelectSealed
import java.util.concurrent.atomic.AtomicLong

/**
 * Custom adapter for RecyclerView Kotlin extension.
 */
@SuppressLint("NotifyDataSetChanged")
open class ReuseAdapter : RecyclerView.Adapter<ReuseAdapter.BaseViewHolder>() {

    var rv: RecyclerView? = null

    /**
     * Model data
     */
    private var _list: MutableList<Any?> = mutableListOf()
    val list = _list

    // Data binding callback.
    private var _onBind: (BaseViewHolder.() -> Unit)? = null

    // Click event callback.
    private var _onItemClick: (BaseViewHolder.(view: View) -> Unit)? =
        null

    // Long click event callback.
    private var _onItemLongClick: (BaseViewHolder.(view: View) -> Unit)? =
        null

    // Selection callback.
    private var onChecked: ((position: Int, checked: Boolean, isAll: Boolean) -> Unit)? =
        null

    // Child item click event callback collection, key is the ID of the child item, and the value is the callback.
    private val clickListeners = mutableMapOf<Int, (BaseViewHolder.(viewId: Int) -> Unit)>()

    // Child item long click event callback collection, key is the ID of the child item, and the value is the callback.
    private val longClickListeners = mutableMapOf<Int, (BaseViewHolder.(viewId: Int) -> Unit)>()

    /**
     * Adds a layout of the specified type to support multiple layouts.
     *
     * Entity class (index) -> Layout ID.
     *  Any.(Int) -> Int
     */
    var typeLayouts = mutableMapOf<Class<*>, Any.(Int) -> Int>()

    /**
     * Adds a header layout to support multiple layouts.
     */
    var headerList = mutableListOf<Any?>()

    /**
     * Adds a footer layout to support multiple layouts.
     */
    var footerList = mutableListOf<Any?>()

    /**
     * Gets the number of header layouts.
     */
    val headerCount get() = headerList.size

    /**
     * Gets the number of footer layouts.
     */
    val footerCount get() = footerList.size

    /**
     * Whether to apply debounce to click events, default is true.
     */
    var shakeEnable = true

    /**
     * Position of the selected item.
     */
    val checkedPosition = mutableListOf<Int>()

    /**
     * Select mode.
     *
     * Default selection mode is [SelectSealed.None] does nothing.
     * [SelectSealed.Single] single selection.
     * [SelectSealed.Multiple] multiple selection.
     */
    var selectModel: SelectSealed = SelectSealed.None

    /** Number of selected items. */
    val checkedCount: Int get() = checkedPosition.size

    override fun getItemViewType(position: Int): Int {
        val item = getData<Any>(position)
        return typeLayouts[item.javaClass]?.invoke(item, position) ?: throw NoSuchPropertyException(
            "No such property in typeLayouts: $item"
        )
    }

    /**
     * Create a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    /**
     * Bind data to ViewHolder.
     */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(getData(position))
    }

    override fun getItemCount(): Int {
        return _list.size + headerCount + footerCount
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rv = recyclerView
    }

    /**
     * [onBindViewHolder] callback.
     *
     * @param onBind [@kotlin.ExtensionFunctionType] Function1<BaseViewHolder, Unit>
     */
    fun onBind(onBind: (BaseViewHolder.() -> Unit)) {
        _onBind = onBind
    }

    /**
     * Add item click event callback to RecyclerView.
     */
    fun onItemClick(onItemClick: (BaseViewHolder.(view: View) -> Unit)) {
        _onItemClick = onItemClick
    }

    /**
     * Add item long click event callback to RecyclerView.
     */
    fun onItemLongClick(onItemLongClick: (BaseViewHolder.(view: View) -> Unit)) {
        _onItemLongClick = onItemLongClick
    }


    /**
     * Selection callback.
     */
    fun onChecked(block: (position: Int, checked: Boolean, isAll: Boolean) -> Unit) {
        onChecked = block
    }

    /**
     * Add child item click event callback to RecyclerView.
     */
    fun addOnItemChildClickListener(
        @IdRes vararg id: Int,
        onItemChildClick: (BaseViewHolder.(viewId: Int) -> Unit)
    ) {
        id.forEach {
            clickListeners[it] = onItemChildClick
        }
    }

    /**
     * Add child item long click event callback to RecyclerView.
     */
    fun addOnItemChildLongClickListener(
        @IdRes vararg id: Int,
        onItemChildLongClick: (BaseViewHolder.(viewId: Int) -> Unit)
    ) {
        id.forEach {
            longClickListeners[it] = onItemChildLongClick
        }
    }

    /**
     * Select all or deselect all.
     *
     * @param checked In single selection mode, selecting all is not allowed, but deselecting is permitted.
     */
    fun checkedAll(checked: Boolean = true) {
        if (isCheck()) return
        if (checked) {
            if (selectModel is SelectSealed.Single) return
            _list.forEachIndexed { index, t ->
                if (!checkedPosition.contains(index)) {
                    setChecked(index, true)
                }
            }
        } else {
            _list.forEachIndexed { index, t ->
                if (checkedPosition.contains(index)) setChecked(index, false)
            }
        }
    }

    /**
     * Set the specified index to selected or unselected.
     *
     * @param position Index
     * @param checked true: Selected, false: Deselect.
     */
    fun setChecked(position: Int, checked: Boolean) {
        // Avoid redundant operations.
        if ((checkedPosition.contains(position) && checked) ||
            (!checked && !checkedPosition.contains(position)) || isCheck() || position < 0
        ) return

        // If the entity class does not implement the ICheckedEntity interface, return directly.
        val item = getData<Any>(position)
        if (item !is ICheckedEntity) return

        if (checked) checkedPosition.add(position)
        else checkedPosition.remove(position)

        if (selectModel is SelectSealed.Single && checked && checkedPosition.size > 1) {
            // In single selection mode, use recursion to deselect.
            setChecked(checkedPosition[0], false)
        }
        // Modify the selection state.
        item.isSelected = checked
        // Selection callback.
        onChecked?.invoke(position, checked, isCheckedAll())
        notifyItemChanged(position)
    }

    /**
     * Toggle the selection state of the specified index.
     */
    fun checkedSwitch(position: Int) {
        if (isCheck() && position < 0) return
        if (checkedPosition.contains(position)) {
            setChecked(position, false)
        } else {
            setChecked(position, true)
        }
    }

    /**
     * Determine whether selection is needed.
     *
     * @return true: Selection needed, false: No selection needed.
     */
    private fun isCheck() = onChecked != null && selectModel !is SelectSealed.None

    /**
     * Add header layout.
     *
     * Header layout is essentially a type of multiple layouts, so it also needs to be added like [addType].
     *
     * @param header Header layout entity class.
     * @param position Add to the specified header layout index. If the index is negative or greater than the number of headers, add to the last position.
     * @param isScrollTop Whether to scroll to the top.
     */
    fun addHeader(header: Any, position: Int = -1, isScrollTop: Boolean = false) {
        if (position == -1 || position >= headerCount) {
            headerList.add(header)
            notifyItemInserted(headerCount)
        } else {
            headerList.add(position, header)
            notifyItemInserted(position)
        }
        if (isScrollTop) {
            scrollRv(0)
        }
    }

    /**
     * Set the old header layout to the new header layout.
     *
     * @param oldHeader Old header layout.
     * @param newHeader New header layout.
     * @param isScrollTop Whether to scroll to the top.
     */
    fun setHeader(oldHeader: Any, newHeader: Any, isScrollTop: Boolean = false) {
        headerList[headerList.indexOf(oldHeader)] = newHeader
        notifyItemChanged(headerList.indexOf(oldHeader))
        if (isScrollTop) {
            scrollRv(0)
        }
    }

    /**
     *Set the specified header layout index to the new header layout.
     *
     * @param position Index
     * @param newHeader New header layout.
     * @param isScrollTop Whether to scroll to the top.
     */
    fun setHeader(position: Int, newHeader: Any, isScrollTop: Boolean = false) {
        headerList[position] = newHeader
        notifyItemChanged(position)
        if (isScrollTop) {
            scrollRv(0)
        }
    }

    /**
     * Remove header layout by specifying type.
     */
    fun removeHeader(type: Any) {
        if (headerList.contains(type)) {
            headerList.forEachIndexed { index, any ->
                if (any == type) {
                    headerList.removeAt(index)
                    notifyItemRemoved(index)
                }
            }
        }
    }

    /**
     * Remove header layout by specifying index.
     */
    fun removeHeader(position: Int) {
        if (position >= headerCount) return
        headerList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Remove all header layouts.
     */
    fun removeHeaderAll() {
        if (headerCount > 0) {
            headerList.clear()
            notifyItemRangeRemoved(0, headerCount)
        }
    }

    /**
     * Determine whether the specified index is a header layout.
     *
     * @param position Index
     * @return Boolean true: Header layout, false: Not a header layout
     */
    fun isHeader(position: Int): Boolean = (position < headerCount && headerCount > 0)

    /**
     * Add footer layout.
     *
     * Footer layout is essentially a type of multiple layouts, so it also needs to be added like [addType].
     *
     * @param footer Footer layout entity class.
     * @param position Add to the specified footer layout index. If the index is negative or greater than the number of footers, add to the last position.
     * @param isScrollBottom Whether to scroll to the bottom.
     */
    fun addFooter(footer: Any, position: Int = -1, isScrollBottom: Boolean = false) {
        if (position == -1 || position >= footerCount) {
            footerList.add(footer)
            notifyItemInserted(itemCount)
        } else {
            footerList.add(position, footer)
            notifyItemInserted(itemCount)
        }
        if (isScrollBottom) {
            scrollRv(itemCount - 1)
        }
    }

    /**
     * Set the old footer layout to the new footer layout.
     *
     * @param oldFooter Old footer layout.
     * @param newFooter New footer layout.
     * @param isScrollBottom Whether to scroll to the bottom.
     */
    fun setFooter(oldFooter: Any, newFooter: Any, isScrollBottom: Boolean = false) {
        footerList[footerList.indexOf(oldFooter)] = newFooter
        notifyItemChanged(itemCount)
        if (isScrollBottom) {
            scrollRv(itemCount - 1)
        }
    }

    /**
     * Remove footer layout by specifying type.
     *
     * @param type Type
     */
    fun removeFooter(type: Any) {
        if (footerList.contains(type)) {
            footerList.forEachIndexed { index, any ->
                if (any == type) {
                    footerList.removeAt(index)
                    notifyItemRemoved(headerCount + _list.size + index)
                }
            }
        }
    }

    /**
     * Remove footer layout by specifying index.
     *
     * @param position Index
     */
    fun removeFooter(position: Int) {
        if (position >= footerCount) return
        footerList.removeAt(position)
        notifyItemRemoved(headerCount + _list.size + position)
    }

    /**
     * Remove all footer layouts.
     */
    fun removeFooterAll() {
        if (footerCount > 0) {
            footerList.clear()
            notifyItemRangeRemoved(headerCount + _list.size, footerCount)
        }
    }

    /**
     * Determine if the specified index is a footer layout.
     *
     * @param position Index
     * @return Boolean true: Footer layout, false: Not a footer layout
     */
    fun isFooter(position: Int): Boolean =
        (position >= headerCount + _list.size && footerCount > 0 && position < itemCount)

    /**
     * Scroll RecyclerView to the specified position.
     */
    fun scrollRv(lastIndex: Int) {
        rv?.let { rv ->
            rv.scrollToPosition(lastIndex)
            (rv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                lastIndex,
                0
            )
        }
    }

    /**
     * Add a layout of the specified type to support multiple layouts.
     *
     * @param layoutRes Int
     */
    inline fun <reified T> addType(@LayoutRes layoutRes: Int) {
        typeLayouts[T::class.java] = { layoutRes }
    }

    /**
     * Add a layout of the specified type. The generic type here must match the Model type; otherwise, the corresponding layout will not be found.
     *
     * Entity class (index) -> Layout ID.
     */
    inline fun <reified T> addType(noinline block: T.(Int) -> Int) {
        typeLayouts[T::class.java] = block as Any.(Int) -> Int
    }

    /**
     * Get the data model based on the specified index.
     */
    fun <T> getData(index: Int): T {
        return if (isHeader(index)) {
            headerList[index] as T
        } else if (isFooter(index)) {
            footerList[index - headerCount - _list.size] as T
        } else {
            _list[index - headerCount] as T
        }
    }

    /**
     * Get the data model based on the specified index. If the data model is not found, return null.
     *
     * @param index Int
     * @return T
     */
    inline fun <reified T> getDataOrNull(index: Int): T? {
        return try {
            if (isHeader(index)) {
                headerList[index] as? T
            } else if (isFooter(index)) {
                footerList[index - headerCount - list.size] as? T
            } else {
                list.getOrNull(index - headerCount) as? T
            }
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Set the data source.
     *
     * @param list Dataset
     */
    fun setData(list: List<Any?>?) {
        _list.clear()
        checkedPosition.clear()
        if (!list.isNullOrEmpty()) {
            list.forEachIndexed { index, any ->
                _list.add(any)
                if (any is ItemExpand && any.itemExpand && !any.itemChildList.isNullOrEmpty()){
                    _list.addAll(any.itemChildList ?: emptyList())
                }
                if (any is ICheckedEntity && any.isSelected){
                    checkedPosition.add(index)
                }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * Set the data source.
     *
     * @param index Index
     * @param item Data
     */
    fun setData(index: Int, item: Any) {
        if (index < _list.size) {
            _list[index] = item
            notifyItemChanged(index)
            isRefresh(0)
        }
    }

    /**
     * Add a collection of data.
     *
     * @param data Dataset
     * @param index Index
     */
    fun addData(data: List<Any?>, index: Int = -1) {
        if (index < _list.size) {
            if (index == -1) {
                _list.addAll(data)
                notifyItemRangeInserted(_list.size - data.size, data.size)
            } else {
                _list.addAll(index, data)
                notifyItemRangeInserted(index, data.size)
            }
            isRefresh(data.size)
        }
    }

    /**
     * Add data.
     *
     * @param item Data
     * @param index Index
     */
    fun addData(item: Any?, index: Int = -1) {
        if (index < _list.size) {
            if (index == -1) {
                _list.add(item)
                notifyItemInserted(_list.size - 1)
            } else {
                _list.add(index, item)
                notifyItemInserted(index)
            }
            isRefresh(0)
        }
    }

    /**
     * Delete data at the specified index.
     *
     * @param index Index
     */
    fun removeAt(index: Int) {
        if (index < _list.size) {
            _list.removeAt(index)
            notifyItemRemoved(index)
            isRefresh(0)
            notifyItemRangeChanged(index, _list.size - index)
        }
    }

    /**
     * Whether to select all.
     */
    fun isCheckedAll(): Boolean = checkedCount == _list.size

    /**
     * Refresh the adapter when the data matches the specified size.
     *
     * @param size Int
     */
    private fun isRefresh(size: Int) {
        if (_list.size == size) {
            notifyDataSetChanged()
        }
    }

    inner class BaseViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        lateinit var _item: Any private set

        fun <DB : ViewDataBinding> getBinding(): DB? {
            return if (isHeader(bindingAdapterPosition) || isFooter(bindingAdapterPosition)) {
                null
            } else {
                DataBindingUtil.bind(itemView)
            }
        }

        fun <DB : ViewDataBinding> getHeaderBinding(): DB? {
            return if (isHeader(bindingAdapterPosition)) {
                DataBindingUtil.bind(itemView)
            } else {
                null
            }
        }

        fun <DB : ViewDataBinding> getFooterBinding(): DB? {
            return if (isFooter(bindingAdapterPosition)) {
                DataBindingUtil.bind(itemView)
            } else {
                null
            }
        }

        internal fun onBind(item: Any) {
            _item = item
            _onBind?.invoke(this)
            _onItemClick?.let { itemClick ->
                if (shakeEnable) {
                    itemView.setOnClickListener(ShakeClickListener {
                        itemClick.invoke(this@BaseViewHolder, this)
                    })
                } else {
                    itemView.setOnClickListener {
                        itemClick.invoke(this@BaseViewHolder, it)
                    }
                }
            }
            _onItemLongClick?.let { longClick ->
                itemView.setOnLongClickListener {
                    longClick.invoke(this@BaseViewHolder, it)
                    true
                }
            }
            for (clickListener in clickListeners) {
                val view = itemView.findViewById<View>(clickListener.key) ?: continue
                if (shakeEnable) {
                    view.setOnClickListener(ShakeClickListener {
                        clickListener.value.invoke(this@BaseViewHolder, clickListener.key)
                    })
                } else {
                    view.setOnClickListener {
                        clickListener.value.invoke(this@BaseViewHolder, clickListener.key)
                    }
                }
            }
            for (longClickListener in longClickListeners) {
                val view = itemView.findViewById<View>(longClickListener.key) ?: continue
                view.setOnLongClickListener {
                    longClickListener.value.invoke(this@BaseViewHolder, longClickListener.key)
                    true
                }
            }
        }

        /**
         * Retrieve the current data type.
         *
         * @return Any
         */
        fun getType() = _item

        /**
         * Retrieve data of the current type.
         */
        inline fun <reified T> getItem() = _item as T

        /**
         * Retrieve data of the current type. If the data type does not match, return null.
         */
        inline fun <reified T> getItemOrNull() = _item as? T

        /**
         * Find a view by its ID.
         *
         * @param id Int ID
         * @return (V..V?)
         */
        fun <V : View> findView(@IdRes id: Int) = itemView.findViewById<V>(id)

        /**
         * Expand data.
         */
        fun expand(scrollTop: Boolean = false) {
            (_item as? ItemExpand)?.let {
                val childList = it.itemChildList
                if (it.itemExpand || bindingAdapterPosition == -1 || childList.isNullOrEmpty()) return
                it.itemGroupPosition = bindingAdapterPosition
                it.itemExpand = true
                _list.addAll(bindingAdapterPosition + 1, childList)
                notifyItemChanged(bindingAdapterPosition)
                notifyItemRangeInserted(bindingAdapterPosition + 1, childList.size)
                notifyItemRangeChanged(bindingAdapterPosition + 1, _list.size)
                if (scrollTop) {
                    rv?.let { rv ->
                        rv.scrollToPosition(bindingAdapterPosition)
                        (rv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                            bindingAdapterPosition,
                            0
                        )
                    }
                }
            }
        }

        /**
         * Collapse data.
         */
        fun collapse() {
            (_item as? ItemExpand)?.let {
                collapseAll(it)
            }
        }

        /**
         * Recursively collapse all data.
         */
        private fun collapseAll(item: ItemExpand) {
            val childList = item.itemChildList
            if (!item.itemExpand || bindingAdapterPosition == -1 || childList.isNullOrEmpty()) return
            item.itemChildList?.forEach {
                if (it is ItemExpand) {
                    collapseAll(it)
                }
            }
            item.itemExpand = false
            item.itemGroupPosition = bindingAdapterPosition
            _list.removeAll(childList)
            notifyItemChanged(bindingAdapterPosition)
            notifyItemRangeRemoved(bindingAdapterPosition + 1, childList.size)
            notifyItemRangeChanged(bindingAdapterPosition + 1, _list.size)
        }

        /**
         * Expand or collapse data.
         */
        fun expandOrCollapse(scrollTop: Boolean = false) {
            (_item as? ItemExpand)?.let {
                if (it.itemExpand) {
                    collapse()
                } else {
                    expand(scrollTop)
                }
            }
        }
    }

    /**
     * Debounce click events.
     *
     * @property internal
     * @property block
     */
    private class ShakeClickListener(
        private val internal: Int = 500,
        private val block: View.() -> Unit
    ) : View.OnClickListener {

        private val lastClickTime = AtomicLong(0)

        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime.get() > internal) {
                lastClickTime.set(currentTime)
                block.invoke(v)
            }
        }
    }

}