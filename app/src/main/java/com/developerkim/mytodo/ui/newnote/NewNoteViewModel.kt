package com.developerkim.mytodo.ui.newnote

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.developerkim.mytodo.NotesApp
import com.developerkim.mytodo.R
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.model.Note
import com.developerkim.mytodo.data.model.NoteCategory
import com.developerkim.mytodo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class NewNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    val noteCategories: Array<String> = context.resources.getStringArray(R.array.notes_categories)
    val notePriorities: Array<String> = context.resources.getStringArray(R.array.note_priorities)


    private val category = MutableLiveData<NoteCategory>()

    private val currentDateTime: LocalDateTime = LocalDateTime.now()
    val noteDate: String = currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    private val _noteCategory = MutableLiveData<String>()
    val noteCategory: LiveData<String>
        get() = _noteCategory

    fun updateIfExist(note: Note, noteCategory: NoteCategory) {
        viewModelScope.launch {
            if (notesRepository.categoryExists(noteCategory.categoryName)) {
                updateCategoryNotes(note, noteCategory)
            } else {
                insertCategoriesWithNotes(noteCategory)
            }
        }

    }

    private fun insertCategoriesWithNotes(newCategory: NoteCategory) {
        viewModelScope.launch {
            notesRepository.insert(newCategory)
        }
    }

    private fun updateCategoryNotes(note: Note, newCategory: NoteCategory) {
        viewModelScope.launch {
            category.value = notesRepository.getCategory(newCategory.categoryName)
            val categoryToUpdate = category.value
            val categoryNotes = categoryToUpdate?.notes
            categoryNotes?.addAll(listOf(note))
            categoryToUpdate?.notes = categoryNotes
            categoryToUpdate?.categoryName = note.noteCategory
            categoryToUpdate?.let { notesRepository.update(it) }
        }
    }
    fun createNoteList(note: Note): ArrayList<Note> {
        val noteList = ArrayList<Note>()
        noteList.add(note)
        return noteList
    }
}
