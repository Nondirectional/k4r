package com.non.k4r.core.holder;

import android.content.Context;
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

import dagger.hilt.android.qualifiers.ApplicationContext;

class InitiatedFlagHolder(
    @ApplicationContext private val context: Context
) {
    private val initiatedFlagPreferences: SharedPreferences = context.getSharedPreferences(
        "initiated_flag",
        MODE_PRIVATE
    )

    fun isExpenditureTagsInitiated(): Boolean {
        return initiatedFlagPreferences.getBoolean("expenditure_tags_initiated", false)
    }

    fun setExpenditureTagsInitiated() {
        initiatedFlagPreferences.edit().putBoolean("expenditure_tags_initiated", true).apply()
    }
}