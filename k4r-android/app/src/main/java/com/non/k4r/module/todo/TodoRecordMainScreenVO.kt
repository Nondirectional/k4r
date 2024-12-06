package com.non.k4r.module.todo

import com.non.k4r.core.data.database.model.TodoRecord
import com.non.k4r.module.common.model.RecordMainScreenVO

class TodoRecordMainScreenVO : RecordMainScreenVO() {
    var todoRecord: TodoRecord? = null
}