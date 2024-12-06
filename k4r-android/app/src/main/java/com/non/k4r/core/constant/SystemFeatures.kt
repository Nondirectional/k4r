package com.non.k4r.core.constant

import com.non.k4r.R

enum class SystemFeatures() {
    Expenditure {
        override fun featureName(): String = "开支"
        override fun iconResId(): Int = R.drawable.baseline_payments_24
    },
    Todo {
        override fun featureName(): String = "待办"
        override fun iconResId(): Int = R.drawable.baseline_done_24
    };

    abstract fun featureName(): String

    abstract fun iconResId(): Int
}