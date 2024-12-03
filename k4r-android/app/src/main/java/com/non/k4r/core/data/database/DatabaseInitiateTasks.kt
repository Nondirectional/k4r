package com.non.k4r.core.data.database

import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.model.ExpenditureTag
import com.non.k4r.core.holder.InitiatedFlagHolder


suspend fun initExpenditureTags(
    initiatedFlagHolder: InitiatedFlagHolder,
    expenditureTagDao: ExpenditureTagDao
) {
    if (!initiatedFlagHolder.isExpenditureTagsInitiated()) {
        val tags = listOf<ExpenditureTag>(
            ExpenditureTag(key = "catering", name = "餐饮"),
            ExpenditureTag(key = "shopping", name = "购物"),
            ExpenditureTag(key = "daily_necessities", name = "日用"),
            ExpenditureTag(key = "transportation", name = "交通"),
            ExpenditureTag(key = "vegetables", name = "蔬菜"),
            ExpenditureTag(key = "fruits", name = "水果"),
            ExpenditureTag(key = "snacks", name = "零食"),
            ExpenditureTag(key = "sports", name = "运动"),
            ExpenditureTag(key = "entertainment", name = "娱乐"),
            ExpenditureTag(key = "communications", name = "通讯"),
            ExpenditureTag(key = "clothing", name = "服饰"),
            ExpenditureTag(key = "beauty", name = "美容"),
            ExpenditureTag(key = "housing", name = "住房"),
            ExpenditureTag(key = "home", name = "居家"),
            ExpenditureTag(key = "children", name = "孩子"),
            ExpenditureTag(key = "elders", name = "长辈"),
            ExpenditureTag(key = "social", name = "社交"),
            ExpenditureTag(key = "travel", name = "旅行"),
            ExpenditureTag(key = "tobacco_and_alcohol", name = "烟酒"),
            ExpenditureTag(key = "digital", name = "数码"),
            ExpenditureTag(key = "cars", name = "汽车"),
            ExpenditureTag(key = "medical", name = "医疗"),
            ExpenditureTag(key = "books", name = "书籍"),
            ExpenditureTag(key = "study", name = "学习"),
            ExpenditureTag(key = "pets", name = "宠物"),
            ExpenditureTag(key = "money_gift", name = "礼金"),
            ExpenditureTag(key = "physical_gift", name = "礼物"),
            ExpenditureTag(key = "office", name = "办公"),
            ExpenditureTag(key = "maintenance", name = "维修"),
            ExpenditureTag(key = "donations", name = "捐赠"),
            ExpenditureTag(key = "lottery", name = "彩票"),
            ExpenditureTag(key = "relatives_and_friends", name = "亲友"),
            ExpenditureTag(key = "express_delivery", name = "快递"),
            ExpenditureTag(key = "bills", name = "账单"),
        )
        expenditureTagDao.insertAll(tags)
        initiatedFlagHolder.setExpenditureTagsInitiated()
    }
}