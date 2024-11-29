package com.non.k4r.module.common;

import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity
import kotlinx.serialization.Serializable;

@Serializable
object MainRoute

@Serializable
data class ExpenditureSubmitRoute(val expenditureTags:List<ExpenditureTagEntity>)
