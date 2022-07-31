package com.developerkim.mytodo.data.repository

import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.util.Constants
import javax.inject.Inject

class NotesRepository @Inject constructor(private val notesDao: NoteCategoriesDao) {
    suspend fun getAllNotes()= notesDao.getAllNoteCategories()
    suspend fun getAllNotesPrivateHidden() = notesDao.getCategoriesPrivateHidden()
    suspend fun deleteAllCategories() = notesDao.clearAllCategories()
    suspend fun deleteCategory(noteCategory: NoteCategory) = notesDao.deleteCategory(noteCategory.categoryName)
    suspend fun updateCategory(noteCategory: NoteCategory) = notesDao.update(noteCategory)
    suspend fun getCategoryToUpdate(categoryName: String)= notesDao.getCategory(categoryName)
    suspend fun insert(noteCategory: NoteCategory)=  notesDao.insert(noteCategory)
    suspend fun getCategory(categoryName: String)= notesDao.getCategory(categoryName)
    suspend fun categoryExists(categoryName: String)= notesDao.isCategoryExists(categoryName)


}