package com.developerkim.mytodo.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NoteReminderWorker(workerParameters: WorkerParameters, val context: Context):Worker(context, workerParameters) {
    override fun doWork(): Result {
       NotificationHelper(context).createNotification(
           inputData.getString("title").toString(),
           inputData.getString("message").toString(),
           inputData.getString("noteTitle").toString()
       )
        return Result.success()
    }
}