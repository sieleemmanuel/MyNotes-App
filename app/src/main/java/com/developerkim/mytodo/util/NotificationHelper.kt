package com.developerkim.mytodo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.util.Constants.Companion.CHANNEL_ID
import com.developerkim.mytodo.util.Constants.Companion.NOTIFICATION_ID

class NotificationHelper(val context: Context) {
    fun createNotification(title:String, message:String, noteTitle:String, noteCategory: String){
        createNotificationChannel()
        val args = Bundle()
        args.putString(context.getString(R.string.note_title_arg_key), noteTitle)
        args.putString(context.getString(R.string.note_category_arg_key), noteCategory)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.readNotesFragment)
            .setArguments(args)
            .createPendingIntent()
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Note Reminder"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}