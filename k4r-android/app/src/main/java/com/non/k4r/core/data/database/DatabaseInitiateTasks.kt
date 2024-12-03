package com.non.k4r.core.data.database

import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.model.ExpenditureTagEntity
import com.non.k4r.core.holder.InitiatedFlagHolder


suspend fun initExpenditureTags(
    initiatedFlagHolder: InitiatedFlagHolder,
    expenditureTagDao: ExpenditureTagDao
) {
    if (!initiatedFlagHolder.isExpenditureTagsInitiated()) {
        val tags = listOf<ExpenditureTagEntity>(
            ExpenditureTagEntity(key = "catering", name = "餐饮"),
            ExpenditureTagEntity(key = "shopping", name = "购物"),
            ExpenditureTagEntity(key = "daily_necessities", name = "日用"),
            ExpenditureTagEntity(key = "transportation", name = "交通"),
            ExpenditureTagEntity(key = "vegetables", name = "蔬菜"),
            ExpenditureTagEntity(key = "fruits", name = "水果"),
            ExpenditureTagEntity(key = "snacks", name = "零食"),
            ExpenditureTagEntity(key = "sports", name = "运动"),
            ExpenditureTagEntity(key = "entertainment", name = "娱乐"),
            ExpenditureTagEntity(key = "communications", name = "通讯"),
            ExpenditureTagEntity(key = "clothing", name = "服饰"),
            ExpenditureTagEntity(key = "beauty", name = "美容"),
            ExpenditureTagEntity(key = "housing", name = "住房"),
            ExpenditureTagEntity(key = "home", name = "居家"),
            ExpenditureTagEntity(key = "children", name = "孩子"),
            ExpenditureTagEntity(key = "elders", name = "长辈"),
            ExpenditureTagEntity(key = "social", name = "社交"),
            ExpenditureTagEntity(key = "travel", name = "旅行"),
            ExpenditureTagEntity(key = "tobacco_and_alcohol", name = "烟酒"),
            ExpenditureTagEntity(key = "digital", name = "数码"),
            ExpenditureTagEntity(key = "cars", name = "汽车"),
            ExpenditureTagEntity(key = "medical", name = "医疗"),
            ExpenditureTagEntity(key = "books", name = "书籍"),
            ExpenditureTagEntity(key = "study", name = "学习"),
            ExpenditureTagEntity(key = "pets", name = "宠物"),
            ExpenditureTagEntity(key = "money_gift", name = "礼金"),
            ExpenditureTagEntity(key = "physical_gift", name = "礼物"),
            ExpenditureTagEntity(key = "office", name = "办公"),
            ExpenditureTagEntity(key = "maintenance", name = "维修"),
            ExpenditureTagEntity(key = "donations", name = "捐赠"),
            ExpenditureTagEntity(key = "lottery", name = "彩票"),
            ExpenditureTagEntity(key = "relatives_and_friends", name = "亲友"),
            ExpenditureTagEntity(key = "express_delivery", name = "快递"),
            ExpenditureTagEntity(key = "bills", name = "账单"),
        )
        expenditureTagDao.insertAll(tags)
        initiatedFlagHolder.setExpenditureTagsInitiated()
    }
}