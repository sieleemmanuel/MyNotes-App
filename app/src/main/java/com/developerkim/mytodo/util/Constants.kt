package com.developerkim.mytodo.util

import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory

class Constants {
    companion object{
        const val CHANNEL_ID = "reminder_channel_id"
        const val NOTIFICATION_ID = 1
        private val noteList = mutableListOf(Note(
            "Study",
            "Study Note Title",
            "This is study note content and description and text",
            "06/06/2022 4:41PM",
            false,
            "None"

        ),
                Note(
                    "Study",
                    "Study01 Note Title",
                    "This is study01 note content and description and text",
                    "04/06/2022 4:41PM",
                    true,
                    "None"
                ),
            Note(
                "Study",
                "Study02 Note Title",
                "This is study01 note content and description and text",
                "05/06/2022 4:41PM",
                true,
                "None"
            ))
        private val noteList01 = mutableListOf(Note(
            "Daily Tasks",
            "Daily Task Note Title",
            "This is Daily Task note content and description and text",
            "06/06/2022 4:41PM",
            false,
            "None"
        ),
                Note(
                    "Daily Tasks",
                    "Daily Task01 Note Title",
                    "This is Daily Task01 note content and description and text",
                    "04/06/2022 4:41PM",
                    true,
                    "None"
                ),
            Note(
                "Daily Tasks",
                "Daily Task02 Note Title",
                "This is Daily Task01 note content and description and text",
                "05/06/2022 4:41PM",
                true,
                "None"
            ))
        val noteCategories = listOf(
            NoteCategory("Study", noteList),
            NoteCategory("Daily Tasks", noteList01)

        )

    }
}