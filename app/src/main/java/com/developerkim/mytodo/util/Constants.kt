package com.developerkim.mytodo.util

class Constants {
    companion object {
        const val CHANNEL_ID = "reminder_channel_id"
        val NOTIFICATION_ID = System.currentTimeMillis().toInt()
        val RECEIVER_CODE = System.currentTimeMillis().toInt()
    }
}