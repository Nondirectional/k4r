package com.non.k4r

import android.app.Application
import com.non.k4r.module.expenditure.dao.ExpenditureTagDao
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class K4rApplication: Application() {
    lateinit var expenditureTagDao: ExpenditureTagDao
    override fun onCreate() {

        super.onCreate()
        expenditureTagDao = Hilt    .init(this) { provideUserDao() }
    }
}