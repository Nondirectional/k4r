package com.non.k4r

import android.app.Application
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.initExpenditureTags
import com.non.k4r.core.holder.InitiatedFlagHolder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class K4rApplication : Application() {
    @Inject
    lateinit var initiatedFlagHolder: InitiatedFlagHolder

    @Inject
    lateinit var expenditureTagDao: ExpenditureTagDao

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            // Do some initialization work here
            initExpenditureTags(initiatedFlagHolder, expenditureTagDao)
        }
    }
}