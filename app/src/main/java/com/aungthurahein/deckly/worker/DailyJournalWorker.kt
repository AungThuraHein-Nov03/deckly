package com.aungthurahein.deckly.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aungthurahein.deckly.data.DailyJournalStore

class DailyJournalWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            DailyJournalStore.archiveIfDayEnded(applicationContext)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
