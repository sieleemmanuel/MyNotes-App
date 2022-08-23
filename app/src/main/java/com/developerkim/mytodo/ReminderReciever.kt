package com.developerkim.mytodo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.developerkim.mytodo.util.NotificationHelper

class ReminderReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val notificationTitle = bundle?.getString(context?.getString(R.string.title_key))
        val message = bundle?.getString(context?.getString(R.string.message_key))
        val noteTitle = bundle?.getString(context?.getString(R.string.note_title_arg_key))
        val noteCategory = bundle?.getString(context?.getString(R.string.note_category_arg_key))

        NotificationHelper(context!!).createNotification(
            title = notificationTitle!!,
            message = message!!,
            noteTitle = noteTitle!!,
            noteCategory = noteCategory!!)
    }
}