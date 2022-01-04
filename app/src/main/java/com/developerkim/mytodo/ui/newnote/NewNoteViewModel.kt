package com.developerkim.mytodo.ui.newnote

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
class NewNoteViewModel(private val categoryDatabase: NoteCategoriesDao, application: Application) :
    AndroidViewModel(application) {


    val categoryItems = application.resources.getStringArray(R.array.notes_categories)

    val category = MutableLiveData<NoteCategory>()

    private val currentDateTime: LocalDateTime = LocalDateTime.now()
    val noteDate = currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    val _noteCategory = MutableLiveData<String>()
    val noteCategory: LiveData<String>
        get() = _noteCategory


    val _noteText = MutableLiveData<String>()
    val noteText: LiveData<String>
        get() = _noteText

    private val _navigateToListNotes = MutableLiveData<Boolean?>()
    val navigateToListNote: LiveData<Boolean?>
        get() = _navigateToListNotes

    private suspend fun insert(noteCategory: NoteCategory) {
        categoryDatabase.insert(noteCategory)
    }

    private suspend fun getCategory(categoryName: String): NoteCategory {
        return categoryDatabase.getCategory(categoryName)
    }

    private suspend fun categoryExists(categoryName: String): Boolean {
        return categoryDatabase.isCategoryExists(categoryName)
    }

    private suspend fun update(noteCategory: NoteCategory) {
        categoryDatabase.update(noteCategory)
    }

    fun updateIfExist(note: Note, noteCategory: NoteCategory) {
        viewModelScope.launch {
            if (categoryExists(noteCategory.categoryName)) {
                updateCategoryNotes(note, noteCategory)
            } else {
                insertCategoriesWithNotes(noteCategory)
            }
        }

    }

    private fun insertCategoriesWithNotes(newCategory: NoteCategory) {
        viewModelScope.launch {
            insert(newCategory)
        }
    }

    private fun updateCategoryNotes(note: Note, newCategory: NoteCategory) {
        viewModelScope.launch {
            category.value = getCategory(newCategory.categoryName)
            val categoryToUpdate = category.value
            val categoryNotes = categoryToUpdate?.notes
            categoryNotes?.addAll(listOf(note))
            categoryToUpdate?.notes = categoryNotes
            categoryToUpdate?.categoryName = note.noteCategory
            categoryToUpdate?.let { update(it) }
        }
    }
    fun createNoteList(note: Note): ArrayList<Note> {
        val noteList = ArrayList<Note>()
        noteList.add(note)
        return noteList
    }
}
