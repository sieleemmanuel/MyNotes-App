package com.developerkim.mytodo.ui.listnotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.database.NoteCategoriesDao
import com.developerkim.mytodo.model.Note
import com.developerkim.mytodo.model.NoteCategory
import kotlinx.coroutines.launch

class ListNoteViewModel(
    private val dataSource: NoteCategoriesDao
) : ViewModel() {
    var noteCategory: NoteCategory = NoteCategory()
    val categoriesList = dataSource.getAllNoteCategories()
    val privateHiddenCategories = dataSource.getCategoriesPrivateHidden()


    private suspend fun deleteAllCategories() {
        dataSource.clearAllCategories()
    }

    private suspend fun deleteCategory(noteCategory: NoteCategory) {
        dataSource.deleteCategory(noteCategory.categoryName)
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

    private val _navigateToReadNote = MutableLiveData<Boolean>()
    val navigateToReadNote:LiveData<Boolean>
        get() = _navigateToReadNote

    private val _navigateToNewNote = MutableLiveData<Boolean>()
    val navigateToNewNote:LiveData<Boolean>
        get() = _navigateToNewNote
    fun navigateToNewNote(){
        _navigateToNewNote.value = true
    }
    fun newNoteNavigated(){
        _navigateToNewNote.value = false
    }

    private suspend fun update(noteCategory: NoteCategory) {
        dataSource.update(noteCategory)
    }
    private suspend fun getCategoryToUpdate(categoryName: String): NoteCategory {
        return dataSource.getCategory(categoryName)
    }

    fun updateNote(note: Note,notePosition:Int) {
        viewModelScope.launch {
            val toUpdateCategory = getCategoryToUpdate(note.noteCategory)
            toUpdateCategory.notes?.removeAt(notePosition)
            update(toUpdateCategory)
        }

    }

    fun navigateOnNoteClicked() {
        _navigateToReadNote.value = true

    }

    fun onReadNoteNavigated() {
        _navigateToReadNote.value = false
    }


}
