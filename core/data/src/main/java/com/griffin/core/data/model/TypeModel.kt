package com.griffin.core.data.model

import com.griffin.core.rv.model.ItemExpand
import kotlinx.serialization.Serializable

/**
 * 类型模型
 *
 * @param parentId 父ID
 * @param typeId 类型ID
 * @param typeName 类型名称
 * @param typeTag 类型标签
 * @param itemGroupPosition 同级别分组的索引位置
 * @param itemExpand 是否展开
 * @param itemChildList 子列表
 */
data class TypeModel(
    val parentId: Long = 0,
    val typeId: Long = 0,
    val typeName: String = "",
    val typeTag: String = "",
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = true,
    override var itemChildList: List<Any?>? = listOf<TypeChildModel>(),
    override var isSelected: Boolean = false
): ItemExpand, ICheckedEntity, ItemStableId {
    override fun getItemId(): Long {
        return typeId
    }
}

/**
 * 类型子模型
 *
 * @param parentId 父ID
 * @param typeId 类型ID
 * @param typeName 类型名称
 * @param typeTag 类型标签
 */
@Serializable
data class TypeChildModel(
    val parentId: Long = 0,
    val typeId: Long = 0,
    val typeName: String = "",
    val typeTag: String = "",
    override var isSelected: Boolean = false
): ICheckedEntity, java.io.Serializable, ItemStableId {
    override fun getItemId(): Long {
        return typeId
    }
}