package com.non.k4r.module.expenditure.model

import com.non.k4r.core.data.database.model.ExpenditureRecordWithTags
import com.non.k4r.module.common.model.RecordMainScreenVO

class ExpenditureRecordMainScreenVO : RecordMainScreenVO() {
    var expenditureWithTags: ExpenditureRecordWithTags? = null
}
