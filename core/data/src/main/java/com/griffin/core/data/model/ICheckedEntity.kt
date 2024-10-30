package com.griffin.core.data.model

/**
 * 单选 or 多选 的实体类接口,返回当前选择状态
 */
interface ICheckedEntity {

    /**
     * 是否已选择
     */
    var isSelected: Boolean

}