package com.developerkim.mytodo.ui.listnotes

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

   /* private val _filteredList = MutableLiveData<List<NoteCategory>>()
    val filteredList: LiveData<List<NoteCategory>>
        get()= _filteredList

    fun filteredList(){
        _filteredList.value = categoriesList.value?.filter { it.categoryName != "private" }
    }*/

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

    private val _navigateToReadNote = MutableLiveData<Note>()
    val navigateToReadNote
        get() = _navigateToReadNote

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

    fun onNoteClicked(note: Note) {
        _navigateToReadNote.value = note

    }

    fun onReadNoteNavigated() {
        _navigateToReadNote.value = null
    }


}
