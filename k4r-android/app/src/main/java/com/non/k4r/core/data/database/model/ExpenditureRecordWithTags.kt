package com.non.k4r.core.data.database.model;

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExpenditureRecordWithTags(
    @Embedded val expenditureRecord: ExpenditureRecord,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ExpenditureRecordTag::class)
    )
    var tags: List<ExpenditureTag>
)