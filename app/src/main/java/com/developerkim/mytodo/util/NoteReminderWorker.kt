package com.developerkim.mytodo.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.developerkim.mytodo.R

class NoteReminderWorker(val context: Context, workerParameters: WorkerParameters):Worker(context, workerParameters) {
    override fun doWork(): Result {
       NotificationHelper(context).createNotification(
           inputData.getString(context.getString(R.string.title_key))!!,
           inputData.getString(context.getString(R.string.message_key))!!,
           inputData.getString(context.getString(R.string.note_title_arg_key))!!,
           inputData.getString(context.getString(R.string.note_category_arg_key))!!
       )
        return Result.success()
    }
}