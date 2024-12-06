package com.non.k4r.module.common.model

import com.non.k4r.core.data.database.constant.RecordType
import java.time.LocalDateTime

open class RecordMainScreenVO {
    var id: Long? = null
    var type: RecordType? = null
    var recordTime: LocalDateTime? = null
}
