package com.developerkim.mytodo.data.repository

import com.developerkim.mytodo.data.database.NoteDatabase

class NotesRepository(database: NoteDatabase) {

    val categoryNoteList = database.notesCategoriesDao.getAllNoteCategories()
    val privateHiddenNotes = database.notesCategoriesDao.getCategoriesPrivateHidden()

}