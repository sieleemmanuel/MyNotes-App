package com.developerkim.mytodo.ui.listnotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import kotlinx.coroutines.launch

class ListNoteViewModel(
    private val dataSource: NoteDatabase
) : ViewModel() {
    private val notesRepository = NotesRepository(dataSource)

    private val _categories = MutableLiveData<List<NoteCategory>>()
    val categoriesList:LiveData<List<NoteCategory>> = _categories

    init {
        notesRepository.privateHiddenNotes.observeForever{
            _categories.value =it
        }

    }


    private suspend fun deleteAllCategories() {
        dataSource.notesCategoriesDao.clearAllCategories()
    }

    private suspend fun deleteCategory(noteCategory: NoteCategory) {
        dataSource.notesCategoriesDao.deleteCategory(noteCategory.categoryName)
    }

    fun clearAllCategories() {
        viewModelScope.launch {
            deleteAllCategories()
        }
    }

    fun clearCategories(noteCategory: NoteCategory) {
        viewModelScope.launch {
            deleteCategory(noteCategory)
        }
    }

    private suspend fun update(noteCategory: NoteCategory) {
        dataSource.notesCategoriesDao.update(noteCategory)
    }
    private suspend fun getCategoryToUpdate(categoryName: String): NoteCategory {
        return dataSource.notesCategoriesDao.getCategory(categoryName)
    }

    fun updateNote(note: Note,notePosition:Int) {
        viewModelScope.launch {
            val toUpdateCategory = getCategoryToUpdate(note.noteCategory)
            toUpdateCategory.notes?.removeAt(notePosition)
            update(toUpdateCategory)
        }

    }

    fun showAllCategories(){
        notesRepository.categoryNoteList.observeForever {
            _categories.value = it
        }
    }
    fun hidePrivateCategories(){
        notesRepository.privateHiddenNotes.observeForever{
            _categories.value = it
        }
    }


}
