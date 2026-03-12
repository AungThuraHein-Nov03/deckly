package com.aungthurahein.deckly.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object DailyJournalScheduler {

    private const val WORK_NAME = "daily_journal_rollover"

    fun schedule(context: Context) {
        val now = LocalDateTime.now()
        val nextMidnight = LocalDate.now().plusDays(1).atStartOfDay()
        val delay = Duration.between(now, nextMidnight)

        val request = PeriodicWorkRequestBuilder<DailyJournalWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
