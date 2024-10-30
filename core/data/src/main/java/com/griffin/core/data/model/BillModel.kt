package com.griffin.core.data.model

import com.griffin.core.data.dto.UserAccountDto
import com.griffin.core.data.dto.UserPayTypeDto
import com.griffin.core.rv.model.ItemExpand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 账单模型
 *
 * @param createTime 创建时间
 * @param income 收入
 * @param expenses 支出
 * @param itemGroupPosition 同级别分组的索引位置
 */
data class BillModel(
    val id: Long = 0L,
    val createTime: String = "",
    val income: Long = 0,
    val expenses: Long = 0,
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    override var itemChildList: List<Any?>? = listOf<BillListModel>()
) : ItemExpand, ItemStableId {
    override fun getItemId(): Long {
        return id
    }
}

/**
 * 账单列表模型
 *
 * @param billId 账单ID
 * @param userPayTypeDto 用户支付类型
 * @param billName 账单名称
 * @param billAmount 账单金额
 * @param isIncome 是否是收入（true：收入，false：支出）
 * @param remark 备注
 * @param createTime 创建时间
 * @param address 地址
 * @param longitude 经度
 * @param latitude 纬度
 */
data class BillListModel(
    var billId: Long = 0,
    var userPayTypeDto: UserPayTypeDto = UserPayTypeDto(),
    var userAccountDto: UserAccountDto = UserAccountDto(),
    var billName: String = "",
    var billAmount: Long = 0,
    var isIncome: Boolean = false,
    var remark: String = "",
    var createTime: String = "",
    var address: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
): ItemStableId {
    override fun getItemId(): Long {
        return billId
    }
}

/**
 * 选择地图位置的回调
 *
 * @param address 地址
 * @param longitude 经度
 * @param latitude 纬度
 */
@Serializable
data class MapLocationModel(
    @SerialName("address")
    val address: String = "",
    @SerialName("longitude")
    val longitude: Double = 0.0,
    @SerialName("latitude")
    val latitude: Double = 0.0
): java.io.Serializable